package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import se306group8.scheduleoptimizer.algorithm.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class CanvasFillManager extends ManagerThread {
	
	private Canvas canvas;
	private GraphicsContext gc;

	public CanvasFillManager(Canvas canvas) {
		this.canvas = canvas;
	}
	
	@Override
	protected void updateHook() {
		ObservableRuntimeMonitor monitor = FXApplication.getMonitor();
		
		Platform.runLater(() -> {
			System.out.println("RUNTIME: " + monitor.getBestSchedule().getRuntime() + ", IS COMPLETE: " + monitor.getBestSchedule().isComplete());
			
			//Object[] coordinates = scheduleToPixels(monitor.getBestSchedule());
			
			//drawPixels(this.canvas, (int [])coordinates[0], (int [])coordinates[1]);
		});
	}
	
	private void drawPixels(Canvas canvas, int[] x, int[] y) {
		PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
		
		for (int i = 0; i < x.length; i++) {
			pixelWriter.setColor(x[i], y[i], Color.RED);
		}
	}
	
	// Method that translates an input partial schedule, into a series of x/y coordinates
	// representing task allocations at given points
    private Object[] scheduleToPixels(TreeSchedule schedule) {
    	
    	Collection<Task> allocations = schedule.getAllocated();
    	
    	double depth = 0.0;
    	
    	// Arrays to buffer pixel coordinates into
    	int[] xValues = new int[allocations.size()];
		int[] yValues = new int[allocations.size()];
    	
		// Loop through all allocations, setting coordinates for each
    	for (Task t : allocations) {
    		ProcessorAllocation allocation = schedule.getAllocationFor(t);
    		
    		
    	}
    
//		for (int j = 0; j < 1000; j++) {
//			xValues[j] = (int) Math.floor(Math.random() * 641);
//			yValues[j] = (int) Math.floor(Math.random() * 368);
//		}
		
    	return new Object[]{xValues, yValues};
    }
    
    // Calculates the length by which to section up, at any given depth in the triangle
    private double horizontalLength(double depth) {
    	
    	double totalTriangleWidth = 4.0*canvas.getWidth()/6.0;
    	double totalTriangleHeight = 6.0*canvas.getHeight()/8.0;
    	double bottomDegree = Math.tan(totalTriangleHeight/(totalTriangleWidth*2));
    	
    	double currentWidth= depth*bottomDegree;
 
    	return currentWidth;
    }
    
    

}