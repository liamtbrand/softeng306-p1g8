package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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

public class CanvasFillManager extends Manager {
	
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
		
		if (monitor.getBestSchedule() == null) {
			return;
		}
		
		// Called and run once every second
		Platform.runLater(() -> {
			
			/*
			double[][] coordinates = scheduleToPixels(monitor.getBestSchedule(), monitor.getNumberOfProcessors());
		
			// Method call to draw out a given partial/full schedule (red if incomplete, green if complete)
			if (keepDrawing) {
				if (FXApplication.getMonitor().hasFinished()) {
					drawPixels(this.canvas, Color.DARKBLUE, coordinates[0], coordinates[1], 3);
					this.keepDrawing = false;
				} else {
					drawPixels(this.canvas, Color.GREY, coordinates[0], coordinates[1], 1);
				}
			} else {
				// Stop drawing
			}
			
			*/
			
		});
	}
	
	// Method that translates an input partial schedule, into a series of x/y coordinates
	// representing task allocations at given points
    private double[][] scheduleToPixels(TreeSchedule schedule, int numberOfProcessors) {
    	
    	int i = schedule.getGraph().getAll().size();
    	
    	// Arrays to buffer pixel coordinates into (xValues are 0 - 1, yValues are 1 - numberOfTasks)
    	double[] xValues = new double[i + 1];
		double[] yValues = new double[i + 1];
		
		// Setting first points
		double xCoord = this.startPointX;
		double yCoord = this.startPointY;
		
		xValues[0] = 0;
		yValues[0] = 0;
	
		// COULD BE BUGGY (BY 1)
		double heightIncrement = this.totalTriangleHeight/(i + 1);
		
		TreeSchedule s = schedule;
		
		// Recursively go back up schedule via parents, plotting points at each stage
    	while (!s.getParent().isEmpty()) {
    		
    		xValues[i] = scheduleToPoint(s);
    		yValues[i] = schedule.getAllocated().size()*heightIncrement;
    		
    		// Move up schedule (to parent)
    		s = s.getParent();
    		i--;
    	}
    	
    	return new double[][]{xValues, yValues};
    }
    
    // Convert a given schedule (with a certain number of tasks), to a
    // point between zero and one.
    private double scheduleToPoint(TreeSchedule schedule) {
    	
    	int numberOfOptions = schedule.getAllocated().size()*FXApplication.getMonitor().getNumberOfProcessors();
    	
    	double value = 0.0;
    	double horizontalLength = horizontalLength(schedule.getAllocated().size()*(this.totalTriangleHeight/schedule.getGraph().getAll().size()));
    	
    	for (Task t : schedule.getAllocated()) {
    		ProcessorAllocation allocation = schedule.getAllocationFor(t);
    		
    		
    	}
    	
    	return 0.0;
    }
    
    // Convert allocation to number (as per James' pseudocode)
    private double convertToNumber(ProcessorAllocation allocation, int totalNumberOfTasks) {
    	return (allocation.processor - 1)/FXApplication.getMonitor().getNumberOfProcessors() + allocation.task.getId()/(FXApplication.getMonitor().getNumberOfProcessors()*totalNumberOfTasks);
    }
    
    // Converts taskNumber to a given y-coordinate, (with respect to the top tip of the triangle) 
    private double taskNumberToDepth(int taskNumber, int totalTasks) {
    	return (this.totalTriangleHeight/totalTasks)*taskNumber;
    }
    
    // Calculates the horizontal length by which to section up, at any given depth in the triangle
    private double horizontalLength(double depth) {
    	double ratio = this.totalTriangleWidth/this.totalTriangleHeight;	
    	return depth*ratio;
    }
    
	// Method to draw a set of dots, and interconnected lines, from arrays passed to it (representing a schedule)
	private void drawPixels(Canvas canvas, TreeSchedule schedule, Color color, double[] x, double[] y, int width) {
		PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
		
		int xCoord;
		int yCoord;
		
		// Loop through all points
		for (int i = 0; i < x.length; i++) {
			
			// Both draw a point, then a line to the next point, as you traverse coordinates
			pixelWriter.setColor((int)x[i], (int)y[i], color);
			if ((i + 1) == x.length) {
			} else {
				drawLine(x[i], y[i], x[i+1], y[i+1], color, width);
			}
		}
	}
    
    // Method to draw line with input color/width
    private void drawLine(double x1, double y1, double x2, double y2, Color color, int width) {
    	this.canvas.getGraphicsContext2D().setLineWidth(width);
    	this.canvas.getGraphicsContext2D().setStroke(color);
    	this.canvas.getGraphicsContext2D().strokeLine(x1, y1, x2, y2);
    }
}