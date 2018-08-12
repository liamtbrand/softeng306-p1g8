package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.chart.PieChart;

public class PieChartManager {

	private PieChart pieChart;

	private PieChart.Data diskData;
	private PieChart.Data arrayData;
	private PieChart.Data queueData;

	public PieChartManager(PieChart pieChart) {

		this.pieChart = pieChart;

		diskData = new PieChart.Data("Disk",0);
		arrayData = new PieChart.Data("Array",0);
		queueData = new PieChart.Data("Queue",0);

		pieChart.getData().setAll(diskData,arrayData,queueData);
		pieChart.labelsVisibleProperty().setValue(true);
		pieChart.legendVisibleProperty().setValue(true);
	}

	public void update(double percentOnDisk, double percentInArray, double percentInQueue) {
		Platform.runLater(() -> {
			diskData.setPieValue(percentOnDisk);
			arrayData.setPieValue(percentInArray);
			queueData.setPieValue(percentInQueue);
		});
	}

}
