package se306group8.scheduleoptimizer.visualisation.manager;

import java.time.Duration;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class AlgorithmRuntimeManager extends Manager {
	private long startTime = -1;
	private boolean startDetected = false;
	
	private Label algorithmRuntimeLabel;
	private Label algorithmLabel;
	private Label parallelizedLabel;

	public AlgorithmRuntimeManager(Label algorithmRuntimeLabel, Label algorithmLabel, Label parallelizedLabel) {
		this.algorithmRuntimeLabel = algorithmRuntimeLabel;
		this.algorithmLabel = algorithmLabel;
		this.parallelizedLabel = parallelizedLabel;
	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		algorithmLabel.textProperty().setValue(monitor.getAlgorithmName());
		int cores = monitor.getCoresToUseForExecution();
		parallelizedLabel.textProperty().setValue(cores + (cores > 1 ? " Cores" : " Core"));
		
		if(!startDetected) {
			startTime = System.currentTimeMillis();
			startDetected = true;
		}
		
		if(monitor.hasStarted() && !monitor.hasFinished()) {
			Duration timeTaken = Duration.ofMillis(System.currentTimeMillis() - startTime);
			
			algorithmRuntimeLabel.textProperty().set(String.format("%dm %ds", timeTaken.getSeconds() / 60, timeTaken.getSeconds() % 60));
		}
	}
}
