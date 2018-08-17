package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.StackedBarChart;
import se306group8.scheduleoptimizer.visualisation.manager.StackedBarChartManager;

import java.net.URL;
import java.util.*;

public class StatisticsPageController extends Controller {

	@FXML
	private StackedBarChart<String, Number> scheduleStorage;
	
	@Override
	public void setup() {

		startManager(new StackedBarChartManager(
				scheduleStorage
		));

	}

}
