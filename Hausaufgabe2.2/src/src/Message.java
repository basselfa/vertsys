package src;
public class Message {
	
	
	private final int Payload;
	private int TYPE; //1 is external, 0 is internal
	private int id ; 
	//mehr argumente möglich
	//TODO: timestamps array[2]
	
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
	//TODO: funktion: set timestamp
}
