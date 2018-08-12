package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class MemoryStatisticsManager {

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

	public void update() {
		double freeMemory = runtime.freeMemory() / 1_000_000.0;
		double maxMemory = runtime.maxMemory() / 1_000_000.0;
		double usedMemory = maxMemory - freeMemory;

		Platform.runLater(() -> {
			freeMemoryLabel.textProperty().setValue((int) freeMemory + " MB");
			maxMemoryLabel.textProperty().setValue((int) maxMemory + " MB");
			usedMemoryLabel.textProperty().setValue((int) usedMemory + " MB");
		});
	}

}
