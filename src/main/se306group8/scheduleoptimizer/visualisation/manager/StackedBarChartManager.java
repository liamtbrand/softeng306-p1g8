package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class StackedBarChartManager extends ManagerThread {

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

	@Override
	protected void updateHook() {

		ObservableRuntimeMonitor monitor = FXApplication.getMonitor();

		int schedulesInArray = monitor.getSchedulesInArray();
		int schedulesInQueue = monitor.getSchedulesInQueue();
		int schedulesOnDisk = monitor.getSchedulesOnDisk();

		long totalSchedules = schedulesInArray + schedulesInQueue + schedulesOnDisk;

		if(totalSchedules == 0) {
			totalSchedules += 1;
		}

		double percentOnDisk = 100.0 * schedulesOnDisk / totalSchedules;
		double percentInArray = 100.0 * schedulesInArray / totalSchedules;
		double percentInQueue = 100.0 * schedulesInQueue / totalSchedules;

		Platform.runLater(() -> {
			diskData.setYValue(percentOnDisk);
			arrayData.setYValue(percentInArray);
			queueData.setYValue(percentInQueue);
		});
	}
}
