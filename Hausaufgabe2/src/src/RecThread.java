package src;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList; 
import java.util.Queue; 

/*receives external message -> forward to sequencer
 *receives internal message -> store in local history
 *when closed -> write history to log file*/

public class RecThread implements Runnable{
	private Thread t;
	private MSequencer seq;
	private int ID;
	private int counter=0;
	boolean flagAwake = true ;
	RecThread lock;
	public Client client;

	
	public volatile LinkedList<Message> externalQueue = new LinkedList<>();
	public volatile LinkedList<Message> internalQueue = new LinkedList<>();
	
	public void justEndIt() {
		this.t.interrupt();
	}
	
	public RecThread(int id) {
		this.ID = id;
	}
	public int getThreadID() {
		return ID;
	}
	
	public Thread getThread() {
		return t;
	}
	
	public void setSequencer(MSequencer sequencer) {
		this.seq= sequencer;
	}
	public void associateToThread(Thread thread, int threadID) {
		ID = threadID;
		t = thread;
	}
	public void addExternal(Message msg) {
		externalQueue.add(msg);
	}
	public void addInternal(Message msg) {
		internalQueue.add(msg);
	}
	
	public void printInternalQueue() {
		for(int i=0; i< internalQueue.size(); i++) {
			System.out.println("Element "+i+": "+internalQueue.get(i).getPayload());
		}
	}
	public void setFlagAwake(boolean value) {
		flagAwake = value ;
	}
	
	public void printLog() throws FileNotFoundException, UnsupportedEncodingException {
		
		PrintWriter writer = new PrintWriter( "/home/users/m/magical_studies/irb-ubuntu/uni/VS(verteilteSysteme)/ergebnisse2.1/logThread" + ID , "UTF-8");
		for (Message msg : internalQueue) {
			writer.println(msg.getId() + "  " +msg.getPayload())	;	;	
		}
		writer.close();
	}
	
	public void sendToSeq(Message msg) {
		msg.setType(0); //internal 
		seq.addReceivedMsgs(msg);
		System.out.println("the size of seq msgs: "+ seq.receivedMsgs.size());
	}
	
	public void parseMsg() {

		// We received a msg
		while( externalQueue.size()!= 0) {
			Message msg = externalQueue.pollLast();
			System.out.println("Thread"+this.getThreadID()+" received a message with id: "+ msg.getId()+"from client");
			sendToSeq(msg);				
			System.out.println("Thread " + this.getThreadID()  + " finished publishing.");
			
//			synchronized(seq)
//			{		
//				seq.notify();
//			}
		}
					
	}

		

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while (flagAwake == true) {
			synchronized(this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			parseMsg();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized(client){	
				client.notify();
			}
		}
		
		try {
			printLog();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		System.out.println("thread terminated");
		
		while(this.t.isInterrupted() == false) { continue; }

	}
	

}
