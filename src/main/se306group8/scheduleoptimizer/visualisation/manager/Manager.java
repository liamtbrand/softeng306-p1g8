package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.TimerTask;

public abstract class Manager extends TimerTask {

	protected abstract void updateHook();

	@Override
	public final void run() {
		updateHook();
	}

}
