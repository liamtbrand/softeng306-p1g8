package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class AlgorithmRuntimeManager {

	private Label algorithmRuntimeLabel;
	private Label algorithmLabel;
	private CheckBox parallelizedCheckbox;

	public AlgorithmRuntimeManager(Label algorithmRuntimeLabel, Label algorithmLabel, CheckBox parallelizedCheckbox) {
		this.algorithmRuntimeLabel = algorithmRuntimeLabel;
		this.algorithmLabel = algorithmLabel;
		this.parallelizedCheckbox = parallelizedCheckbox;
	}

}
