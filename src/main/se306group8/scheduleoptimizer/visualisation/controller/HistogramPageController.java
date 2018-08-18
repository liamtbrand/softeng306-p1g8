package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.manager.HistogramManager;

public class HistogramPageController extends Controller {
	@FXML
	private BarChart<String, Number> chart;

	@FXML
	private Label scheduleTitle;
	
	@Override
	public void setup() {
		startManager(new HistogramManager(chart, scheduleTitle), UpdateFrequency.FAST);
		
		chart.getData().add(new Series<>("Lower Bound", FXCollections.observableArrayList()));
	}
}
