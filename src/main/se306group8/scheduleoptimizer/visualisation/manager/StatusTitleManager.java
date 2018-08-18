package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class StatusTitleManager extends Manager {

	private Label statusLabel;
	private Label graphNameLabel;
	
	public StatusTitleManager(Label statusLabel, Label graphNameLabel) {
		this.statusLabel = statusLabel;
		this.graphNameLabel = graphNameLabel;
	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {

		Platform.runLater(() -> {
			if (monitor.hasFinished()) {
				this.statusLabel.textProperty().setValue("COMPLETE");
				this.statusLabel.setId("complete-label");
				this.graphNameLabel.textProperty().setValue(monitor.getBestSchedule().getGraph().getName());
				this.graphNameLabel.setId("complete-label");
			} else {
				this.statusLabel.textProperty().setValue("LOADING");
				this.statusLabel.setId("incomplete-label");
				this.graphNameLabel.textProperty().setValue(monitor.getBestSchedule().getGraph().getName());
				this.graphNameLabel.setId("incomplete-label");
			}
		});
		
	}

}
