package se306group8.scheduleoptimizer.visualisation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import se306group8.scheduleoptimizer.Main;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitorAggregator;
import se306group8.scheduleoptimizer.algorithm.CLIRuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.manager.*;

import java.util.Timer;
import java.util.TimerTask;

public class FXApplication extends Application {

	private Thread algorithmThread;
	private Timer timer;

	@FXML private Label schedulesExploredLabel;
	@FXML private Label schedulesInArrayLabel;
	@FXML private Label schedulesInQueueLabel;
	@FXML private Label schedulesOnDiskLabel;
	@FXML private Label schedulesPerSecondLabel;

	@FXML private Label availableProcessorsLabel;
	@FXML private Label maxMemoryLabel;
	@FXML private Label usedMemoryLabel;
	@FXML private Label freeMemoryLabel;

	@FXML private StackedBarChart scheduleStorage;
	@FXML private PieChart storageBreakdown;
	
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

		ScheduleStatisticsManager scheduleStatisticsManager = new ScheduleStatisticsManager(
				schedulesExploredLabel,
				schedulesInArrayLabel,
				schedulesInQueueLabel,
				schedulesOnDiskLabel,
				schedulesPerSecondLabel,
				1
		);

		StackedBarChartManager stackedBarChartManager = new StackedBarChartManager(
				scheduleStorage
		);

		PieChartManager pieChartManager = new PieChartManager(
				storageBreakdown
		);

		MemoryStatisticsManager memoryStatisticsManager = new MemoryStatisticsManager(
				maxMemoryLabel,
				usedMemoryLabel,
				freeMemoryLabel
		);

		ProcessorStatisticsManager processorStatisticsManager = new ProcessorStatisticsManager(
				availableProcessorsLabel
		);

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

					int schedulesExplored = monitor.getSchedulesExplored();

					int schedulesInArray = monitor.getSchedulesInArray();
					int schedulesInQueue = monitor.getSchedulesInQueue();
					int schedulesOnDisk = monitor.getSchedulesOnDisk();

					long totalSchedules = schedulesInArray + schedulesInQueue + schedulesOnDisk;

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

					scheduleStatisticsManager.update(
							schedulesExplored,
							schedulesInArray,
							schedulesInQueue,
							schedulesOnDisk
					);

					double percentOnDisk = 100.0 * schedulesOnDisk / totalSchedules;
					double percentInArray = 100.0 * schedulesInArray / totalSchedules;
					double percentInQueue = 100.0 * schedulesInQueue / totalSchedules;

					stackedBarChartManager.update(percentOnDisk,percentInArray,percentInQueue);

					pieChartManager.update(percentOnDisk,percentInArray,percentInQueue);

					processorStatisticsManager.update();
					memoryStatisticsManager.update();

				});
			}
		};

		timer = new Timer();
		timer.schedule(updateStatisticsTask, 0l,1000l);

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
