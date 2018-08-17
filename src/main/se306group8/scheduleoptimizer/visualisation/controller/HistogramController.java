package se306group8.scheduleoptimizer.visualisation.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart.Series;
import se306group8.scheduleoptimizer.visualisation.manager.HistogramManager;

public class HistogramController extends Controller {
	@FXML
	private BarChart<String, Number> chart;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		startManager(new HistogramManager(chart), 0l, 1000l);
		
		chart.getData().add(new Series<>("Lower Bound", FXCollections.observableArrayList()));
		chart.setBarGap(0.0);
		chart.setLegendVisible(false);
	}
}
