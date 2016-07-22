package np2016;

public class Watcher {
	
	/**
	 * 
	 */
	private boolean watcher;

	/**
	 * 
	 */
	public Watcher() {
		this.watcher = false;
	}

	/**
	 * @return
	 */
	synchronized public boolean getWatcher() {
		return watcher;
	}

	/**
	 * @param watcher
	 */
	synchronized public void setWatcher(boolean watcher) {
		this.watcher = watcher;
	}
}
