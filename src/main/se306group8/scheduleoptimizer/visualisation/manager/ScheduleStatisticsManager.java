package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.HumanReadableFormatter;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class ScheduleStatisticsManager extends Manager {

	private Label schedulesExploredLabel;
	private Label schedulesInArrayLabel;
	private Label schedulesInQueueLabel;
	private Label schedulesPerSecondLabel;
	
	private Label lowerBoundLabel;
	private Label upperBoundLabel;
	
	private double schedulesPerSecond;
	private long lastScheduleCount;
	private long lastScheduleCountSampleTime;
	private double schedulesPerSecondAdjustmentFactor; // Applied for smoothing.

	public ScheduleStatisticsManager(
			Label schedulesExploredLabel,
			Label schedulesInArrayLabel,
			Label schedulesInQueueLabel,
			Label schedulesPerSecondLabel,
			Label lowerBoundLabel,
			Label upperBoundLabel,
			double schedulesPerSecondAdjustmentFactor
	) {

		this.schedulesExploredLabel = schedulesExploredLabel;
		this.schedulesInArrayLabel = schedulesInArrayLabel;
		this.schedulesInQueueLabel = schedulesInQueueLabel;
		this.schedulesPerSecondLabel = schedulesPerSecondLabel;
		this.lowerBoundLabel = lowerBoundLabel;
		this.upperBoundLabel = upperBoundLabel;
		
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
		if (monitor.getAlgorithmName().equals("A*")) {
			
			schedulesInArrayLabel.textProperty().setValue(HumanReadableFormatter.format(schedulesInArray," "));
			schedulesInQueueLabel.textProperty().setValue(HumanReadableFormatter.format(schedulesInQueue," "));			
		} else {
			schedulesInArrayLabel.textProperty().setValue("N/A");
			schedulesInQueueLabel.textProperty().setValue("N/A");
		}
		
		schedulesExploredLabel.textProperty().setValue(HumanReadableFormatter.format(schedulesExplored," "));
		schedulesPerSecondLabel.textProperty().setValue(HumanReadableFormatter.format((int)schedulesPerSecond," "));
		
		lowerBoundLabel.textProperty().setValue(monitor.getLowerBound() == 0 ? "No bound" : Integer.toString(monitor.getLowerBound()));
		upperBoundLabel.textProperty().setValue(monitor.getUpperBound() == Integer.MAX_VALUE ? "No bound" : Integer.toString(monitor.getUpperBound()));
	}
}
