package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.StackedBarChart;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.manager.StackedBarChartManager;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class StatisticsPageController implements Initializable {

	private Timer timer;

	@FXML
	private StackedBarChart scheduleStorage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		StackedBarChartManager stackedBarChartManager = new StackedBarChartManager(
				scheduleStorage
		);

		TimerTask updateStatisticsTask = new TimerTask() {
			@Override
			public void run() {

				ObservableRuntimeMonitor monitor = FXApplication.getMonitor();

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

				double percentOnDisk = 100.0 * schedulesOnDisk / totalSchedules;
				double percentInArray = 100.0 * schedulesInArray / totalSchedules;
				double percentInQueue = 100.0 * schedulesInQueue / totalSchedules;

				stackedBarChartManager.update(percentOnDisk,percentInArray,percentInQueue);

			}
		};

		timer = new Timer();
		timer.schedule(updateStatisticsTask, 0l,1000l);

	}

	public void stop() {
		timer.cancel();
	}

}
