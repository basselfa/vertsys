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
        ArrayList<RecThread> recList = new ArrayList<RecThread>();
        
        mSequencer s = new mSequencer();
		Thread seq = new Thread(s, "seq"); 
		s.associateToThread(seq);
		s.recList=recList;
		
		
		//TO DO: create an array list for the threads 
		
		for(int i = 1; i<=numThreads; i++) {
			RecThread rect = new RecThread(i);
			Thread t = new Thread(rect,"t"+i);
			//TO DO:add thread to the array list
			rect.associateToThread(t,i);
			rect.setSequencer(s);
			recList.add(rect);
			t.start();
		}
		
		
		
		
		client c = new client(numMessages, numThreads,recList);
		Thread clientThread = new Thread(c,"client1");
		
		
		s.c=c;
		clientThread.start();
		seq.start();
		

		while(clientThread.isAlive()) {;}
		
		for (RecThread recThread : recList) {
			recThread.setFlagAwake(false);
				
		} 
		s.setFlagAwake(false);
		
	}
	
}