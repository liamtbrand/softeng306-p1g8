package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
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
import se306group8.scheduleoptimizer.visualisation.FXApplication;

public class ScheduleManager extends Manager {

	private final VBox tasks;
	private final VBox processors;
	private final NumberAxis runtimeAxis;
	
	private final int GRAPH_WIDTH = 550;
	private final int TASK_HEIGHT = 30;

	private final Paint GREEN = Color.web("#55B655");
	private final Paint ORANGE = Color.web("#FBA51C");
	
	public ScheduleManager(VBox tasks, VBox processors, NumberAxis runtimeAxis) {
		this.tasks = tasks;
		this.processors = processors;
		this.runtimeAxis = runtimeAxis;
	}

	@Override
	protected void updateHook() {
		
		int currentProcessor = 1;
		TreeSchedule bestSchedule = FXApplication.getMonitor().getBestSchedule();
		
		if (bestSchedule == null) {
			return;
		}
		
		int runtime = bestSchedule.getRuntime();
		
		List<AnchorPane> taskPanes = new ArrayList<AnchorPane>();
		List<Label> processorLabels = new ArrayList<Label>();
		
		for (List<Task> list : bestSchedule.computeTaskLists()) {
			
			Label label = new Label("P"+currentProcessor);
			label.setMinWidth(50);
			label.setMinHeight(30);
			label.setAlignment(Pos.CENTER);
			processorLabels.add(label);
			
			currentProcessor++;
			
			AnchorPane taskPane = new AnchorPane();
			List<Rectangle> rectangles = new ArrayList<Rectangle>();
			List<Label> taskNames = new ArrayList<Label>();
			
			for (Task task : list) {

				int startTime = bestSchedule.getAllocationFor(task).startTime;
				int graphStartTime = startTime*GRAPH_WIDTH/runtime;
				int graphCost = task.getCost()*GRAPH_WIDTH/runtime;
				
				Rectangle rectangle = new Rectangle(graphStartTime, 0, graphCost, TASK_HEIGHT);
				rectangle.setStroke(Color.WHITE);
				if (bestSchedule.isComplete()) {
					rectangle.setFill(GREEN);
				} else {
					rectangle.setFill(ORANGE);
				}

				Label name = new Label(task.getName());
				name.setTextFill(Color.WHITE);
				name.setAlignment(Pos.CENTER);
				name.setLayoutX(graphStartTime);
				name.setMinWidth(graphCost);
				name.setMinHeight(TASK_HEIGHT);
				name.setFont(new Font(8));
				
				rectangles.add(rectangle);
				taskNames.add(name);
			}
			
			Platform.runLater(() -> {
				taskPane.getChildren().addAll(rectangles);
				taskPane.getChildren().addAll(taskNames);
			});

			taskPanes.add(taskPane);
		}
		
		Platform.runLater(() -> {
			processors.getChildren().setAll(processorLabels);
			tasks.getChildren().setAll(taskPanes);
			runtimeAxis.setUpperBound(runtime);
		});
		
		
	}

}
