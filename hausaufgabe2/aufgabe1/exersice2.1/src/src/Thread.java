package src;

import java.util.LinkedList; 
import java.util.Queue; 

/*receives external message -> forward to sequencer
 *receives internal message -> store in local history
 *when closed -> write history to log file*/

public class Thread {
	
	private int ID;
	
	public Queue<Integer> q = new LinkedList<>();
	
	public Thread(int id) {
		this.ID = id;
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
