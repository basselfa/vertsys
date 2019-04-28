package src;

public class Message {
	
	private final int Payload;
	private String TYPE;
	//mehr argumente möglich
	
	public Message(int payload, String type) {
		this.Payload = payload;
		this.TYPE = type;
	}
	
	public int getPayload() {
		return Payload;
	}
	
	public void setType(String type) {
		this.TYPE = type;
	}
}
