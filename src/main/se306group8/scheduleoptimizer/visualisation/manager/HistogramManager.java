package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.Collection;

import javafx.application.Platform;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart.Data;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class HistogramManager extends Manager {
	private final BarChart<String, Number> chart;
	
	public HistogramManager(BarChart<String, Number> chart) {
		this.chart = chart;
	}

	@Override
	protected void updateHook() {
		ObservableRuntimeMonitor monitor = FXApplication.getMonitor();
		Collection<Data<String, Number>> col = monitor.getHistogramData();
		
		Platform.runLater(() -> {
			chart.getData().get(0).getData().setAll(col);
		});
	}
}
