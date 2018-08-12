package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	@FXML DashboardController dashboardPageController;
	@FXML StatisticsController statisticsPageController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void stop() {
		dashboardPageController.stop();
		statisticsPageController.stop();
	}

}
