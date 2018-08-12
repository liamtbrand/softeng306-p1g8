package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

public class StackedBarChartManager {

	private StackedBarChart stackedBarChart;

	private XYChart.Data diskData;
	private XYChart.Data arrayData;
	private XYChart.Data queueData;

	public StackedBarChartManager(StackedBarChart stackedBarChart) {

		this.stackedBarChart = stackedBarChart;

		XYChart.Series dataSeriesDisk = new XYChart.Series();
		XYChart.Series dataSeriesArray = new XYChart.Series();
		XYChart.Series dataSeriesQueue = new XYChart.Series();

		dataSeriesDisk.setName("Disk");
		dataSeriesArray.setName("Array");
		dataSeriesQueue.setName("Queue");

		diskData = new XYChart.Data();
		diskData.setXValue("Memory");
		diskData.setYValue(0.1);
		dataSeriesDisk.getData().add(diskData);

		arrayData = new XYChart.Data();
		arrayData.setXValue("Memory");
		arrayData.setYValue(0.1);
		dataSeriesArray.getData().add(arrayData);

		queueData = new XYChart.Data();
		queueData.setXValue("Memory");
		queueData.setYValue(0.1);
		dataSeriesQueue.getData().add(queueData);

		stackedBarChart.getData().setAll(dataSeriesDisk,dataSeriesArray,dataSeriesQueue);
	}

	public void update(double percentOnDisk, double percentInArray, double percentInQueue) {
		Platform.runLater(() -> {
			diskData.setYValue(percentOnDisk);
			arrayData.setYValue(percentInArray);
			queueData.setYValue(percentInQueue);
		});
	}

}
