package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class StatusTitleManager extends Manager {

	//private Label statusLabel;
	private Label graphNameLabel;
	
	public StatusTitleManager(Label graphNameLabel) {
		//this.statusLabel = statusLabel;
		this.graphNameLabel = graphNameLabel;
	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		this.graphNameLabel.textProperty().setValue(monitor.getGraphName());
		
			if (monitor.hasFinished()) {
				this.graphNameLabel.setId("complete-label");
			} else {
				this.graphNameLabel.setId("incomplete-label");
			}	
	}

}
