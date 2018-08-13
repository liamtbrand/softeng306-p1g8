package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.manager.ScheduleManager;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class TaskGraphPageController extends Controller {

	private Timer timer;

	@FXML
	private Pane pane;
	@FXML
	private VBox processors;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		ScheduleManager scheduleManager = new ScheduleManager(
				pane, processors
		);

		TimerTask updateScheduleTask = new TimerTask() {
			@Override
			public void run() {
				ObservableRuntimeMonitor monitor = FXApplication.getMonitor();

				TreeSchedule bestSchedule = monitor.getBestSchedule();
				if (bestSchedule != null) {
				scheduleManager.update(bestSchedule);
				}
			}
		};

		timer = new Timer();
		timer.schedule(updateScheduleTask, 0l,1000l);

	}

}
