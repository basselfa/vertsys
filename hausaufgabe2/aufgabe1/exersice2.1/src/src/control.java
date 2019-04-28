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
	}
	
	public void closeThreads() {
		
	}
}
