package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class ProcessorStatisticsManager extends Manager {

	private Label availableProcessorsLabel;

	private Runtime runtime;

	public ProcessorStatisticsManager(Label availableProcessorsLabel) {
		this.availableProcessorsLabel = availableProcessorsLabel;
		runtime = Runtime.getRuntime();
	}

	protected void updateHook(ObservableRuntimeMonitor monitor) {
		int availableProcessors = runtime.availableProcessors();
		availableProcessorsLabel.textProperty().setValue(availableProcessors+"   ");
	}

}
