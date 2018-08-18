package se306group8.scheduleoptimizer.visualisation.manager;

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
	private long lastScheduleCount;
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
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		long schedulesExplored = monitor.getSchedulesExplored();

		long schedulesInArray = monitor.getSchedulesInArray();
		long schedulesInQueue = monitor.getSchedulesInQueue();
		long schedulesOnDisk = monitor.getSchedulesOnDisk();

		long currentSampleTime = System.nanoTime();
		long newScheduleCount = schedulesExplored - lastScheduleCount;
		long timeSinceLastSampleTime = currentSampleTime - lastScheduleCountSampleTime;

		//If there is a stupidly small value just duplicate the previous value
		double sampleSchedulesPerSecond = timeSinceLastSampleTime < 10 ? schedulesPerSecond : 1.0e9 * newScheduleCount / timeSinceLastSampleTime;
		//double sampleSchedulesPerSecond = (double)newScheduleCount / (double)timeSinceLastSampleTime;

		schedulesPerSecond = (1-schedulesPerSecondAdjustmentFactor)*schedulesPerSecond
				+ schedulesPerSecondAdjustmentFactor*sampleSchedulesPerSecond;

		lastScheduleCountSampleTime = currentSampleTime;
		lastScheduleCount = schedulesExplored;

		schedulesExploredLabel.textProperty().setValue(HumanReadableFormatter.format(schedulesExplored," "));
		schedulesInArrayLabel.textProperty().setValue(HumanReadableFormatter.format(schedulesInArray," "));
		schedulesInQueueLabel.textProperty().setValue(HumanReadableFormatter.format(schedulesInQueue," "));
		schedulesOnDiskLabel.textProperty().setValue(HumanReadableFormatter.format(schedulesOnDisk," "));
		schedulesPerSecondLabel.textProperty().setValue(HumanReadableFormatter.format((int)schedulesPerSecond," "));
	}
}
