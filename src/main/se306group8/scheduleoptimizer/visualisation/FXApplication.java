package se306group8.scheduleoptimizer.visualisation;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import se306group8.scheduleoptimizer.Main;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitorAggregator;
import se306group8.scheduleoptimizer.algorithm.CLIRuntimeMonitor;

public class FXApplication extends Application {

	@FXML
	private Label myLabel;
	
	private ObservableRuntimeMonitor monitor;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		monitor = new ObservableRuntimeMonitor();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setController(this);
		loader.setLocation(getClass().getResource("/fxml/MainWindow.fxml"));
		
		Parent root = loader.load();
		Scene scene = new Scene(root);
		
		primaryStage.setTitle("Visulalisation");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		myLabel.textProperty().bind(Bindings.createStringBinding(() -> monitor.hasStarted() ? "Started!" : "Waiting to start.", monitor ));

		Thread th = new Thread(() -> {
			Main.startAlgorithm(
					new RuntimeMonitorAggregator(
							new CLIRuntimeMonitor(Main.config.P()), 
							monitor));
		});
		th.setName("Algorithm-thread");
		th.start();
		
	}

}
