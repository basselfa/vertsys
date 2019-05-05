package src;

public class Message {
	
	
	private final int Payload;
	private int TYPE; //1 is external, 0 is internal
	private int id ; 
	//mehr argumente möglich
	
	public Message(int payload, int type,int id ) {
		this.Payload = payload;
		this.TYPE = type;
		
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
}
