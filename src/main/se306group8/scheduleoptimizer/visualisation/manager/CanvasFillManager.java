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
	
	// Coordinates for starting point of rendering searchSpace
	private double startPointX;
	private double startPointY;
	
	// Required for multiple methods within the CanvasFillManager
	private double totalTriangleWidth;
	private double totalTriangleHeight;
	
	private boolean keepDrawing = true;

	public CanvasFillManager(Canvas canvas) {
		this.canvas = canvas;
		
		// Computed from chosen points of triangle
		this.startPointX = canvas.getWidth()/2.0;
		this.startPointY = canvas.getHeight()/8.0;
		
		this.totalTriangleWidth = 4.0*canvas.getWidth()/6.0;
		this.totalTriangleHeight = 6.0*canvas.getHeight()/8.0;
	}
	
	
	@Override
	protected void updateHook() {
		ObservableRuntimeMonitor monitor = FXApplication.getMonitor();
		
		// Called and run once every second
		Platform.runLater(() -> {
			
			// Currently a debug line
			System.out.println("RUNTIME: " + monitor.getBestSchedule().getRuntime() + ", IS COMPLETE: " + monitor.getBestSchedule().isComplete());
			
			Object[] coordinates = scheduleToPixels(monitor.getBestSchedule(), monitor.getNumberOfProcessors());
			
			int lineWidth = 1;
			
			// Method call to draw out a given partial/full schedule (red if incomplete, green if complete
			if (keepDrawing) {
				if (monitor.getBestSchedule().isComplete()) {
					drawPixels(this.canvas, Color.GREEN, (int [])coordinates[0], (int [])coordinates[1], lineWidth);
					this.keepDrawing = false;
				} else {
					drawPixels(this.canvas, Color.RED, (int [])coordinates[0], (int [])coordinates[1], lineWidth);
				}
			} else {
				// Stop drawing
			}
			
		});
	}
	
	// Method to draw a set of dots, and interconnected lines, from arrays passed to it (representing a schedule)
	private void drawPixels(Canvas canvas, Color color, int[] x, int[] y, int width) {
		PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
		
		// Loop through all points
		for (int i = 0; i < x.length; i++) {
			System.out.println("X: " + x[i] + "\tY: " + y[i]);
			// Both draw a point, then a line to the next point, as you traverse coordinates
			pixelWriter.setColor(x[i], y[i], color);
			
			if (i != x.length - 1) {
				drawLine(x[i], y[i], x[i+1], y[i+1], color, width);
			} else {
				
			}
		}
	}
	
	
	// Method that translates an input partial schedule, into a series of x/y coordinates
	// representing task allocations at given points
    private Object[] scheduleToPixels(TreeSchedule schedule, int numberOfProcessors) {
    	
    	// Number to aid in space partitioning
    	int totalNumberOfTasks = schedule.getGraph().getAll().size();
    	
    	// Get currently allocated tasks
    	Collection<Task> allocations = schedule.getAllocated();
    	
    	double heightIncrement = this.totalTriangleHeight/(double)totalNumberOfTasks;
    	
    	// Represents current depth down the graph
    	double depth = 0.0;
    	
    	// Arrays to buffer pixel coordinates into
    	int[] xValues = new int[allocations.size()];
		int[] yValues = new int[allocations.size()];
		
		double xCoord;
		double yCoord = this.startPointY;
		double range;
    	
		int i = 0;
		
		// Loop through all allocations, setting coordinates for each
    	for (Task t : allocations) {
    		ProcessorAllocation allocation = schedule.getAllocationFor(t);
    		
    		double partitionLength = horizontalLength(depth);
    		
    		System.out.println("HEIGHT COORD IS: " + yCoord);
    		
    		range = (canvas.getWidth()/2.0 + partitionLength/2.0) - (canvas.getWidth()/2.0 - partitionLength/2.0) + 1.0;
    		xCoord = (int)(Math.random()*range) + (canvas.getWidth()/2.0 - partitionLength/2.0);
    		
    		xValues[i] = (int)xCoord;
    		yValues[i] = (int)yCoord;
    		
    		i++;

    		yCoord+=heightIncrement;
    		depth+=heightIncrement;
    	}
     	
    	
    	
//		for (int j = 0; j < 1000; j++) {
//			xValues[j] = (int) Math.floor(Math.random() * 641);
//			yValues[j] = (int) Math.floor(Math.random() * 368);
//		}
		
    	return new Object[]{xValues, yValues};
    }
    
    // Calculates the horizontal length by which to section up, at any given depth in the triangle
    private double horizontalLength(double depth) {
    	
    	double bottomDegree = Math.tan(this.totalTriangleHeight/(this.totalTriangleWidth*2));
    	
    	double currentWidth=depth*bottomDegree;
    	
    	System.out.println("CURRENT WIDTH: " + currentWidth + "\tAT DEPTH OF: " + depth + ", WITH CONST DEGREE OF: " + bottomDegree);

    	return currentWidth*2.0;
    }
    
    // Method to draw line with input color/width
    private void drawLine(double x1, double y1, double x2, double y2, Color color, int width) {
    	this.canvas.getGraphicsContext2D().setLineWidth(width);
    	this.canvas.getGraphicsContext2D().setStroke(color);
    	this.canvas.getGraphicsContext2D().strokeLine(x1, y1, x2, y2);
    }
    
    

}