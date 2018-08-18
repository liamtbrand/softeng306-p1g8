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
	
	public HistogramManager(BarChart<String, Number> chart, Label label) {
		this.chart = chart;
		this.label = label;
	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		Collection<Data<String, Number>> col = monitor.getHistogramData();
		
		/*if (FXApplication.getMonitor().hasFinished()) {
			this.label.setTextFill(Color.rgb(68, 96, 140, 1.0));
		}*/
		
		chart.getData().get(0).getData().setAll(col);
	}
}
