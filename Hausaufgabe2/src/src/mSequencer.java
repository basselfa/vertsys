package src;


/*receives internal messages. -> forward to all threads*/

public class mSequencer implements Runnable {
	
	@Override
	public void run() {
		System.out.println("Inside:"+ Thread.currentThread().getName());
		//Thread work happens here 
	}

}
