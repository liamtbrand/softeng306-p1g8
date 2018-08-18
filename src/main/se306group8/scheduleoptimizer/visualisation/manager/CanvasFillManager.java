package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import se306group8.scheduleoptimizer.algorithm.ProcessorAllocation;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Dependency;
import se306group8.scheduleoptimizer.taskgraph.Task;
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

		double[][] coordinates = scheduleToPixels(monitor.getBestSchedule());
		// Method call to draw out a given partial/full schedule (light blue if incomplete, green if complete)
		
		if (FXApplication.getMonitor().hasFinished()) {
			drawPixels(this.canvas, Color.rgb(0, 166, 118, 1.0), coordinates[0], coordinates[1], 5);
			//this.label.setTextFill(Color.rgb(68, 96, 140, 1.0));
		} else {
			drawPixels(this.canvas, Color.rgb(117, 149, 198, 1.0), coordinates[0], coordinates[1], 2);
		}		

	}
	
	// Method that translates an input partial schedule, into a series of x/y coordinates
	// representing task allocations at given points
    private double[][] scheduleToPixels(TreeSchedule schedule) {
    	
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
    		ProcessorAllocation allocation = parents.get(i).getMostRecentAllocation();
        	Collection<Task> allocSet = new HashSet<>(parents.get(i).getAllocatable());
        	
        	//Add the task and remove all children of it to get the allocatable tasks.
        	allocSet.add(allocation.task);
        	allocSet.removeAll(allocation.task.getChildren().stream().map(Dependency::getTarget).collect(Collectors.toSet()));
        	
        	List<Task> allocatable = new ArrayList<>(allocSet);
        	allocatable.sort((a, b) -> a.getId() - b.getId());
    		
        	int numberOfProcessors = parents.get(i).getNumberOfUsedProcessors();
        	
        	int optionsAtThisLevel = numberOfProcessors * allocatable.size();
        	xValues[i] = xValues[i + 1] * (1.0 - scalingFactor) + convertToNumber(allocation, allocatable, numberOfProcessors) * scalingFactor;
    		
    		scalingFactor /= optionsAtThisLevel;
    		yValues[i] = (1.0 / schedule.getGraph().getAll().size()) * (parents.size() - 1 - i);
    	}
    
    	return new double[][]{xValues, yValues};
    }

    // Convert allocation to number (as per pseudocode)
    private double convertToNumber(ProcessorAllocation allocation, List<Task> allocatable, int processors) {
    	double taskContribution = (double) allocatable.indexOf(allocation.task) / allocatable.size();
    	double processorContribution = (double) (allocation.processor - 1) / processors / allocatable.size();
    	double result =  taskContribution + processorContribution;
    	
    	//This corrects the value to 1.0 if there is the max processor and task allocated
    	return result / (1.0 - 1.0 / processors / allocatable.size());
    }
    
    // Calculates the horizontal length by which to section up, at any given depth in the triangle
    private double horizontalLength(double depth) {
    	double ratio = this.totalTriangleWidth/this.totalTriangleHeight;	
    	return depth*ratio;
    }
    
	// Method to draw a set of dots, and interconnected lines, from arrays passed to it (representing a schedule)
	private void drawPixels(Canvas canvas, Color color, double[] x, double[] y, int width) {

		for (int j = 0; j < x.length; j++) {
			y[j] = Math.max(0.0, Math.min(y[j], 1.0));
			x[j] = Math.max(0.0, Math.min(x[j], 1.0));
			
			y[j] = this.startPointY + y[j] * this.totalTriangleHeight;
			x[j] = this.startPointX - horizontalLength(y[j] - this.startPointY) / 2.0 + x[j] * horizontalLength(y[j] - this.startPointY);
		}
		
		// Loop through all points
		gc.setStroke(color);
		gc.setLineWidth(width);
		
		// Draw each individual line (fixed problem!)
		for (int j = 1; j < x.length; j++) {
			gc.strokeLine(x[j - 1], y[j - 1], x[j], y[j]);
		}
		
		gc.setFill(Color.rgb(255, 255, 255, 0.02));
		gc.fillPolygon(new double[] { startPointX, startPointX - totalTriangleWidth / 2, startPointX + totalTriangleWidth / 2 }, 
				new double[] { startPointY, startPointY + totalTriangleHeight, startPointY + totalTriangleHeight }, 
				3);
	}
}