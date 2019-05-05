package src;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

/*receives internal messages. -> forward to all threads*/

public class mSequencer implements Runnable {
	private Thread t;
	public client c;
	boolean flagAwake = true ;
	ArrayList<RecThread> recList;
	
	private int counter=0; //Anzahl der bearbeitete msgs
	public LinkedList<Message> q = new LinkedList<>();
	
	public void associateToThread(Thread thread) {
		t = thread;
	}
	
	public void add(Message msg) {
		q.add(msg);
	}
	public void setFlagAwake(boolean value) {
		flagAwake = value ;
	}
	public void printLog() throws FileNotFoundException, UnsupportedEncodingException {
			
			PrintWriter writer = new PrintWriter( "/Users/ferielamira/Desktop/SS19/VS/logThreadMassageSequencer"  , "UTF-8");
			for (Message msg : q) {
				writer.println(msg.getId() + "  " +msg.getPayload())	;	
			}
			writer.close();
	}
	
	public void informReceivers(Message msg) throws InterruptedException {
		for(RecThread tmpRecv : recList) {
			tmpRecv.addInternal(msg);
		}	
		counter++;
	}
	
	
	@Override
	public void run() {
		
		while(flagAwake==true) {
			try {	
	
				//we received a message 
				if(counter < q.size()) {
					System.out.println("Sequencer received a message with id: "+ q.peekLast().getId()+" the message is internal"+ q.peekLast().getType());
					//broadcast message to everyooooone
				
					informReceivers(q.peekLast());
				
					synchronized(c)
					{	
						c.notify();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			printLog();
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
		}
		
		
	}

}
