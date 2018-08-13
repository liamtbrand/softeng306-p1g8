package se306group8.scheduleoptimizer.visualisation.manager;

public abstract class ManagerThread extends Thread {

	private volatile long updateFrequency = 1000l; // Default to updating once per second.

	protected abstract void updateHook();

	/**
	 * Set how often this manager should invoke the #updateHook.
	 * @param frequency
	 */
	private final void setUpdateFrequency(long frequency) {
		this.updateFrequency = frequency;
	}

	@Override
	public final void run() {
		try {
			while(true) {

				this.updateHook();

				this.sleep(updateFrequency);
			}
		} catch (InterruptedException e) {
			// TODO give an appropriate message.
			return;
		}
	}

}
