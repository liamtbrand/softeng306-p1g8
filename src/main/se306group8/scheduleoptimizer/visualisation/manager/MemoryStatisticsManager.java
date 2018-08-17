package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class MemoryStatisticsManager extends Manager {

	private Label maxMemoryLabel;
	private Label usedMemoryLabel;
	private Label freeMemoryLabel;

	private Runtime runtime;

	public MemoryStatisticsManager(Label maxMemoryLabel, Label usedMemoryLabel, Label freeMemoryLabel) {

		this.maxMemoryLabel = maxMemoryLabel;
		this.usedMemoryLabel = usedMemoryLabel;
		this.freeMemoryLabel = freeMemoryLabel;

		runtime = Runtime.getRuntime();

	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		double freeMemory = runtime.freeMemory() / 1_000_000.0;
		double maxMemory = runtime.maxMemory() / 1_000_000.0;
		double usedMemory = maxMemory - freeMemory;

		freeMemoryLabel.textProperty().setValue((int) freeMemory + " MB");
		maxMemoryLabel.textProperty().setValue((int) maxMemory + " MB");
		usedMemoryLabel.textProperty().setValue((int) usedMemory + " MB");
	}

}
