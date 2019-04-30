package src;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/*when started -> creates multiple threads(number depending) and sequencer
 *when ended -> gives command for history saving and closes threads*/

public class control {
	
	//make a list that contains all the threads 
	public static void main(String[] args) {
	
        int numThreads = Integer.parseInt(args[0]);
        int numMessages = Integer.parseInt(args[1]);
        ArrayList<RecThread> recList = new ArrayList<RecThread>();
        
		Thread seq = new Thread(new mSequencer(), "seq");
		seq.start();
		
		//TO DO: create an array list for the threads 
		
		for(int i = 1; i<=numThreads; i++) {
			Thread t = new Thread(new RecThread(i),"t"+i);
			RecThread rect = new RecThread(i);
			//TO DO:add thread to the array list
			rect.associateToThread(t,i);
			recList.add(rect);
			t.start();
		}
		
		
		
		
		client Client = new client(numMessages, numThreads,recList);
	}
	
}