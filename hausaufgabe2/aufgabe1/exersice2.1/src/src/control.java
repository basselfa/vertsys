package src;

/*when started -> creates multiple threads(number depending) and sequencer
 *when ended -> gives command for history saving and closes threads*/

public class control {

	public static void main(int argNumThreads, int argNumClinets) {
		
		mSequencer Sequencer = new mSequencer();
		
		for(int i = 0; i<argNumThreads; i++) {
			Thread thread = new Thread(i);
		}
		
		client Client = new client(argNumClinets, argNumThreads);
		
		//ablauf:
		//start client
		//start mSequencer
		//start threads
		//checken ob alles durch
		//close threads(set thread.active auf false)
		//exit
	}
	
	public void closeThreads(int argNumThreads) {

		for(int i = 0; i<argNumThreads; i++) {
			Thread thread = new Thread(i);
		}
	}
}
