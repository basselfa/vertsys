package src;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;

/*receives internal messages. -> forward to all threads*/

public class mSequencer implements Runnable {
	
	//VARIABLES
	private Thread t;
	public client c;
	boolean flagAwake = true;  // activity flag, used to terminate thread
	ArrayList<RecThread> recList;
	private int counter=0; //Anzahl der bearbeitete msgs
	public LinkedList<Message> q = new LinkedList<>();
	
	
	
	//METHODS
	
	public void add(Message msg) {
		q.add(msg);
	}
	
	public void printLog() throws FileNotFoundException, UnsupportedEncodingException {
			
			PrintWriter writer = new PrintWriter( "/Users/ferielamira/Desktop/SS19/VS/logThreadMassageSequencer"  , "UTF-8");
			for (Message msg : q) {
				writer.println(msg.getId() + "  " +msg.getPayload())	;	
			}
			writer.close();
	}
	
	
	
	
	/**
	 * broadcast message to all receiving threads
	 * @param msg instance of class message, should be internal?
	 * @throws InterruptedException
	 */
	public void informReceivers(Message msg) throws InterruptedException {
		// iterate over all receiving threads
		for(RecThread tmpRecv : recList) {
			//send the message internally
			tmpRecv.addInternal(msg);// ????????????????? why have type if differentiating method exists
		}	
		counter++;
	}
	
	
	
	/**
	 * This method runs automatically as a part of runnable classes.  It starts up the message sequencer thread.
	 */
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

	
	
	// GETTERS AND SETTERS

	public void setFlagAwake(boolean value) {
		flagAwake = value ;
	}
	
	
	/**
	 * set local thread of the message sequencer to that in the input
	 * @param thread message sequencer thread
	 */
	public void associateToThread(Thread thread) {
		t = thread;
	}
	
	
}
