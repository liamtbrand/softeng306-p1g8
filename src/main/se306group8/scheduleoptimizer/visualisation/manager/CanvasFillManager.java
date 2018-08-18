package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import se306group8.scheduleoptimizer.algorithm.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class CanvasFillManager extends Manager {
	
	private Canvas canvas;
	private GraphicsContext gc;
	
	private Label label;
	
	// Coordinates for starting point of rendering searchSpace
	private double startPointX;
	private double startPointY;
	
	// Required for multiple methods within the CanvasFillManager
	private double totalTriangleWidth;
	private double totalTriangleHeight;

	public CanvasFillManager(Canvas canvas, Label label) {
		this.canvas = canvas;
		this.label = label;
		this.gc = canvas.getGraphicsContext2D();
		
		// Computed from chosen points of triangle
		this.startPointX = canvas.getWidth()/2.0;
		this.startPointY = canvas.getHeight()/8.0;
		
		this.totalTriangleWidth = 4.0*canvas.getWidth()/6.0;
		this.totalTriangleHeight = 6.0*canvas.getHeight()/8.0;
	}
	
	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		
		if (monitor.getBestSchedule() == null) {
			return;
		}

		double[][] coordinates = scheduleToPixels(monitor.getBestSchedule(), monitor.getNumberOfProcessors());
		// Method call to draw out a given partial/full schedule (light blue if incomplete, green if complete)
		
		if (FXApplication.getMonitor().hasFinished()) {
			drawPixels(this.canvas, Color.rgb(0, 166, 118, 1.0), coordinates[0], coordinates[1], 3);
			//this.label.setTextFill(Color.rgb(68, 96, 140, 1.0));
		} else {
			drawPixels(this.canvas, Color.rgb(117, 149, 198, 1.0), coordinates[0], coordinates[1], 1);
		}		

	}
	
	// Method that translates an input partial schedule, into a series of x/y coordinates
	// representing task allocations at given points
    private double[][] scheduleToPixels(TreeSchedule schedule, int numberOfProcessors) {
    	
    	// List of Parents, populated in reverse order from the deepest child
    	List<TreeSchedule> parents = new ArrayList<>();
    	
    	TreeSchedule s = schedule;
    	
    	while (!s.isEmpty()) {
    		parents.add(s);
    		s = s.getParent();
    	}
    	
    	// Add empty parent to end of List
    	parents.add(s);
    	
    	// Arrays to populate coordinates with
    	double[] xValues = new double[parents.size()];
    	double[] yValues = new double[parents.size()];
    	
    	// Represents the point of the first parent (the tip of the triangle)
    	yValues[parents.size() - 1] = 0;
    	xValues[parents.size() - 1] = 0;		
   
    	double scalingFactor = 1;
    	
    	for (int i = parents.size() - 2; i >= 0; i--) {
    		xValues[i] = xValues[i + 1] + convertToNumber(parents.get(i).getMostRecentAllocation(), schedule.getGraph().getAll().size()) * scalingFactor;
    		scalingFactor /= (numberOfProcessors * schedule.getGraph().getAll().size() / 4);
    		yValues[i] = (1.0 / schedule.getGraph().getAll().size()) * (parents.size() - 1 - i);
    	}
    
    	return new double[][]{xValues, yValues};
    }

    // Convert allocation to number (as per pseudocode)
    private double convertToNumber(ProcessorAllocation allocation, int totalNumberOfTasks) {
    	return (double)(allocation.processor - 1)/(double)FXApplication.getMonitor().getNumberOfProcessors()*totalNumberOfTasks 
    			+ (double)allocation.task.getId()/(FXApplication.getMonitor().getNumberOfProcessors());
    }
    
    // Calculates the horizontal length by which to section up, at any given depth in the triangle
    private double horizontalLength(double depth) {
    	double ratio = this.totalTriangleWidth/this.totalTriangleHeight;	
    	return depth*ratio;
    }
    
	// Method to draw a set of dots, and interconnected lines, from arrays passed to it (representing a schedule)
	private void drawPixels(Canvas canvas, Color color, double[] x, double[] y, int width) {
		PixelWriter pixelWriter = this.gc.getPixelWriter();
		
		double max = Arrays.stream(x).max().orElse(1.0);
		
		for (int j = 0; j < x.length; j++) {
			x[j] /= max + 0.0001;
			y[j] = this.startPointY + y[j] * this.totalTriangleHeight;
			x[j] = this.startPointX - horizontalLength(y[j] - this.startPointY) / 2.0 + x[j] * horizontalLength(y[j] - this.startPointY);
		}
	
		// Loop through all points
		for (int i = 0; i < x.length; i++) {

			// Both draw a point, then a line to the next point, as you traverse coordinates
			pixelWriter.setColor((int)x[i], (int)y[i], color);
			if ((i + 1) == x.length) {
			} else {
				drawLine(x[i], y[i], x[i + 1], y[i + 1], color, width);
			}
		}
	}
    
    // Method to draw line with input color/width
    private void drawLine(double x1, double y1, double x2, double y2, Color color, int width) {
    	this.gc.setLineWidth(width);
    	this.gc.setStroke(color);
    	this.gc.strokeLine(x1, y1, x2, y2);
    }
}