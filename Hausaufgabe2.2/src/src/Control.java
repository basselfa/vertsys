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
		
		//TO DO: create an array list for the threads 
		
		for(int i = 1; i<=numThreads; i++) {
			RecThread rect = new RecThread(i);
			Thread t = new Thread(rect,"t"+i);
			//TO DO:add thread to the array list
			rect.associateToThread(t,i);
			
			recList.add(rect);
			t.start();
		}
		
		
		
		
		Client Client = new Client(numMessages, numThreads,recList);
		for (RecThread recThread : recList) {
			recThread.setFlagAwake(false);
				
		} 
		
	}
	
}