package de.tu_berlin.cit;

import java.nio.ByteBuffer;

public class SMTPServerState {

	public final static int CONNECTED = 0;
	public final static int HELO = 1;
	public final static int MAILFROM = 2;
	public final static int RCPTTO = 3;
	public final static int DATA = 4;
	public final static int MESSAGE = 5;
	public final static int QUIT = 6;
	public final static int HELP = 7;
	
	private int state;
	private int previousState;
	private ByteBuffer buffer;
	
	private String from;
	private String to;
	private String data;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	
	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public SMTPServerState() {
		this.state = CONNECTED;
		this.data = "";
	}
	
	public int getState() {
		return this.state;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public int getPreviousState() {
		return previousState;	
	}
	
	public void setPreviousState(int previousState) {
		this.previousState = previousState;
	}
}