package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.chart.PieChart;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class PieChartManager extends ManagerThread {

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
			diskData.setPieValue(percentOnDisk);
			arrayData.setPieValue(percentInArray);
			queueData.setPieValue(percentInQueue);
		});
	}
}
