package src;

import java.util.LinkedList; 
import java.util.Queue; 

/*receives external message -> forward to sequencer
 *receives internal message -> store in local history
 *when closed -> write history to log file*/

public class Thread {
	
	private int ID;
	
	public boolean Active;
	
	public Queue<Integer> q = new LinkedList<>();
	
	public Thread(int id) {
		this.ID = id;
	}
	
	//conatandly pulls queue and works. when active == false -> store hiistory
	public void doYourWork(){
		
		while(Active) {
			//do stuff
		}
		
		saveHistory();
	}
	 /* 
	pullQueue(){
	
	}
	
	receiveExternalMessage(X){
		Message Y = createMessage(X);
		sendMessageToSequencer(Y);
	}
	
	receiveInternalMessage(X){
		Stuff Z = extractMessage(Y);
		storeMessage(Z);
	}
	
	private void storeMessage(Z){};
	*/
	public static void saveHistory(){
		//do stuff
	}
	
	
	public void setActive(Boolean active) {
		this.Active = active;
	}

}
