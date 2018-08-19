package se306group8.scheduleoptimizer.visualisation.controller;

import java.util.Timer;
import java.util.TimerTask;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
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
	@FXML
	private StatusTitleController statusTitleController;
	@FXML
	private Tab scheduleDistributionTab;

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
		statusTitleController.setMainController(this);

		scheduleDistributionTab.setDisable(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if( FXApplication.getMonitor().getAlgorithmName().equals("DFS Branch & Bound") && FXApplication.getMonitor().getCoresToUseForExecution() > 1 ) {
					scheduleDistributionTab.setDisable(true);
				} else {
					scheduleDistributionTab.setDisable(false);
				}
			}
		};
		updateTimer.schedule(task,0l,1000l);
		
		FXApplication.getMonitor().addListener(new FinishMessageBoxController());
	}

	public void addToUpdate(Manager manager) {
		updateTimer.schedule(manager, 0, UpdateFrequency.SLOW.period);
	}
	
	public void addToUpdate(Manager manager, UpdateFrequency frequency) {
		updateTimer.schedule(manager, 0, frequency.period);
	}
}
