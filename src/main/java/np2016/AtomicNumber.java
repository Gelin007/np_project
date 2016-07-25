package np2016;

/**
 * AtomicNumber is a monitor, that will be used for store the number of working
 * Threads.
 */
public class AtomicNumber {

	private int x;

	public AtomicNumber() {
		x = 0;
	}

	/**
	 * Increases the number of working Threads.
	 */
	synchronized public void increase() {
		x++;
	}

	/**
	 * Cecreases the number of working Threads.
	 */
	synchronized public void decrease() {
		x--;
	}

	/**
	 * Checks, whether the number of working Threads is egal to 0.
	 * 
	 * @return boolean
	 */
	synchronized public boolean check() {
		return x == 0;
	}
}
