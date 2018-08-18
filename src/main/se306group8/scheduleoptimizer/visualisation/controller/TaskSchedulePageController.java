package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import se306group8.scheduleoptimizer.visualisation.manager.ScheduleManager;

public class TaskSchedulePageController extends Controller {

	@FXML private VBox tasks;
	@FXML private VBox processors;
	@FXML private LineChart chart;
	@FXML private Label title;

	@Override
	public void setup() {
		startManager(new ScheduleManager(tasks, processors, chart, title), UpdateFrequency.SLOW);

	}

}
