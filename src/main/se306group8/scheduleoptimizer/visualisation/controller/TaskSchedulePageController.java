package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import se306group8.scheduleoptimizer.visualisation.manager.ScheduleManager;

/**
 * Controller for the Task Schedule tab page in visualisation.
 * Responsible for starting the schedule manager and contains references to fx ids for the FXApplication.
 */

public class TaskSchedulePageController extends Controller {

	@FXML private VBox tasks;
	@FXML private VBox processors;
	@FXML private LineChart chart;
	@FXML private Label bestRuntimeLabel;
	@FXML private Label percentTasksLabel;

	@Override
	public void setup() {
		startManager(new ScheduleManager(
				tasks, 
				processors, 
				chart, 
				bestRuntimeLabel, 
				percentTasksLabel), 
				UpdateFrequency.SLOW);
	}

}
