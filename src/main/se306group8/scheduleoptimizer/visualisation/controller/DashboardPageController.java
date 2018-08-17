package se306group8.scheduleoptimizer.visualisation.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.manager.AlgorithmRuntimeManager;
import se306group8.scheduleoptimizer.visualisation.manager.MemoryStatisticsManager;
import se306group8.scheduleoptimizer.visualisation.manager.PieChartManager;
import se306group8.scheduleoptimizer.visualisation.manager.ProcessorStatisticsManager;
import se306group8.scheduleoptimizer.visualisation.manager.ScheduleStatisticsManager;


public class DashboardPageController extends Controller {

	@FXML private Label algorithmRuntimeLabel;
	@FXML private Label algorithmLabel;
	@FXML private CheckBox parallelizedCheckbox;

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
	public void setup() {

		// Create our managers for the dashboard components

		startManager(new AlgorithmRuntimeManager(
				algorithmRuntimeLabel,
				algorithmLabel,
				parallelizedCheckbox
		));

		startManager(new ScheduleStatisticsManager(
				schedulesExploredLabel,
				schedulesInArrayLabel,
				schedulesInQueueLabel,
				schedulesOnDiskLabel,
				schedulesPerSecondLabel,
				1
		));

		startManager(new PieChartManager(
				storageBreakdown
		));

		startManager(new MemoryStatisticsManager(
				maxMemoryLabel,
				usedMemoryLabel,
				freeMemoryLabel
		));

		startManager(new ProcessorStatisticsManager(
				availableProcessorsLabel
		));

	}

}
