package src;
public class Message {
	//CONSTANTS
	public final static int EXTERNAL = 1;
	public final static int INTERNAL = 0;
	
	//VARIABLES
	private final int Payload;
	private int TYPE; //1 is external, 0 is internal
	private int id;
	//mehr argumente möglich
	
	
	
	
	//METHODS
	
	/**
	 * Message builder
	 * @param payload random integer representing the content of a message
	 * @param type is the message external or internal? (Use contants in Message)
	 * @param id
	 */
	public Message(int payload, int type,int id ) {
		this.Payload = payload;
		this.TYPE = type;
		this.id = id;
		
	}
	
	// GETTERS AND SETTERS
	
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
