package src;

import java.util.Comparator;

public class Message implements Comparable<Message>{
	
	private final int Payload;
	private int TYPE; //1 is external, 0 is internal
	private int id ; 
	//mehr argumente möglich
	//TODOdone: TIMESTAMP: [0] = IDofsendingThread; [1] = timeOfSendingThread
	private int[] TIMESTAMP = new int[2];
	
	public Message(int payload, int type,int id ) {
		this.Payload = payload;
		this.TYPE = type;
		this.id = id;
	}
	
	public int getPayload() {
		return Payload;
	}
	
	public int getId() {
		return id;
	}
	
	public int getType() {
		return this.TYPE;
	}
	
	public void setType(int type) {
		this.TYPE = type;
	}
	//TODOdone: funktion: set timestamp
	public void setTimestamp(int threadID, int threadTime) {
		this.TIMESTAMP[0] = threadID;
		this.TIMESTAMP[1] = threadTime;
	}
	//TODOdone: get IDOfThread
	public int getIDOfSenderThread() {
		return this.TIMESTAMP[0];
	}
	//TODOdone: new funktion getTime
	public int getTimeOfSenderThread() {
		return this.TIMESTAMP[1];
	}

	@Override
	public int compareTo(Message message2) {
		// for comparison 
        int TimeCompare = this.getTimeOfSenderThread() - message2.getTimeOfSenderThread(); 
        int IDCompare = this.getId() - message2.getId();
        
        // 2-level comparison using if-else block 
        if (TimeCompare == 0) { 
            return ((IDCompare == 0) ? TimeCompare : IDCompare); 
        } else { 
            return TimeCompare; 
        } 
	}
}
