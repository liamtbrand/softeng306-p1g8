package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart.Series;
import se306group8.scheduleoptimizer.visualisation.manager.HistogramManager;

public class HistogramPageController extends Controller {
	@FXML
	private BarChart<String, Number> chart;

	@Override
	public void setup() {
		startManager(new HistogramManager(chart), UpdateFrequency.FAST);
		
		chart.getData().add(new Series<>("Lower Bound", FXCollections.observableArrayList()));
		chart.setCategoryGap(0.0);
		chart.setBarGap(0.0);
		chart.setLegendVisible(false);
		chart.setAnimated(false);
	}
}
