package src;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

/*receives internal messages. -> forward to all threads*/

public class MSequencer implements Runnable {
	private Thread t;
	public Client client;
	boolean flagAwake = true ;
	ArrayList<RecThread> recList;
	
	private int counter=0; //Anzahl der bearbeitete msgs
	public volatile LinkedList<Message> receivedMsgs = new LinkedList<>();
	public volatile LinkedList<Message> Msgs = new LinkedList<>();
	public void associateToThread(Thread thread) {
		t = thread;
	}
	
	public void addReceivedMsgs(Message msg) {
		receivedMsgs.add(msg);
	}
	public void setFlagAwake(boolean value) {
		flagAwake = value ;
	}
	public void printLog() throws FileNotFoundException, UnsupportedEncodingException {
			
			PrintWriter writer = new PrintWriter( "/home/users/m/magical_studies/irb-ubuntu/uni/VS(verteilteSysteme)/ergebnisse2.1/logThreadMassageSequencer"  , "UTF-8");
			for (Message msg : receivedMsgs) {
				writer.println(msg.getId() + "  " +msg.getPayload())	;	
			}
			writer.close();
	}
	
	public void informReceivers(Message msg) throws InterruptedException {
		for(RecThread tmpRecv : recList) {
			tmpRecv.addInternal(msg);
//			synchronized(tmpRecv){	
//				tmpRecv.notify();
//			}
			//this.t.sleep(50);
		//	counter++;
		}	
	}
	
	
	@Override
	public void run() {
		try {
			while(flagAwake==true) {

				while(receivedMsgs.isEmpty() && flagAwake==true) {
//					Thread.sleep(1);
					continue;
				}
				
				if(flagAwake==true) {
					System.out.println("Sequencer received a message with id: "+ receivedMsgs.peekLast().getId()+" and is broadcasting it to all threads");
					//broadcast message to everyone
					informReceivers(receivedMsgs.pollLast());
					synchronized(client){	
						client.notify();
					}
				}
				
			}
			System.out.println("Sequencer knows the flag is false");
			for(RecThread tmpRecv : recList) {
				synchronized(tmpRecv){	
					tmpRecv.notify();
				}
				
			}
			printLog();
		} catch (FileNotFoundException | UnsupportedEncodingException | InterruptedException e) {			
			e.printStackTrace();
		} 
		System.out.println("Sequencer terminated");
		
		
	}

}
