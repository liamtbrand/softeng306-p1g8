package se306group8.scheduleoptimizer.visualisation.controller;

import java.util.Timer;

import javafx.fxml.FXML;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.manager.Manager;

/**
 * The purpose of main controller is to keep references to the controllers for each page.
 */
public class MainController {
	private final Timer updateTimer;
	
	// The controllers are injected here. Convention is: fx:id+Controller.
	@FXML
	private DashboardPageController dashboardPageController;
	@FXML
	private TaskSchedulePageController taskSchedulePageController;
	@FXML
	private SearchSpacePageController searchSpacePageController;
	@FXML
	private ConsolePageController consolePageController;
	@FXML
	private HistogramPageController histogramPageController;

	public MainController() {
		updateTimer = new Timer("Display Polling Timer", true);
	}
	
	@FXML
	private void initialize() {
		dashboardPageController.setMainController(this);
		taskSchedulePageController.setMainController(this);
		searchSpacePageController.setMainController(this);
		consolePageController.setMainController(this);
		histogramPageController.setMainController(this);
		
		FXApplication.getMonitor().addListener(new FinishMessageBoxController());
	}

	public void addToUpdate(Manager manager) {
		updateTimer.schedule(manager, 0, UpdateFrequency.SLOW.period);
	}
	
	public void addToUpdate(Manager manager, UpdateFrequency frequency) {
		updateTimer.schedule(manager, 0, frequency.period);
	}
}
