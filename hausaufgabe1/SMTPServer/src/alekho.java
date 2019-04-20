
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class SMTPServer {

	private ServerSocketChannel serverSocket;
	private InetSocketAddress addrSock;
	private List<SMTPServerState> recievedMails;
//	private SMTPServerState state;

	public SMTPServer() {
		try {
			InetSocketAddress socketAdd = new InetSocketAddress(6332);
			this.addrSock = socketAdd;
			recievedMails = new ArrayList<SMTPServerState>();

		} catch (Exception e) {
			throw new RuntimeException("Error creating address for socket.");
		}
	}

	public void startServer() {
		try {
			Selector selector = Selector.open();

			this.serverSocket = ServerSocketChannel.open();
			this.serverSocket.configureBlocking(false);
			this.serverSocket.bind(this.addrSock);

			this.serverSocket.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("Server running...");

			while (true) {
				if (selector.select() == 0) /* blocking */
					continue;

				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iter = selectedKeys.iterator();

				while (iter.hasNext()) {
					SelectionKey key = iter.next();

					if (key.isAcceptable()) {
						acceptKey(key, selector);
					}
					if (key.isReadable()) {
						readKey(key);
					}
					if (key.isWritable()) {
						writeKey(key);
					}
					iter.remove();
				}
			}

		} catch (Exception e) {
			throw new RuntimeException("Unable to start server.");
		}

	}

	private void acceptKey(SelectionKey key, Selector selector) throws IOException {
		ServerSocketChannel sock = (ServerSocketChannel) key.channel();
		SocketChannel client = sock.accept();
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, new SMTPServerState());
//		this.state = new SMTPServerState();

	}

	private void readKey(SelectionKey key) throws IOException {

		String s = readChannel(key);
		handleCommands(key, s);
		 	
		System.out.println(s);
	}

	private void writeKey(SelectionKey key) throws IOException {
		String response = "";
		boolean canQuit = false;
		SMTPServerState state = (SMTPServerState) key.attachment();

		switch (state.getState()) {
		case SMTPServerState.CONNECTED:
			response = "220 Hi there! We are connected :) \r\n";
			break;
		case SMTPServerState.HELO:
			response = "250 HELO OK \r\n";
			break;
		case SMTPServerState.MAILFROM:
			response = "250 MAILFROM OK \r\n";
			break;
		case SMTPServerState.RCPTTO:
			response = "250 RCPT OK \r\n";
			break;
		case SMTPServerState.DATA:
			response = "354 DATA OK \r\n";
			break;
		case SMTPServerState.MESSAGE:
			response = "250 MESSAGE OK \r\n";
			break;
		case SMTPServerState.HELP:
			response = "214 Call 911! \r\n";
			break;
		case SMTPServerState.QUIT:
			canQuit = true;
			response = "221 Goodbye! \r\n";
			break;
		default:
			System.out.println("Incorrect state, an error ocurred.");
			return;
		}

		byte[] b = response.getBytes(StandardCharsets.US_ASCII);
		ByteBuffer buf = ByteBuffer.allocate(b.length);
		buf.put(b);
		buf.flip();
		SocketChannel channel = (SocketChannel) key.channel();

		while (buf.hasRemaining()) {
			channel.write(buf);
		}

		buf.clear();
		key.interestOps(SelectionKey.OP_READ);

		if (canQuit) {
			key.cancel();
			key.channel().close();
		}
	}

	private void handleCommands(SelectionKey key, String s) throws IOException {

		SMTPServerState state = (SMTPServerState) key.attachment();
		if (state.getState() == SMTPServerState.DATA) { // *Message case*//
			if(s.endsWith("\r\n.\r\n")){
				state.setPreviousState(state.getState());
				state.setState(SMTPServerState.MESSAGE);
				String newData = state.getData() + s.split("\r\n.\r\n")[0];
				state.setData(newData);
				key.interestOps(SelectionKey.OP_WRITE);
				return;
			}
			String newData = state.getData() + s;
			state.setData(newData);

		} else if (s.startsWith("HELO")) {
			state.setPreviousState(state.getState());
			state.setState(SMTPServerState.HELO);
		} else if (s.startsWith("MAIL")) {
			state.setPreviousState(state.getState());
			state.setState(SMTPServerState.MAILFROM);
			parseSender(state, s);

		} else if (s.startsWith("RCPT")) {
			state.setPreviousState(state.getState());
			state.setState(SMTPServerState.RCPTTO);
			parseRecipient(state,s);

		} else if (s.startsWith("DATA")) {
			state.setPreviousState(state.getState());
			state.setState(SMTPServerState.DATA);

		} else if (s.startsWith("HELP")) {
			state.setPreviousState(state.getState());
			state.setState(SMTPServerState.HELP);

		} else if (s.startsWith("QUIT")) {
			state.setPreviousState(state.getState());
			state.setState(SMTPServerState.QUIT);
			addAndSaveMail(state);
		}
		
		key.interestOps(SelectionKey.OP_WRITE);
	}

	private void parseRecipient(SMTPServerState state, String s) {
		String temp [] = s.split(":");
		String recipient = temp[1].trim();
		
		state.setTo(recipient);	
	}

	private void parseSender(SMTPServerState state, String s) {
		String temp [] = s.split(":");
		String sender = temp[1].trim();
		
		state.setFrom(sender);
	}

	private void addAndSaveMail(SMTPServerState state) throws IOException {
		int random = getRandomNumber();
		byte[] data = state.getData().getBytes(StandardCharsets.US_ASCII);

		Files.createDirectories(Paths.get(state.getTo()));
		String p = ".\\" + state.getTo()+"\\" + state.getFrom() +"_" + random+ ".txt";
		Path path = Paths.get(p);
		Files.write(path, data);
		recievedMails.add(state);

	}

	private int getRandomNumber() {
		Random rand = new Random();
		int r = rand.nextInt(10000);
		return r;
	}

	private String readChannel(SelectionKey key) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		SocketChannel channel = (SocketChannel) key.channel();
		channel.read(buf);
		buf.flip();

		Charset messageCharset = Charset.forName("US-ASCII");
		CharsetDecoder decoder = messageCharset.newDecoder();
		CharBuffer charBuf = null;
		try {
			charBuf = decoder.decode(buf);
		} catch (CharacterCodingException e) {
			System.out.println("Error decoding the buffer.");
		}

		buf.clear();
		String s = charBuf.toString();

		return s;
	}

	public static void main(String[] args) {

		SMTPServer server = new SMTPServer();
		server.startServer();
	}

}