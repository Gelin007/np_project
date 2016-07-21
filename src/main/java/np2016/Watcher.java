package np2016;

public class Watcher {
	
	private boolean watcher;

	public Watcher() {
		this.watcher = false;
	}

	synchronized public boolean getWatcher() {
		return watcher;
	}

	synchronized public void setWatcher(boolean watcher) {
		this.watcher = watcher;
	}
}
