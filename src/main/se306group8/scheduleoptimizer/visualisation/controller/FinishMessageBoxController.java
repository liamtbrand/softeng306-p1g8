package se306group8.scheduleoptimizer.visualisation.controller;

import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import se306group8.scheduleoptimizer.taskgraph.Schedule;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class FinishMessageBoxController implements InvalidationListener {
	private boolean hasShown = false;
	private final VBox root;
	
	@FXML
	private Label introText, timeTaken, solutionLength;
	
	public FinishMessageBoxController() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/DialogBox.fxml"));
		loader.setController(this);
		
		try {
			root = loader.load();
		} catch (IOException e) {
			throw new RuntimeException("Dialog FXML failed to load", e);
		}
	}
	
	@Override
	public void invalidated(Observable observable) {
		ObservableRuntimeMonitor monitor = (ObservableRuntimeMonitor) observable;
		
		if(monitor.hasFinished() && !hasShown) {
			hasShown = true;
			Schedule solution = monitor.getFinishedSolution();
			
			introText.setText("Graph complete!");
			timeTaken.setText(String.format("%.1fs", monitor.timeTaken() / 1000.0));
			solutionLength.setText(String.format("%d", solution.getTotalRuntime()));
			
			Alert alert = new Alert(AlertType.NONE, 
					"Computation finished, with a runtime of " + solution.getTotalRuntime(), ButtonType.CLOSE);
			
			alert.setTitle("Finished!");
			alert.getDialogPane().setContent(root);
			
			alert.show();
		}
	}
}
