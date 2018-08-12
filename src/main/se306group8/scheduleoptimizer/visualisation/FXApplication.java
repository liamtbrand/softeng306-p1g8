package se306group8.scheduleoptimizer.visualisation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se306group8.scheduleoptimizer.Main;
import se306group8.scheduleoptimizer.algorithm.RuntimeMonitorAggregator;
import se306group8.scheduleoptimizer.algorithm.CLIRuntimeMonitor;
import se306group8.scheduleoptimizer.visualisation.controller.MainController;

public class FXApplication extends Application {

	private Thread algorithmThread;

	MainController mainController;
	
	private static ObservableRuntimeMonitor monitor;

	/**
	 * Controllers use this to get the runtime monitor.
	 * This might need to be rethought...
	 * @return
	 */
	public static ObservableRuntimeMonitor getMonitor() {
		return monitor;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		monitor = new ObservableRuntimeMonitor();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/MainWindow.fxml"));
		
		Parent root = loader.load();
		Scene scene = new Scene(root);

		mainController = loader.<MainController>getController();
		
		primaryStage.setTitle("Visulalisation");
		primaryStage.setScene(scene);
		primaryStage.show();

		Thread th = new Thread(() -> {
			Main.startAlgorithm(
					new RuntimeMonitorAggregator(
							new CLIRuntimeMonitor(Main.config.P()), 
							monitor));
		});
		th.setName("Algorithm-thread");
		th.start();
		algorithmThread = th;

	}

	@Override
	public void stop() {
		mainController.stop();
		algorithmThread.interrupt();
		try {
			algorithmThread.join();
		} catch (InterruptedException e) {
			return;
		}
	}

}
