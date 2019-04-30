package src;
public class Message {
	
	
	private final int Payload;
	private int TYPE; //1 is external, 0 is internal
	//mehr argumente möglich
	
	public Message(int payload, int type) {
		this.Payload = payload;
		this.TYPE = type;
	}
	
	public int getPayload() {
		return Payload;
	}
	
	public void setType(int type) {
		this.TYPE = type;
	}
}
