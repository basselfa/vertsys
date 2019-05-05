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

	private int ID;
	private int counter=0;
	boolean flagAwake = true ;
	
	public LinkedList<Message> q = new LinkedList<>();
	
	public RecThread(int id) {
		this.ID = id;
	}
	public int getID() {
		return ID;
	}
	
	public Thread getThread() {
		return t;
	}
	
	public void associateToThread(Thread thread, int threadID) {
		ID = threadID;
		t = thread;
	}
	public void add(Message msg) {
		q.add(msg);
	}
	
	public void printQueue() {
		for(int i=0; i< q.size(); i++) {
			System.out.println("Element "+i+": "+q.get(i).getPayload());
		}
	}
	public void setFlagAwake(boolean value) {
		flagAwake = value ;
	}
	
	public void printLog() throws FileNotFoundException, UnsupportedEncodingException {
		
		PrintWriter writer = new PrintWriter( "/home/bass/logs/logThread" + ID , "UTF-8");
		for (Message msg : q) {
			writer.println(msg.getId() + "  " +msg.getPayload())	;	;	
		}
		writer.close();
	}
	public void sendToSeq(Message msg) {
		 // the id of the message will be set by the  sequencer
		msg.setType(0); //internal 
	//	seq.add(msg);
		
	
	}
	public boolean queueContainsMsg(Message msg) {
		boolean contains=false;
		for(int i=0; i< q.size()-1; i++) {
			if(q.get(i).getPayload() == msg.getPayload()) {
				contains=true;
			}
	
		}
		return contains;
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Inside:"+ Thread.currentThread().getName());
		// Thread work happens here
		
		while (flagAwake == true) {
			
			// We received a msg
			if( counter < q.size()) {
				Message msg = q.peekLast();
				
				//msg is external
				if( msg.getType()==1) {
					System.out.println("Thread received a messages");
					sendToSeq(msg);				
					counter++;
				}
				//msg is internal
				else {
					//check if we already have it in the queue and get rid of it 
					if(queueContainsMsg(msg)){
						//if yea, delete it
						q.pollLast();
						
					}
					
				}
			}	

		}
		
		try {
			printLog();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		}
		
		
	}
	
	/*
	pullQueue(){}
	
	receiveExternalMessage(X){
		Message Y = createMessage(X);
		sendMessageToSequencer(Y);
	}
	
	receiveInternalMessage(X){
		Stuff Z = extractMessage(Y);
		storeMessage(Z);
	}
	
	private void storeMessage(Z){};
	
	public boolean saveHistory(){
		do stuff
		return true;
	}
	*/

}
