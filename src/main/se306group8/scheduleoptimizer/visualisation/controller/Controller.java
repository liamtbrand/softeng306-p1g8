package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.Initializable;
import se306group8.scheduleoptimizer.visualisation.manager.ManagerThread;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller implements Initializable {

	private List<ManagerThread> managers;

	public Controller() {
		managers = new ArrayList<>();
	}

	protected void startManager(ManagerThread th) {
		managers.add(th);
		th.start();
	}

	protected void stopManager(ManagerThread th) {
		try {
			th.interrupt();
			th.join();
		} catch (InterruptedException e) {
			// TODO Interrupted while joining?
		}
	}

	protected void stopHook() {}

	public final void stop() {
		stopHook();
		for(ManagerThread manager : managers) {
			stopManager(manager);
		}
	}

}
