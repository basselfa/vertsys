package src;

import java.util.ArrayList;
import java.util.Random;

/*sends random masseges with ints to single thread(external message)*/

public class Client {
	RecThread thread;
	
	public int random(int upperBound) {
		Random rand = new Random();
		// Obtain a number between [0 - (numOfThreads-1) ].
		int n = rand.nextInt(upperBound);
		// Add 1 to the result to get a number from the required range
		return n += 1;
	}
	
	public void getRandomThread(int numOfThreads, ArrayList<RecThread> list) {
		int randThreadID = random(numOfThreads);
		System.out.println("Client will be sending  messages to thread"+randThreadID);	
		for(int j=0 ; j< list.size(); j++) {
			if(list.get(j).getID() == randThreadID)
				thread = list.get(j);
		}
	}
	
	public void sendRandomMsgs(int numOfMsgs) {
		for(int i=0;i<numOfMsgs;i++) {
			//get the right thread from RecThread List
			int randMsg =random(1000);
			Message msg = new Message(randMsg,1,i);
			thread.add(msg);
			
		}	
	}
		
	
	public Client(int numOfMessages, int numOfThreads, ArrayList<RecThread> list) {
		//pick random thread 
		getRandomThread(numOfThreads,list);
		//send messages to thread
		
		//block 
		sendRandomMsgs(numOfMessages);
		//free
		
		
	}

}
