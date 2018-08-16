package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.HumanReadableFormatter;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class ScheduleStatisticsManager extends Manager {

	private Label schedulesExploredLabel;
	private Label schedulesInArrayLabel;
	private Label schedulesInQueueLabel;
	private Label schedulesOnDiskLabel;
	private Label schedulesPerSecondLabel;

	private double schedulesPerSecond;
	private int lastScheduleCount;
	private long lastScheduleCountSampleTime;
	private double schedulesPerSecondAdjustmentFactor; // Applied for smoothing.

	public ScheduleStatisticsManager(
			Label schedulesExploredLabel,
			Label schedulesInArrayLabel,
			Label schedulesInQueueLabel,
			Label schedulesOnDiskLabel,
			Label schedulesPerSecondLabel,
			double schedulesPerSecondAdjustmentFactor
	) {

		this.schedulesExploredLabel = schedulesExploredLabel;
		this.schedulesInArrayLabel = schedulesInArrayLabel;
		this.schedulesInQueueLabel = schedulesInQueueLabel;
		this.schedulesOnDiskLabel = schedulesOnDiskLabel;
		this.schedulesPerSecondLabel = schedulesPerSecondLabel;

		this.schedulesPerSecondAdjustmentFactor = schedulesPerSecondAdjustmentFactor;

		// Setup schedules per second data.
		lastScheduleCount = 0;
		schedulesPerSecond = 0;
		lastScheduleCountSampleTime = System.nanoTime();

	}

	@Override
	protected void updateHook() {

		ObservableRuntimeMonitor monitor = FXApplication.getMonitor();

		int schedulesExplored = monitor.getSchedulesExplored();

		int schedulesInArray = monitor.getSchedulesInArray();
		int schedulesInQueue = monitor.getSchedulesInQueue();
		int schedulesOnDisk = monitor.getSchedulesOnDisk();

		long currentSampleTime = System.nanoTime();
		int newScheduleCount = schedulesExplored - lastScheduleCount;
		long timeSinceLastSampleTime = currentSampleTime - lastScheduleCountSampleTime;

		//If there is a stupidly small value just duplicate the previous value
		double sampleSchedulesPerSecond = timeSinceLastSampleTime < 10 ? schedulesPerSecond : 1.0e9 * newScheduleCount / timeSinceLastSampleTime;

		schedulesPerSecond = (1-schedulesPerSecondAdjustmentFactor)*schedulesPerSecond
				+ schedulesPerSecondAdjustmentFactor*sampleSchedulesPerSecond;

		lastScheduleCountSampleTime = currentSampleTime;
		lastScheduleCount = schedulesExplored;

		Platform.runLater(() -> {

			schedulesExploredLabel.textProperty().setValue(""+schedulesExplored);
			schedulesInArrayLabel.textProperty().setValue(""+schedulesInArray);
			schedulesInQueueLabel.textProperty().setValue(""+schedulesInQueue);
			schedulesOnDiskLabel.textProperty().setValue(""+schedulesOnDisk);
			schedulesPerSecondLabel.textProperty().setValue(""+(int)schedulesPerSecond);
		});
	}
}
