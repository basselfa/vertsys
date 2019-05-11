/**
 * Plays the role of main.
 * Manages the initialization of all threads and that of the client.
 * receives the number of threads and 
 */


package src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/*when started -> creates multiple threads(number depending) and sequencer
 *when ended -> gives command for history saving and closes threads*/

public class control {
	
	//make a list that contains all the threads 
	public static void main(String[] args) {
		
        int numThreads = Integer.parseInt(args[0]);
        int numMessages = Integer.parseInt(args[1]);
        
		// create an array list for the receiving threads 
        ArrayList<RecThread> recList = new ArrayList<RecThread>();
        
        
        // build sequencer (can be done with a builder)
        mSequencer mSeqInstance = new mSequencer();
		Thread seq = new Thread(mSeqInstance, "seq"); 
		mSeqInstance.associateToThread(seq);
		mSeqInstance.recList=recList;
		
		// initialize the receiving threads and add them to the ArrayList of receiving threads.
		for(int i = 1; i<=numThreads; i++) {
			// build new receiver thread's class setting it's id.
			RecThread rect = new RecThread(i);
			// initialize new receiver thread.
//			Thread newThread = new Thread(rect,"t"+i); // why is this a "t"?  Does it matter?
			Thread newThread = new Thread(rect,"newThread"+i);
			
			// add thread to the array list
			rect.associateToThread(newThread,i);
			rect.setSequencer(mSeqInstance);
			recList.add(rect);
			//start thread (run it)
			newThread.start();
		}
		
		
		
		// initialize the client, and it's thread
		client clientInstance = new client(numMessages, numThreads,recList);
		Thread clientThread = new Thread(clientInstance,"client1");
		
		
		
		//set message sequencer's client to client instance
		mSeqInstance.c=clientInstance;
		
		
		// run sequence thread
		seq.start();		
		clientThread.start();
		
		
		
		// non-blocking wait for client thread to finish sending messages.
		while(clientThread.isAlive()) { continue; }
		
		// turn off receiving threads once the client is done
		for (RecThread recThread : recList) {
			recThread.setFlagAwake(false);
		}
		
		// turn off message sequencer flag
		mSeqInstance.setFlagAwake(false);
		
	}
	