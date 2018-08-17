package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import se306group8.scheduleoptimizer.visualisation.FXApplication;

public class AlgorithmRuntimeManager extends Manager {

	private Label algorithmRuntimeLabel;
	private Label algorithmLabel;
	private CheckBox parallelizedCheckbox;

	public AlgorithmRuntimeManager(Label algorithmRuntimeLabel, Label algorithmLabel, CheckBox parallelizedCheckbox) {
		this.algorithmRuntimeLabel = algorithmRuntimeLabel;
		this.algorithmLabel = algorithmLabel;
		this.parallelizedCheckbox = parallelizedCheckbox;
	}

	@Override
	protected void updateHook() {

	}
}
