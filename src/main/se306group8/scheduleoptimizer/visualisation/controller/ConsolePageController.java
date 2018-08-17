package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.manager.ConsoleManager;

public class ConsolePageController extends Controller {
	@FXML
	private TextArea consoleTextArea;

	@Override
	public void setup() {
		startManager(new ConsoleManager(consoleTextArea));

		FXApplication.getMonitor().logMessage("Console Manager has been started.");

	}

}
