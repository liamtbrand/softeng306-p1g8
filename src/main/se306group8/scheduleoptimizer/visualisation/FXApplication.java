package se306group8.scheduleoptimizer.visualisation;

import javafx.application.Application;
import javafx.application.Platform;
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

	private Thread algorithmThread;
	private Timer timer;

	@FXML
	private Label solutionsExploredLabel;
	@FXML
	private Label schedulesInQueueLabel;
	@FXML
	private Label schedulesInArrayLabel;
	@FXML
	private Label schedulesOnDiskLabel;

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
		
		//myLabel.textProperty().bind(Bindings.createStringBinding(() -> monitor.hasStarted() ? "Started!" : "Waiting to start.", monitor ));

		// Setup memory usage graph.

		XYChart.Series dataSeriesDisk = new XYChart.Series();
		XYChart.Series dataSeriesArray = new XYChart.Series();
		XYChart.Series dataSeriesQueue = new XYChart.Series();

		dataSeriesDisk.setName("Disk");
		dataSeriesArray.setName("Array");
		dataSeriesQueue.setName("Queue");

		XYChart.Data diskData = new XYChart.Data();
		diskData.setXValue("Memory");
		diskData.setYValue(0.1);
		dataSeriesDisk.getData().add(diskData);

		XYChart.Data arrayData = new XYChart.Data();
		arrayData.setXValue("Memory");
		arrayData.setYValue(0.1);
		dataSeriesArray.getData().add(arrayData);

		XYChart.Data queueData = new XYChart.Data();
		queueData.setXValue("Memory");
		queueData.setYValue(0.1);
		dataSeriesQueue.getData().add(queueData);

		scheduleStorage.getData().setAll(dataSeriesDisk,dataSeriesArray,dataSeriesQueue);

		PieChart.Data pDiskData = new PieChart.Data("Disk",0.1);
		PieChart.Data pArrayData = new PieChart.Data("Array",0.1);
		PieChart.Data pQueueData = new PieChart.Data("Queue",0.1);
		storageBreakdown.getData().setAll(pDiskData,pArrayData,pQueueData);

		Thread th = new Thread(() -> {
			Main.startAlgorithm(
					new RuntimeMonitorAggregator(
							new CLIRuntimeMonitor(Main.config.P()), 
							monitor));
		});
		th.setName("Algorithm-thread");
		th.start();
		algorithmThread = th;

		TimerTask updateStatisticsTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {

					long solutionsExplored = monitor.getSolutionsExplored();

					long schedulesOnDisk = monitor.getSchedulesOnDisk();
					long schedulesInArray = monitor.getSchedulesInArray();
					long schedulesInQueue = monitor.getSchedulesInQueue();

					long totalSchedules = schedulesOnDisk+schedulesInArray+schedulesInQueue;

					if(totalSchedules == 0) {
						totalSchedules += 1;
					}

					/*
					int scheduleOnDiskSize = monitor.getScheduleOnDiskStorageSize();
					int scheduleInArraySize = monitor.getScheduleInArrayStorageSize();
					int scheduleInQueueSize = monitor.getScheduleInQueueStorageSize();

					long onDiskSize = scheduleOnDiskSize*schedulesOnDisk;
					long inArraySize = scheduleInArraySize*schedulesInArray;
					long inQueueSize = scheduleInQueueSize*schedulesInQueue;

					long totalSize = onDiskSize+inArraySize+inQueueSize;
					*/

					solutionsExploredLabel.textProperty().setValue(""+solutionsExplored);

					schedulesInArrayLabel.textProperty().setValue(""+schedulesInArray);
					schedulesInQueueLabel.textProperty().setValue(""+schedulesInQueue);
					schedulesOnDiskLabel.textProperty().setValue(""+schedulesOnDisk);

					double percentOnDisk = 100.0 * schedulesOnDisk / totalSchedules;
					double percentInArray = 100.0 * schedulesInArray / totalSchedules;
					double percentInQueue = 100.0 * schedulesInQueue / totalSchedules;

					diskData.setYValue(percentOnDisk);
					arrayData.setYValue(percentInArray);
					queueData.setYValue(percentInQueue);

					pDiskData.setPieValue(percentOnDisk);
					pArrayData.setPieValue(percentInArray);
					pQueueData.setPieValue(percentInQueue);

				});
			}
		};

		timer = new Timer();
		timer.schedule(updateStatisticsTask, 0l,200l);
		
	}

	@Override
	public void stop() {
		timer.cancel();
		algorithmThread.interrupt();
		try {
			algorithmThread.join();
		} catch (InterruptedException e) {
			return;
		}
	}

}
