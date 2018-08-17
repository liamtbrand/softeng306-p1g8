package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.manager.*;

import java.net.URL;
import java.util.*;

public class DashboardPageController extends Controller {

	@FXML private Label algorithmRuntimeLabel;
	@FXML private Label algorithmLabel;
	@FXML private Label parallelizedLabel;

	@FXML private Label schedulesExploredLabel;
	@FXML private Label schedulesInArrayLabel;
	@FXML private Label schedulesInQueueLabel;
	@FXML private Label schedulesOnDiskLabel;
	@FXML private Label schedulesPerSecondLabel;

	@FXML private Label availableProcessorsLabel;
	@FXML private Label maxMemoryLabel;
	@FXML private Label usedMemoryLabel;
	@FXML private Label freeMemoryLabel;

	@FXML private PieChart storageBreakdown;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Create our managers for the dashboard components

		startManager(new AlgorithmRuntimeManager(
				algorithmRuntimeLabel,
				algorithmLabel,
				parallelizedLabel
		), 0l, 1000l);

		startManager(new ScheduleStatisticsManager(
				schedulesExploredLabel,
				schedulesInArrayLabel,
				schedulesInQueueLabel,
				schedulesOnDiskLabel,
				schedulesPerSecondLabel,
				1
		),0l,1000l);

		startManager(new PieChartManager(
				storageBreakdown
		), 0l, 1000l);

		startManager(new MemoryStatisticsManager(
				maxMemoryLabel,
				usedMemoryLabel,
				freeMemoryLabel
		),0l,1000l);

		startManager(new ProcessorStatisticsManager(
				availableProcessorsLabel
		),0l,1000l);

	}

}
