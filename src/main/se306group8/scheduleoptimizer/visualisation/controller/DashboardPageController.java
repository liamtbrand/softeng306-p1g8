package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.manager.MemoryStatisticsManager;
import se306group8.scheduleoptimizer.visualisation.manager.PieChartManager;
import se306group8.scheduleoptimizer.visualisation.manager.ProcessorStatisticsManager;
import se306group8.scheduleoptimizer.visualisation.manager.ScheduleStatisticsManager;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardPageController implements Initializable {

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

	@FXML private PieChart storageBreakdown;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Create our managers for the dashboard components

		ScheduleStatisticsManager scheduleStatisticsManager = new ScheduleStatisticsManager(
				schedulesExploredLabel,
				schedulesInArrayLabel,
				schedulesInQueueLabel,
				schedulesOnDiskLabel,
				schedulesPerSecondLabel,
				1
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

		// Create a timerTask to do the updating of our components

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

				scheduleStatisticsManager.update(
						schedulesExplored,
						schedulesInArray,
						schedulesInQueue,
						schedulesOnDisk
				);

				double percentOnDisk = 100.0 * schedulesOnDisk / totalSchedules;
				double percentInArray = 100.0 * schedulesInArray / totalSchedules;
				double percentInQueue = 100.0 * schedulesInQueue / totalSchedules;

				pieChartManager.update(percentOnDisk,percentInArray,percentInQueue);

				processorStatisticsManager.update();
				memoryStatisticsManager.update();

			}
		};

		timer = new Timer();
		timer.schedule(updateStatisticsTask, 0l,1000l);

	}

	public void stop() {
		timer.cancel();
	}

}
