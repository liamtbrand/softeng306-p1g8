package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.manager.StatusTitleManager;

public class StatusTitleController extends Controller {

	@FXML private Label statusLabel;
	@FXML private Label graphNameLabel;
	
	@Override
	public void setup() {
		startManager(new StatusTitleManager(statusLabel, graphNameLabel));
	}

}
