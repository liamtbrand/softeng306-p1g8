package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.Collection;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class HistogramManager extends Manager {
	private final BarChart<String, Number> chart;
	
	private final Label label;
	private final Label noDataLabel;
	
	public HistogramManager(BarChart<String, Number> chart, Label label, Label noDataLabel) {
		this.chart = chart;
		this.label = label;
		this.noDataLabel = noDataLabel;
	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		Collection<Data<String, Number>> col = monitor.getHistogramData();
		
		if (col.isEmpty()) {
			noDataLabel.visibleProperty().setValue(true);
		} else {
			noDataLabel.visibleProperty().setValue(false);
		}
		
		chart.getData().get(0).getData().setAll(col);
	}
}
