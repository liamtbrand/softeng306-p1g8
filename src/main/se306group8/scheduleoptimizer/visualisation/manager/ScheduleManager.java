package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

/**
 * Manager class for the visualisation's task schedule graph. 
 * Implements majority of task schedule functionality.
 */

public class ScheduleManager extends Manager {

	private final VBox tasks;
	private final VBox processors;
	private final LineChart<?, ?> chart;
	private final Label bestRuntimeLabel;
	private final Label percentTasksLabel;

	// Dimensions for task schedule graph.
	private final double GRAPH_WIDTH = 521;
	private final int GRAPH_HEIGHT = 200;

	private boolean updateBest;
	
	private final Paint BLUE = Color.web("#7595c6");
	private final Paint GREEN = Color.web("#00a676");
	
	public ScheduleManager(
			VBox tasks, 
			VBox processors, 
			LineChart<?, ?> chart, 
			Label bestRuntimeLabel, 
			Label percentTasksLabel) {
		
		this.tasks = tasks;
		this.processors = processors;
		this.chart = chart;
		this.bestRuntimeLabel = bestRuntimeLabel;
		this.percentTasksLabel = percentTasksLabel;
	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		
		// Set default of updateBest to true for each iteration.
		updateBest = true;
		
		int currentProcessor = 1;
		TreeSchedule bestSchedule = monitor.getBestSchedule();
		
		// Do not update the visualisation if the schedule is empty of null.
		if (bestSchedule == null || bestSchedule.isEmpty()) {
			return;
		}
		
		// Get information about current schedule
		int numberOfTasks = bestSchedule.getGraph().getAll().size();
		int runtime = bestSchedule.getRuntime();
		int allocated = bestSchedule.getAllocated().size();
		int percentAllocated = allocated*100/numberOfTasks;
		
		// Get information about previous schedule seen
		String previousRuntime = bestRuntimeLabel.textProperty().get();
		int previousPercentAllocated = Integer.parseInt(percentTasksLabel.textProperty().get());
		
		// If less tasks allocated, don't show schedule
		if (percentAllocated < previousPercentAllocated) {
			updateBest = false;
		
			// If same amount of tasks scheduled, but worse runtime, don't show schedule
		} else if (percentAllocated == previousPercentAllocated && 
				(previousRuntime.equals("Searching ...") || runtime > Integer.parseInt(previousRuntime))) {
			updateBest = false;
		}
		
		int noP = bestSchedule.getNumberOfUsedProcessors();
		
		// Scale tasks to fit in graph with 10px spacing
		int taskHeight = (GRAPH_HEIGHT - 10*(noP-1))/noP;
		
		List<AnchorPane> taskPanes = new ArrayList<AnchorPane>();
		List<Label> processorLabels = new ArrayList<Label>();
		
		// Loop through all processors used in schedule
		for (List<Task> list : bestSchedule.computeTaskLists()) {
			
			// Create y-axis label for current processor and add to list
			Label label = new Label("P"+currentProcessor);
			label.setId("processor-label");
			label.setMinWidth(50);
			label.setMinHeight(taskHeight);
			label.setAlignment(Pos.CENTER);
			processorLabels.add(label);
			
			currentProcessor++;
			
			// Create list of tasks and names to put on graph
			AnchorPane taskPane = new AnchorPane();
			List<Rectangle> rectangles = new ArrayList<Rectangle>();
			List<Label> taskNames = new ArrayList<Label>();
			
			// Loop through each task in list and add to graph
			for (Task task : list) {

				int startTime = bestSchedule.getAllocationFor(task).startTime;
				
				// Scale the task's start time and cost to fit on graph
				double graphStartTime = startTime*GRAPH_WIDTH/runtime;
				double graphCost = task.getCost()*GRAPH_WIDTH/runtime;
				
				// Create rectangle with correct size and start time
				Rectangle rectangle = new Rectangle(graphStartTime, 0, graphCost, taskHeight);
				rectangle.setStroke(Color.WHITE);
				
				// Change colour of tasks based on whether schedule is optimal or the current best
				if (bestSchedule.isComplete() && monitor.hasFinished()) {
					rectangle.setFill(GREEN);
				} else if (updateBest){
					rectangle.setFill(BLUE);
				} else {
					rectangle.setFill(Color.DARKGREY);
				}

				// Create the task name label
				Label name = new Label(task.getName());
				name.setTextFill(Color.WHITE);
				name.setAlignment(Pos.CENTER);
				name.setLayoutX(graphStartTime);
				name.setMinWidth(graphCost);
				name.setMinHeight(taskHeight);
				name.setId("task-label");
				
				rectangles.add(rectangle);
				taskNames.add(name);
			}
			
			taskPane.getChildren().addAll(rectangles);
			taskPane.getChildren().addAll(taskNames);
			taskPanes.add(taskPane);
		}

		Platform.runLater(() -> {
				
			if (updateBest || bestRuntimeLabel.textProperty().get().equals("Searching ...")) {
				bestRuntimeLabel.setId("best-label-found");
				percentTasksLabel.setId("best-label-found");
				bestRuntimeLabel.textProperty().setValue(Integer.toString(bestSchedule.getRuntime()));
				percentTasksLabel.textProperty().setValue(Integer.toString((int)(100*(double)allocated/(double)numberOfTasks)));
			} else {
				bestRuntimeLabel.setId("best-label");
				percentTasksLabel.setId("best-label");
			}
				
			if (monitor.hasFinished()) {
				bestRuntimeLabel.setId("best-label-complete");
				percentTasksLabel.setId("best-label-complete");
			}
				
			processors.getChildren().setAll(processorLabels);
			tasks.getChildren().setAll(taskPanes);
			
			NumberAxis runtimeAxis = (NumberAxis) chart.getXAxis();
			runtimeAxis.setUpperBound(runtime);
			runtimeAxis.setTickUnit(runtime/10);
		});
	}

}
