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

	/**
	 * Reference to the thread that is running the algorithm.
	 * This is used for cleanup.
	 */
	private Thread algorithmThread;

	/**
	 * A reference to the main controller being used.
	 * This is needed for cleanup.
	 */
	MainController mainController;
	
	private static ObservableRuntimeMonitor monitor;

	/**
	 * Controllers use this to get the runtime monitor.
	 * @return
	 */
	public static ObservableRuntimeMonitor getMonitor() {
		return monitor;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Keep a reference to the monitor.

		monitor = new ObservableRuntimeMonitor();

		// Load the FXML

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/fxml/MainWindow.fxml"));
		
		Parent root = loader.load();
		Scene scene = new Scene(root);

		// Get the reference to the main controller.
		mainController = loader.<MainController>getController();
		
		primaryStage.setTitle("Visualisation");
		primaryStage.setScene(scene);
		primaryStage.show();

		// Start the algorithm on its own thread.

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

		// Stop the main controller, this will stop everything in the gui.
		mainController.stop();

		// Stop the algorithm.
		algorithmThread.interrupt();
		try {
			algorithmThread.join();
		} catch (InterruptedException e) {
			return;
		}
	}

}
