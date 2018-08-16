package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.Initializable;
import se306group8.scheduleoptimizer.visualisation.manager.Manager;

import java.util.*;

public abstract class Controller implements Initializable {

	private Timer timer;

	public Controller() {
		timer = new Timer();
	}

	protected void startManager(Manager manager, long delay, long period) {
		timer.schedule(manager,delay,period);
	}

	protected void stopHook() {}

	public final void stop() {
		stopHook();
		timer.cancel();
	}

}
