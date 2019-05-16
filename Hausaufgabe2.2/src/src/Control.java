package src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/*when started -> creates multiple threads(number depending) and sequencer
 *when ended -> gives command for history saving and closes threads*/

public class Control {
	
	//make a list that contains all the threads 
	public static void main(String[] args) {
	
        int numThreads = Integer.parseInt(args[0]);
        int numMessages = Integer.parseInt(args[1]);
        ArrayList<RecThread> recList = new ArrayList<RecThread>();
//old code
//      MSequencer sequencer = new MSequencer();
//		Thread seqThread = new Thread(sequencer, "seq"); 
//		sequencer.associateToThread(seqThread);
///		sequencer.recList=recList;
		
		
		//TO DO: create an array list for the threads 
		
		for(int i = 1; i<=numThreads; i++) {
			RecThread rect = new RecThread(i);
			Thread thread = new Thread(rect,"t"+i);
			//TO DO:add thread to the array list
			rect.associateToThread(thread,i);
			rect.recList=recList;		//TODOdone: threads brauchen jetzt auch die thread-Liste
//old code	rect.setSequencer(sequencer);
			recList.add(rect);
			thread.start();
		}
		
		
		Client client = new Client(numMessages, numThreads,recList);
		Thread clientThread = new Thread(client,"client1");
		
//old code		
//		sequencer.client=client;
//		
//		seqThread.start();
		
		clientThread.start();
		
		

		while(clientThread.isAlive()) {;}
		
		for (RecThread recThread : recList) {
			recThread.setFlagAwake(false);
			synchronized(recThread) {
				recThread.notify();
			}
			
//			try {
//		         recThread.getThread().join();
//		       
//		      } catch ( Exception e) {
//		         System.out.println("Thread Interrupted");
//		      }
				
		} 
//old code	sequencer.setFlagAwake(false);
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ending threads");
		for (RecThread recThread : recList) {
			recThread.justEndIt();
		}
		
		
	}
	
	
	
}