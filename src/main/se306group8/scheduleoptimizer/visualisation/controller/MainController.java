package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The purpose of main controller is to keep references to the controllers for each page.
 */
public class MainController implements Initializable {

	// The controllers are injected here. Convention is: fx:id+Controller.
	@FXML
	DashboardPageController dashboardPageController;
	@FXML
	TaskGraphPageController taskGraphPageController;
	@FXML
	SearchSpacePageController searchSpacePageController;
	@FXML
	ConsolePageController consolePageController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	/**
	 * This method is called by application when the visualiser is closed.
	 * This must call stop on all the children controllers.
	 * Each child should ensure that they clean up any threads they have created.
	 * This includes timers.
	 */
	public void stop() {
		dashboardPageController.stop();
		taskGraphPageController.stop();
		searchSpacePageController.stop();
		consolePageController.stop();
	}

}
