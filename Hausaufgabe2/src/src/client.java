package src;

import java.util.ArrayList;
import java.util.Random;

/*sends random masseges with ints to single thread(external message)*/

public class client implements Runnable {
	RecThread thread;
	int numOfMessages;
	int numOfThreads;
	ArrayList<RecThread> list;
	
	
	public client(int nMessages, int nThreads,ArrayList<RecThread>rList) {
		numOfMessages=nMessages;
		numOfThreads=nThreads;
		list= rList ;
		
	}	
	
	public int random(int upperBound) {
		Random rand = new Random();
		// Obtain a number between [0 - (numOfThreads-1) ].
		int n = rand.nextInt(upperBound);
		// Add 1 to the result to get a number from the required range
		return n += 1;
	}
	
	public RecThread getRandomThread() {
		int randThreadID = random(numOfThreads)-1;
		return list.get(randThreadID);
		
	}
	
	public void sendRandomMsgs() throws InterruptedException {
		for(int i=0;i<numOfMessages;i++) {
			//get the right thread from RecThread List
			int randMsg =random(1000);
			Message msg = new Message(randMsg,1,i);
			RecThread thread = getRandomThread() ;
			System.out.println("Client will be sending  message with id:"+msg.getId()+" to thread"+ thread.getFuckingID());	
			thread.addExternal(msg);
//
			synchronized(this) {
				this.wait();
			}
			
			
		}	
	}
		
	@Override
	public void run() {
		
		//send messages to thread
		try {
			sendRandomMsgs();
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
