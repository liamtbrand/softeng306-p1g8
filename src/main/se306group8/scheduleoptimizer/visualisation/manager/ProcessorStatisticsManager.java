package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class ProcessorStatisticsManager {

	private Label availableProcessorsLabel;

	private Runtime runtime;

	public ProcessorStatisticsManager(Label availableProcessorsLabel) {
		this.availableProcessorsLabel = availableProcessorsLabel;
		runtime = Runtime.getRuntime();
	}

	public void update() {
		int availableProcessors = runtime.availableProcessors();
		Platform.runLater(() -> {
			availableProcessorsLabel.textProperty().setValue(""+availableProcessors);
		});
	}

}
