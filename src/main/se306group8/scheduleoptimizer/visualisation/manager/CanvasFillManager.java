package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class CanvasFillManager extends ManagerThread {
	
	private Canvas canvas;
	
	private GraphicsContext gc;
	
	static int size = 1;

	public CanvasFillManager(Canvas canvas) {
		this.canvas = canvas;
	}
	
	@Override
	protected void updateHook() {
		ObservableRuntimeMonitor monitor = FXApplication.getMonitor();
		
		
		Platform.runLater(() -> {
			draw(this.canvas);
		});
	}
	
	// Method to fill in, that UPDATES the information within the triangle
    private void draw(Canvas canvas) {
    	
    	GraphicsContext gc = canvas.getGraphicsContext2D();
    	double height = canvas.getHeight(), width = canvas.getWidth();
    	 
        gc.setFill(Color.ORANGE);
        gc.setStroke(Color.ORANGE);
        
        gc.fillRect(size, size, size, size);

        size*=2;
      }

}
