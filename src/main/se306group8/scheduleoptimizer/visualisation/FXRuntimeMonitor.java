package se306group8.scheduleoptimizer.visualisation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Schedule;

public class FXRuntimeMonitor extends Application implements RuntimeMonitor {
	
	@Override
	public void updateBestSchedule(TreeSchedule optimalSchedule) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		
		// Run visualisation on new thread to prevent other code waiting
		new Thread() {
            @Override
            public void run() {
            	// Luanch the fx application
                FXRuntimeMonitor.launch(FXRuntimeMonitor.class);
            }
        }.start();
	}

	@Override
	public void finish(Schedule solution) {
		
	}

	@Override
	public void logMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Parent root = FXMLLoader.load(getClass().getResource("Visualisation.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Visulalisation");
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}

}
