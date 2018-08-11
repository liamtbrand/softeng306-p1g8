package se306group8.scheduleoptimizer.visualisation;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import se306group8.scheduleoptimizer.Main;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitorAggregator;
import se306group8.scheduleoptimizer.algorithm.CLIRuntimeMonitor;

import java.util.Timer;
import java.util.TimerTask;

public class FXApplication extends Application {

	@FXML
	private Label myLabel;

	@FXML
	private StackedBarChart scheduleStorage;

	@FXML
	private PieChart storageBreakdown;
	
	private ObservableRuntimeMonitor monitor;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		monitor = new ObservableRuntimeMonitor();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(getClass().getResource("/fxml/MainWindow.fxml"));
		
		Parent root = loader.load();
		Scene scene = new Scene(root);
		
		primaryStage.setTitle("Visulalisation");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		myLabel.textProperty().bind(Bindings.createStringBinding(() -> monitor.hasStarted() ? "Started!" : "Waiting to start.", monitor ));

		// Setup memory usage graph.

		XYChart.Series dataSeriesDisk = new XYChart.Series();
		XYChart.Series dataSeriesArray = new XYChart.Series();
		XYChart.Series dataSeriesQueue = new XYChart.Series();

		dataSeriesDisk.setName("Disk");
		dataSeriesArray.setName("Array");
		dataSeriesQueue.setName("Queue");

		XYChart.Data diskData = new XYChart.Data();
		diskData.setXValue("Memory");
		diskData.setYValue(50);
		dataSeriesDisk.getData().add(diskData);

		XYChart.Data arrayData = new XYChart.Data();
		arrayData.setXValue("Memory");
		arrayData.setYValue(25);
		dataSeriesArray.getData().add(arrayData);

		XYChart.Data queueData = new XYChart.Data();
		queueData.setXValue("Memory");
		queueData.setYValue(25);
		dataSeriesQueue.getData().add(queueData);

		scheduleStorage.getData().setAll(dataSeriesDisk,dataSeriesArray,dataSeriesQueue);

		PieChart.Data pDiskData = new PieChart.Data("Disk",50);
		PieChart.Data pArrayData = new PieChart.Data("Array",30);
		PieChart.Data pQueueData = new PieChart.Data("Queue",20);
		storageBreakdown.getData().setAll(pDiskData,pArrayData,pQueueData);

		Thread th = new Thread(() -> {
			Main.startAlgorithm(
					new RuntimeMonitorAggregator(
							new CLIRuntimeMonitor(Main.config.P()), 
							monitor));
		});
		th.setName("Algorithm-thread");
		th.start();

		TimerTask updateStatisticsTask = new TimerTask() {
			@Override
			public void run() {

				int schedulesOnDisk = monitor.getSchedulesOnDisk();
				int schedulesInArray = monitor.getSchedulesInArray();
				int schedulesInQueue = monitor.getSchedulesInQueue();

				long totalSchedules = schedulesOnDisk+schedulesInArray+schedulesInQueue;
				
				if(totalSchedules == 0) {
					totalSchedules += 1;
				}

				int scheduleOnDiskSize = monitor.getScheduleOnDiskStorageSize();
				int scheduleInArraySize = monitor.getScheduleInArrayStorageSize();
				int scheduleInQueueSize = monitor.getScheduleInQueueStorageSize();

				long onDiskSize = scheduleOnDiskSize*schedulesOnDisk;
				long inArraySize = scheduleInArraySize*schedulesInArray;
				long inQueueSize = scheduleInQueueSize*schedulesInQueue;

				long totalSize = onDiskSize+inArraySize+inQueueSize;

				diskData.setYValue(schedulesOnDisk/totalSchedules);
				arrayData.setYValue(schedulesInArray/totalSchedules);
				queueData.setYValue(schedulesInQueue/totalSchedules);

				pDiskData.setPieValue(schedulesOnDisk/totalSchedules);
				pArrayData.setPieValue(schedulesInArray/totalSchedules);
				pQueueData.setPieValue(schedulesInQueue/totalSchedules);

			}
		};

		Timer timer = new Timer();
		timer.schedule(updateStatisticsTask, 0l,200l);
		
	}

}
