package se306group8.scheduleoptimizer.visualisation.controller;

import se306group8.scheduleoptimizer.visualisation.manager.Manager;

public abstract class Controller {
	private MainController mainController;
	
	/** This method is called after the parent is created, register your handers here. */
	protected abstract void setup();
	
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
		setup();
	}
	
	protected void startManager(Manager manager) {
		mainController.addToUpdate(manager, UpdateFrequency.SLOW);
	}
	
	protected void startManager(Manager manager, UpdateFrequency frequency) {
		mainController.addToUpdate(manager, frequency);
	}
}
