package src;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.TimeUnit; 

/*receives external message -> forward to sequencer
 *receives internal message -> store in local history
 *when closed -> write history to log file*/

public class RecThread implements Runnable{
	private Thread t;
//old code	private MSequencer seq;
	private int ID;
	private int counter=0;
	RecThread lock;
	boolean flagAwake = true ;

//TODOdone: new variables
	ArrayList<RecThread> recList;	//speichert alle threads an die geschickt werden muss
	private int time = 0; //thread eigene time

	
	public volatile LinkedList<Message> externalQueue = new LinkedList<>();
	public volatile LinkedList<Message> internalQueue = new LinkedList<>();
	
	public RecThread(int id) {
		this.ID = id;
	}
	public int getThreadID() {
		return ID;
	}
	
	public Thread getThread() {
		return t;
	}
//old code
//	public void setSequencer(MSequencer sequencer) {
//		this.seq= sequencer;
//	}
	public void associateToThread(Thread thread, int threadID) {
		ID = threadID;
		t = thread;
	}
	public void addExternal(Message msg) {
		externalQueue.add(msg);
	}
	public void addInternal(Message msg) {
		internalQueue.add(msg);	//erstmal irgendwie reischreiben durcheinnander, später sortieren 
		//TODOdone: timestamp auslesen und aktualisieren
		if(msg.getTimeOfSenderThread() > this.time) {
			this.time = msg.getTimeOfSenderThread();
		}
	}
	
	public void printInternalQueue() {
		for(int i=0; i< internalQueue.size(); i++) {
			System.out.println("Element "+i+": "+internalQueue.get(i).getPayload());
		}
	}
	public void setFlagAwake(boolean value) {
		flagAwake = value ;
	}

	public void justEndIt() {
		this.t.interrupt();
	}
	
	public void printLog() throws FileNotFoundException, UnsupportedEncodingException {
		//TODO: sort
		//wait for all threads to finish writing
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PrintWriter writer = new PrintWriter( "/home/users/m/magical_studies/irb-ubuntu/uni/VS(verteilteSysteme)/ergebnisse2.1/logThread" + ID , "UTF-8");
		for (Message msg : internalQueue) {
			writer.println(msg.getId() + "  " +msg.getPayload())	;	;	
		}
		writer.close();
	}
//old code	
//	public void sendToSeq(Message msg) {
//		msg.setType(0); //internal 
//		seq.addReceivedMsgs(msg);
//		System.out.println("the size of seq msgs: "+ seq.receivedMsgs.size());
//	}
	
	public void parseMsg() {

		// We received a msg
		if( externalQueue.size()!= 0) {
			Message msg = externalQueue.pollLast();
			System.out.println("Thread"+this.getThreadID()+" received a message with id: "+ msg.getId()+"from client");
//old code	sendToSeq(msg);			
			//TODOdone: send to all threads	
			sendToAllThreads(msg);
			//TODOdone: timer wird erhöht für nächstes empfangen
			this.time++;
			System.out.println("Thread " + this.getThreadID()  + " exiting.");
			//TODO: broadcast to other threads //bin mir nicht mehr sicher was wir mit diesem kommentar meinen. wurde ja schon geschickt...kann weg?
//			synchronized(seq)
//			{		
//				seq.notify();
//			}
		}
					
	}

		

	@Override
	public void run() {		//TODO: client wird jetzt nicht mehr vom sequencer notified. muss hier gemacht werden?
		// TO DO Auto-generated method stub
		
		while (this.flagAwake == true) {
			synchronized(this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					// TO DO Auto-generated catch block
					e.printStackTrace();
				}
			}
			parseMsg();
			
			
		}
		
		try {
			printLog();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		System.out.println("thread terminated");
		
			
		Collections.sort(internalQueue);
		
		
		while(this.t.isInterrupted() == false) { continue; }
	}
	
	
	//TODOdone: new funktion sendToAllThreads
	public void sendToAllThreads(Message msg) {
		//TODOdone: set timestamp
		msg.setTimestamp(ID, time);
		for(RecThread tmpRecv : recList) {
			tmpRecv.addInternal(msg);
		}
	}


	

}
 