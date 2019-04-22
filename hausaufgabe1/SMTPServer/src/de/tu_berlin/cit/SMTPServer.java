package de.tu_berlin.cit;


import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Iterator;
import java.util.Set;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;

//Imports for file operations
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.concurrent.ThreadLocalRandom;


public class SMTPServer {
	
	private static byte [] ackMsg = null;
	private static Charset messageCharset = null;
	private static CharsetDecoder decoder = null;

	
	//modus = 0: initinal, HELO, MAIL FROM, RCPT TO, <mail bestätigung>. modus = 1: DATA. modus = 2: QUIT. modus = 3: HELP
	private static void sendMessages(int modus,SocketChannel clientChannel) throws IOException
	{	switch(modus){
			case 0: {	ackMsg = new String("220 OK \r\n").getBytes(messageCharset);	}	//init
			case 1: {	ackMsg = new String("250 OK \r\n").getBytes(messageCharset);	}	//helo
			case 2: {	ackMsg = new String("250 start mail input \r\n").getBytes(messageCharset);	}	//mailFrom
			case 3: {	ackMsg = new String("250 closing channel \r\n").getBytes(messageCharset);	}	//rcptTo
			case 4: {	ackMsg = new String("354 \r\n").getBytes(messageCharset);	}	//data
			case 5: {	ackMsg = new String("221 OK \r\n").getBytes(messageCharset);	}	//quit
			case 6: {	ackMsg = new String("214 OK \r\n").getBytes(messageCharset);	} //help
			case 7: {	ackMsg = new String("250 OK \r\n").getBytes(messageCharset);	} //massage
		
		}
		sendAck(clientChannel);

	}

	//send ack back to client
	private static void sendAck(SocketChannel clientChannel) throws IOException {
		ByteBuffer buf =ByteBuffer.allocate(1024);
		buf.clear();
		buf = ByteBuffer.wrap(ackMsg);
		buf.flip();
		
		clientChannel.write(buf);
		
		buf.clear();
	}

	private static String createRandomNumber(){
		//erstellt zufällige nummer
		int randomNum = ThreadLocalRandom.current().nextInt(0, 9999 + 1);
		//umwandeln in 4-stelligen string mit führenden nullen
		String formatted = String.format("%04d", randomNum);
		return formatted;

	}

	
	private static void writeToFile(EmailStruct emailStruct) throws IOException{
	
		// if  Reciever Folder already exists then Write in it 
		// TODO : replace valid path
		Path path = Paths.get("valid path" + emailStruct.getReceiver()+"/"+ emailStruct.getReceiver()+".txt");
		File newFile = null;
		
		if (Files.notExists(path))
		{
			newFile = new File("valid path"+emailStruct.getReceiver()+"/"+ emailStruct.getReceiver()+".txt"); 
			newFile.getParentFile().mkdirs();
			FileOutputStream file = new FileOutputStream("valid path" + emailStruct.getReceiver()+"/"+emailStruct.getReceiver()+".txt");
		}
		//creates random id in string format
		String massage_id = createRandomNumber();
		
		// create sender File 
		FileOutputStream f = new FileOutputStream("valid path"+emailStruct.getReceiver()+"/"+ emailStruct.getSender()+"_"+massage_id+".txt");
		FileChannel channel = f.getChannel();

	
		//create and put Massage in ByteBuffer
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.put(emailStruct.getData().getBytes(messageCharset));
		// change to writing Modus
		buf.flip();

	
		//write to  File and close it afterwards 
		channel.write(buf);
		channel.close();
		buf.clear();	
	

	 }

	 

	
	public static void main(String[] args) {

		// TODO 1.selector and  Socket
		Selector selector = null ;
		ServerSocketChannel servSock = null;
		
		// Initialize Char -> Bytes Format
		try {
			messageCharset = Charset.forName("US-ASCII");
		} catch(UnsupportedCharsetException uce) {
			System.err.println("Cannot create charset for this application. Exiting...");
			System.exit(1);
		}
		
		
		// Initialize Bytes -> Char Format
		decoder = messageCharset.newDecoder();


		try {
			selector = Selector.open();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		// TODO 2. create channel
		try {
   		
			servSock = ServerSocketChannel.open();
			servSock.configureBlocking(false);
			servSock.bind(new InetSocketAddress("localhost",12345)); //CHANGE
			servSock.register(selector, SelectionKey.OP_ACCEPT);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//  Initialize Ack (HELO)
		
		// TODO 3. iterate to new selector_key
		while(true){
			try {
				if(selector.select() == 0)
					continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selectedKeys.iterator();

			while(iter.hasNext()) {
				SelectionKey key = iter.next();
				
				/* check ready set of channel */
				// EmailStruct [] myEmailStructs = new EmailStruct();
				
				List<EmailStruct> myEmailStructs = new ArrayList<>();
				myEmailStructs.add(new EmailStruct());

				//has to be dynamic
				// TODO 4. identify flags and set state
				try{
					if (key.isAcceptable()) {
						ServerSocketChannel sock = (ServerSocketChannel) key.channel();
						SocketChannel client = sock.accept();
						client.configureBlocking(false);
						client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						key.attach(myEmailStructs);
					}

				// TODO 5. parse message
					if (key.isReadable()) {
					// receive the buffer
	
						ByteBuffer bytebuf =ByteBuffer.allocate(1024);
						SocketChannel channel = (SocketChannel) key.channel();
						channel.read(bytebuf);
						bytebuf.flip();
						CharBuffer charbuf;

					
						charbuf = decoder.decode(bytebuf);
						
					
						String inputString = charbuf.toString();
					
						System.out.println(inputString);

						// HIER SOLL PARSE INPUT GERUFEN WERDEN
						boolean result = EmailStruct.parseInput(key, inputString);
						System.out.println(result);

						//  Update files :if Connection is done and Closed then write everything to the Files
						if(result == true)
						{
							EmailStruct[] emailStructArray = (EmailStruct []) key.attachment();
							for (EmailStruct tmp :emailStructArray) {
								if(tmp.getState() == EmailStruct.ERROR) continue ;
								writeToFile(tmp);
							}

							
							if(!key.isValid()){
								System.out.println("Closing Connection");
								key.cancel();
								key.channel().close();
								System.exit(0);
							}
							
							
						}
						//INT MODUS is returned here and used for sendMessage

			


				//7. if valid SEND ACK OR ANSWER HELP
						

				
					
					}
					if (key.isWritable()){	
						List<EmailStruct> emailStructArray =(List<EmailStruct>) key.attachment();
						//TODO check casting correctness with try and catch
						EmailStruct emailStruct = emailStructArray.get(emailStructArray.size()-1); 
						int modus=emailStruct.getState() ;
						if(emailStruct.getHelpFlag() == true){
							modus = 6 ;
							emailStruct.deactivateHelpFlag() ;
						}
						
						sendMessages(modus,(SocketChannel) key.channel());
					}				
					
				}catch(IOException ioe) {
					ioe.printStackTrace();
					System.exit(1);
				}
				

				iter.remove();
			}

		}
	}
}
