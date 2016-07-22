package np2016;

public class AtomicNumber {

	private int x;

	public AtomicNumber() {
		x = 0;
	}

	/**
	 * @param x
	 */
	synchronized public void set(int x) {
		this.x = x;

	}

	/**
	 * 
	 */
	synchronized public void increase() {
		x++;
	}

	/**
	 * @return
	 */
	synchronized public int get() {
		return x;
	}
	
	/**
	 * 
	 */
	synchronized public void decrease() {
		x--;
	}

	/**
	 * @param y
	 * @return
	 */
	synchronized public boolean check(int y) {
		return x == y;
	}
}
