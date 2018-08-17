package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.FXApplication;

public class AlgorithmRuntimeManager extends Manager {

	private Label algorithmRuntimeLabel;
	private Label algorithmLabel;
	private Label parallelizedLabel;

	public AlgorithmRuntimeManager(Label algorithmRuntimeLabel, Label algorithmLabel, Label parallelizedLabel) {
		this.algorithmRuntimeLabel = algorithmRuntimeLabel;
		this.algorithmLabel = algorithmLabel;
		this.parallelizedLabel = parallelizedLabel;
	}

	@Override
	protected void updateHook() {
		Platform.runLater(() -> {
			algorithmLabel.textProperty().setValue(FXApplication.getMonitor().getAlgorithmName());
			int cores = FXApplication.getMonitor().getCoresToUseForExecution();
			parallelizedLabel.textProperty().setValue(cores + (cores > 1 ? " Cores" : " Core"));
		});
	}
}
