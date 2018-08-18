package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class StackedBarChartManager extends Manager {

	private StackedBarChart<String, Number> stackedBarChart;

	private XYChart.Data<String, Number> diskData;
	private XYChart.Data<String, Number> arrayData;
	private XYChart.Data<String, Number> queueData;

	public StackedBarChartManager(StackedBarChart<String, Number> scheduleStorage) {

		this.stackedBarChart = scheduleStorage;

		XYChart.Series<String, Number> dataSeriesDisk = new XYChart.Series<>();
		XYChart.Series<String, Number> dataSeriesArray = new XYChart.Series<>();
		XYChart.Series<String, Number> dataSeriesQueue = new XYChart.Series<>();

		dataSeriesDisk.setName("Disk");
		dataSeriesArray.setName("Array");
		dataSeriesQueue.setName("Queue");

		diskData = new XYChart.Data<>();
		diskData.setXValue("Memory");
		diskData.setYValue(0.1);
		dataSeriesDisk.getData().add(diskData);

		arrayData = new XYChart.Data<>();
		arrayData.setXValue("Memory");
		arrayData.setYValue(0.1);
		dataSeriesArray.getData().add(arrayData);

		queueData = new XYChart.Data<>();
		queueData.setXValue("Memory");
		queueData.setYValue(0.1);
		dataSeriesQueue.getData().add(queueData);

		scheduleStorage.getData().setAll(dataSeriesDisk,dataSeriesArray,dataSeriesQueue);
	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		long schedulesInArray = monitor.getSchedulesInArray();
		long schedulesInQueue = monitor.getSchedulesInQueue();
		long schedulesOnDisk = monitor.getSchedulesOnDisk();

		long totalSchedules = schedulesInArray + schedulesInQueue + schedulesOnDisk;

		if(totalSchedules == 0) {
			totalSchedules += 1;
		}

		double percentOnDisk = 100.0 * schedulesOnDisk / totalSchedules;
		double percentInArray = 100.0 * schedulesInArray / totalSchedules;
		double percentInQueue = 100.0 * schedulesInQueue / totalSchedules;

		diskData.setYValue(percentOnDisk);
		arrayData.setYValue(percentInArray);
		queueData.setYValue(percentInQueue);
	}
}
