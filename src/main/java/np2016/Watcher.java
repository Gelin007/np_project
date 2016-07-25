package np2016;

/**
 * The Watcher is a monitor, that store a boolean, which checks, whether all
 * Worker have finish to work on the lts. This means the lts is completely
 * revised.
 */
public class Watcher {

	private boolean watcher;

	/**
	 * The constructor.
	 */
	public Watcher() {
		this.watcher = false;
	}

	/**
	 * Gets the watcher.
	 * 
	 * @return boolean
	 */
	synchronized public boolean getWatcher() {
		return watcher;
	}

	/**
	 * Sets the watcher.
	 * 
	 * @param watcher
	 *            boolean
	 */
	synchronized public void setWatcher(boolean watcher) {
		this.watcher = watcher;
	}
}
