package src;

import java.util.ArrayList;
import java.util.Random;

/*sends random masseges with ints to single thread(external message)*/

public class client implements Runnable {
	RecThread thread;
	int numOfMessages;
	int numOfThreads;
	ArrayList<RecThread> list;
	
	/**
	 * client builder
	 * @param nMessages number of total messages the client sends
	 * @param nThreads number of total receiving threads
	 * @param rList an array list of all the receiving thread
	 */
	public client(int nMessages, int nThreads,ArrayList<RecThread>rList) {
		numOfMessages=nMessages;
		numOfThreads=nThreads;
		list= rList ;
		
	}	
	
	/**
	 * used to choose the id of the next receiver, to whom the client sends a message.
	 * @param upperBound number of receiver threads for out purposes
	 * @return random number between 2 and upperBound+1
	 */
	public int random(int upperBound) {
		// initialize an instance of random class to use its methods
		Random rand = new Random();
		// Obtain a number in the range of [0, numOfThreads-1].
		int n = rand.nextInt(upperBound);
		// Add 1 to the result to get a number from the required range of [1, numberOfThreads] (id range)
		return n += 1;
	}
	
	
	/**
	 * choose a random receiving thread for the client to send a message two.
	 * @return the receiving thread to send the message to.
	 */
	public RecThread getRandomThread() {
		//get a random ThreadID
		int randThreadID = random(numOfThreads);
		// return chosen thread from the receiving threads' list
		return list.get(randThreadID-1);		
	}
	
	
	
	/**
	 * sends all the messages to the receiving threads
	 * @throws InterruptedException
	 */
	public void sendRandomMsgs() throws InterruptedException {
		
		// send messages with i being the id of each message
		for(int i=0;i<numOfMessages;i++) {
			
			// set message pay load to a random number in the range of [1, 1000]
			int randMsg =random(1000);
			
			// initialize an external message with the correct pay load and its appropriate ID
			Message msg = new Message(randMsg, Message.EXTERNAL, i);
			
			// get a random receiving thread
			RecThread thread = getRandomThread();
			
			System.out.println("Client will be sending  message with id:"+msg.getId()+" to thread"+ thread.getFuckingID());	
			// send message to chosen receiving thread
			thread.addExternal(msg);

			// ############# ??????????? ##############
			/*synchronized(this) {
				this.wait();
			}*/
		}	
	}

	
	/**
	 * run thread sends messages and then terminates thread.
	 */
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