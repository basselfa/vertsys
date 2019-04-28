package de.tu_berlin.cit;

//
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.ServerSocketChannel;
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.UnknownHostException;
//import java.nio.ByteBuffer;
//import java.nio.CharBuffer;
//import java.nio.channels.SocketChannel;
//import java.nio.charset.CharacterCodingException;
//import java.nio.charset.Charset;
//import java.nio.charset.CharsetDecoder;
//import java.nio.charset.UnsupportedCharsetException;
//import java.util.Iterator;
//import java.util.Set;
//import java.io.File;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.Files;
//import java.util.List;
//import java.util.ArrayList;
//import java.lang.Object;
//
////Imports for file operations
//import java.io.FileOutputStream;
//import java.nio.channels.FileChannel;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.concurrent.TimeUnit;
//
//
//
//public class SMTPServer {
//	
//	
//    //###################################################################
//    //#########################  constants  #############################
//    //###################################################################
//    // IMPORTANT!!! IF YOU CHANGE ANY OF THESE VALUES THEN CHANGE THEM IN ALL FILES.
//    private static final String TERMINATOR = "\r\n.\r\n";
//    private static final boolean COMPLETE = true;
//
//    public static final String [] theHailMaryArray = {"HELO", "MAIL FROM:", "RCPT TO:", "DATA", "QUIT", "HELP"};
//
//    public static final int NEW = 0;
//    public static final int HELO = 1;
//    public static final int MAILFROM = 2;
//    public static final int RCPTO = 3;
//    public static final int DATA = 4;
//    public static final int QUIT = 5;
//    public static final int HELP = 6;
//
//    public static final int MESS = 7;
//    public static final int ERROR = 8;
//
//    //###################################################################
//
//
//    
//	
//	private static byte [] ackMsg = null;
//	private static Charset messageCharset = null;
//	private static CharsetDecoder decoder = null;
//
//	
//	//modus = 0: initinal, HELO, MAIL FROM, RCPT TO, <mail bestätigung>. modus = 1: DATA. modus = 2: QUIT. modus = 3: HELP
//	private static void sendMessages(int modus,SocketChannel clientChannel) throws IOException
//	{	switch(modus){
//			case 0: {	ackMsg = new String("220 OK \r\n").getBytes(messageCharset); break ;	}	//init
//			case 1: {	ackMsg = new String("250 OK \r\n").getBytes(messageCharset); break;	}	//helo
//			case 2: {	ackMsg = new String("250 start mail input \r\n").getBytes(messageCharset); break;	}	//mailFrom
//			case 3: {	ackMsg = new String("250 closing channel \r\n").getBytes(messageCharset);	break; }	//rcptTo
//			case 4: {	ackMsg = new String("354 \r\n").getBytes(messageCharset); break;	}	//data
//			case 5: {	ackMsg = new String("221 OK \r\n").getBytes(messageCharset); break;	}	//quit
//			case 6: {	ackMsg = new String("214 OK \r\n").getBytes(messageCharset); break;	} //help
//			case 7: {	ackMsg = new String("250 OK \r\n").getBytes(messageCharset); break;	} //massage
//		
//		}
//		sendAck(clientChannel);
//
//	}
//
//	//send ack back to client
//	private static void sendAck(SocketChannel clientChannel) throws IOException {
//
//		System.out.println("Sending ack "+ (new String (ackMsg,messageCharset)));
//		
//		ByteBuffer buf =ByteBuffer.allocate(ackMsg.length);
//		
//		buf.put(ackMsg);
//		//buf = ByteBuffer.wrap(ackMsg);
//		buf.flip();
//		try {
//			TimeUnit.SECONDS.sleep(1);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
//	//	System.out.println("Sending ack "+buf.);
//		clientChannel.write(buf);
//		
//		buf.clear();
//	}
//
//	private static String createRandomNumber(){
//		//erstellt zufällige nummer
//		int randomNum = ThreadLocalRandom.current().nextInt(0, 9999 + 1);
//		//umwandeln in 4-stelligen string mit führenden nullen
//		String formatted = String.format("%04d", randomNum);
//		return formatted;
//
//	}
//
//	
//	private static void writeToFile(EmailStruct emailStruct) throws IOException{
//	
//		// if  Reciever Folder already exists then Write in it 
//		// TODO : replace valid path
//		Path path = Paths.get("/home/users/b/basselfa/irb-ubuntu/emails/"+emailStruct.getReceiver()+"/");
//
//		File newFile = null;
//		
//		if (Files.notExists(path))
//		{
//			newFile = new File("/home/users/b/basselfa/irb-ubuntu/emails/"+emailStruct.getReceiver()+"/"); 
//			newFile.mkdirs();
//
//		}
//		//creates random id in string format
//		String massage_id = createRandomNumber();
//		
//		// create sender File 
//		FileOutputStream f = new FileOutputStream("/home/users/b/basselfa/irb-ubuntu/emails/"+emailStruct.getReceiver()+"/"+ emailStruct.getSender()+"_"+massage_id);
//		FileChannel channel = f.getChannel();
//		
//	
//		//create and put Massage in ByteBuffer
//		ByteBuffer buf = ByteBuffer.allocate(emailStruct.getData().getBytes(messageCharset).length);
//		buf.put(emailStruct.getData().getBytes(messageCharset));
//		// change to writing Modus
//		buf.flip();
//
//	
//		//write to  File and close it afterwards 
//		while (buf.hasRemaining())
//		{
//			channel.write(buf);
//	    }
//		channel.close();
//		buf.clear();	
//		f.close();
//	
//
//	 }
//
//	 
//
//	
//	public static void main(String[] args) {
//		System.out.println("I got in");
//
//		// TODO 1.selector and  Socket
//		Selector selector = null ;
//		ServerSocketChannel servSock = null;
//		
//		// Initialize Char -> Bytes Format
//		try {
//			messageCharset = Charset.forName("US-ASCII");
//		} catch(UnsupportedCharsetException uce) {
//			System.err.println("Cannot create charset for this application. Exiting...");
//			System.exit(1);
//		}
//		
//		
//		// Initialize Bytes -> Char Format
//		decoder = messageCharset.newDecoder();
//
//
//		// TODO 2. create channel
//		try {
//			
//			
//			
//		
//			selector = Selector.open();
//			
//			servSock = ServerSocketChannel.open();
//			servSock.configureBlocking(false);
//			servSock.bind(new InetSocketAddress("localhost",59031)); //CHANGE
//			
//			servSock.register(selector, SelectionKey.OP_ACCEPT);
//		
//			
//			//  Initialize Ack (HELO)
//		
//			// TODO 3. iterate to new selector_key
//			while(true){
//				if(selector.select() == 0)
//					continue;
//		
//				Set<SelectionKey> selectedKeys = selector.selectedKeys();
//				Iterator<SelectionKey> iter = selectedKeys.iterator();
//
//				
//				while(iter.hasNext()) {
//					SelectionKey key = iter.next();
//				
//
//
//					//has to be dynamic
//					// TODO 4. identify flags and set state
//					
//					if (key.isAcceptable()) {
//						System.out.println("in Accepted");
//						ServerSocketChannel sock = (ServerSocketChannel) key.channel();
//						SocketChannel client = sock.accept();
//						client.configureBlocking(false);
//						client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, new emailClassController()); //If the att argument is not null then the key's attachment will have been set to that value. 
//						// initialize controller class
//						
//						
//						System.out.println("out Accepted");
//					
//					}
//
//					// TODO 5. parse message
//					if (key.isReadable()) {
//					// receive the buffer
//						System.out.println("in Readable");
//						ByteBuffer bytebuf =ByteBuffer.allocate(1024);
//						SocketChannel channel = (SocketChannel) key.channel();
//						channel.read(bytebuf);
//						bytebuf.flip();
//						
//						CharBuffer charbuf= decoder.decode(bytebuf);
//						String inputString = charbuf.toString();
//						
//						//NECESSARY
//					
//						System.out.println("************************************************************");
//						System.out.println("what we received from the client: "+inputString);
//						emailClassController myEmails = (emailClassController) key.attachment();
//						// HIER SOLL PARSE INPUT GERUFEN WERDEN
//						if(myEmails.getLastMessage() == null) System.out.println("struct is zero : ");
//						
//						myEmails = emailClassController.parseInput(myEmails, inputString);
//
//						key.attach(myEmails);
//
//						System.out.println("message completely sent ="+myEmails.getComplete());
//
//						//  Update files :if Connection is done and Closed then write everything to the Files
//						if(myEmails.getComplete() == true)
//						{
//							System.out.println("Complete.");
//							// auskommentiert wegen Refaktorisierung
//							// EmailStruct[] emailStructArray = (EmailStruct []) key.attachment();
//							// for (EmailStruct tmp :emailStructArray) {
//							// 	writeToFile(tmp);
//							// }
//
//							myEmails = ((emailClassController) key.attachment());
//							
//							EmailStruct tmp = myEmails.popLastMessage();
//							while(tmp != null){
//								writeToFile(tmp);
//								tmp = myEmails.popLastMessage();
//							}
//							sendMessages(QUIT,(SocketChannel) key.channel());
//								key.interestOps(SelectionKey.OP_READ);
//								System.out.println("Closing Connection");
//								try {
//    								TimeUnit.SECONDS.sleep(5);
//    							} catch (InterruptedException e) {
//    								// TODO Auto-generated catch block
//    								e.printStackTrace();
//    							}	
//								key.cancel();
//
//    						
//							
//    							bytebuf.clear(); 
//						
//						}
//						//INT MODUS is returned here and used for sendMessage
//
//					// TODO 7. if valid SEND ACK OR ANSWER HELP
//					}
//						
//
//					if (key.isWritable()&& key.attachment()!=null){	
//						System.out.println("in Writable");
//
//
//						emailClassController myEmails = (emailClassController) key.attachment();
//						EmailStruct emailStruct = myEmails.getLastMessage();
//						if(emailStruct == null){
//							break;
//						}
//
//						int modus = myEmails.getLastState();
//						
//						
//						if(myEmails.getHelpFlag() == true){
//							modus = 6 ;
//							myEmails.deactivateHelpFlag();
//							System.out.println("SET HELP modus");
//						}
////						
////						if(myEmails.getComplete() == true){
////							modus = QUIT ;
////						
////						}
//						
//						
//						// if ack already sent then skip
//						if (myEmails.getAckFlag()== true)
//							continue;
//						System.out.println("modus ="+ modus);
//						sendMessages(modus,(SocketChannel) key.channel());
//						myEmails.setAckFlag();
//
//
//						// Update emailClass
//						myEmails.setLast(emailStruct);
//						// Update attachment 
//						key.attach(myEmails);
//					}					
//			
//					iter.remove();
//				}
//				
//			}
//
//		
//		}catch(IOException e) {
//				e.printStackTrace();
//				System.exit(1);
//			
//		}
//	}


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
			InetSocketAddress socketAdd = new InetSocketAddress(59031);
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
//}
