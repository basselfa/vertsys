package src;
import java.util.LinkedList; 
import java.util.Queue; 

/*receives external message -> forward to sequencer
 *receives internal message -> store in local history
 *when closed -> write history to log file*/

public class RecThread implements Runnable{
	private Thread t;
	private int ID;
	
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
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Inside:"+ Thread.currentThread().getName());
		// Thread work happens here
		
		
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
