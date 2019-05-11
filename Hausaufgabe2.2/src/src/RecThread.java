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
	
	public LinkedList<Message> externalQueue = new LinkedList<>();
	public LinkedList<Message> internalQueue = new LinkedList<>();
	
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
		internalQueue.add(msg);		//erstmal irgendwie reischreiben durcheinnander, später sortieren TODO: timestamp auslesen und aktiualisieren
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
		//TODO: sort
		PrintWriter writer = new PrintWriter( "/home/users/m/magical_studies/irb-ubuntu/uni/VS(verteilteSysteme)/ergebnisse2.1/logThread" + ID , "UTF-8");
		for (Message msg : internalQueue) {
			writer.println(msg.getId() + "  " +msg.getPayload())	;	;	
		}
		writer.close();
	}
	
	public void sendToSeq(Message msg) {
		msg.setType(0); //internal 
		seq.add(msg);	
	}
	
	public void parseMsg() {

		// We received a msg
		if( externalQueue.size()!= 0) {
			Message msg = externalQueue.pollLast();
			System.out.println("Thread"+this.getThreadID()+" received a message with id: "+ msg.getId()+"from client");
			sendToSeq(msg);				//TODO: send to all threads
			System.out.println("Thread " + this.getThreadID()  + " exiting.");
			//TODO: set timestamp
			//TODO: broadcast to other threads
			synchronized(seq)
			{		
				seq.notify();
			}
		}
					
	}

		

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while (flagAwake == true) {
			parseMsg();
		}
		
		try {
			printLog();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		
		
	}
	
	//TODO: new function sort (comparable)
	

}
