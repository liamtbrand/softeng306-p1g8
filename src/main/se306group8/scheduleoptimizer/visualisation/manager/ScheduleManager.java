package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;
import se306group8.scheduleoptimizer.visualisation.FXApplication;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class ScheduleManager extends Manager {

	private final VBox tasks;
	private final VBox processors;
	private final LineChart<?, ?> chart;
	private final Label title;

	private final double GRAPH_WIDTH = 521;
	private final int GRAPH_HEIGHT = 180;

	private final Paint BLUE = Color.web("#7595c6");
	private final Paint GREEN = Color.web("#00a676");
	
	public ScheduleManager(VBox tasks, VBox processors, LineChart<?, ?> chart, Label title) {
		this.tasks = tasks;
		this.processors = processors;
		this.chart = chart;
		this.title = title;
	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		
		int currentProcessor = 1;
		TreeSchedule bestSchedule = monitor.getBestSchedule();
		
		if (bestSchedule == null || bestSchedule.isEmpty()) {
			return;
		}
		
		int runtime = bestSchedule.getRuntime();
		int taskHeight = GRAPH_HEIGHT/bestSchedule.getNumberOfUsedProcessors();
		
		List<AnchorPane> taskPanes = new ArrayList<AnchorPane>();
		List<Label> processorLabels = new ArrayList<Label>();
		
		for (List<Task> list : bestSchedule.computeTaskLists()) {
			
			Label label = new Label("P"+currentProcessor);
			label.setMinWidth(50);
			label.setMinHeight(taskHeight);
			label.setAlignment(Pos.TOP_CENTER);
			processorLabels.add(label);
			
			currentProcessor++;
			
			AnchorPane taskPane = new AnchorPane();
			List<Rectangle> rectangles = new ArrayList<Rectangle>();
			List<Label> taskNames = new ArrayList<Label>();
			
			for (Task task : list) {

				int startTime = bestSchedule.getAllocationFor(task).startTime;
				double graphStartTime = startTime*GRAPH_WIDTH/runtime;
				double graphCost = task.getCost()*GRAPH_WIDTH/runtime;
				
				Rectangle rectangle = new Rectangle(graphStartTime, 0, graphCost, taskHeight);
				rectangle.setStroke(Color.WHITE);
				
				if (bestSchedule.isComplete()) {
					if (monitor.hasFinished()) {
						rectangle.setFill(GREEN);
					} else {
						rectangle.setFill(BLUE);
					}
				} else {
					rectangle.setFill(Color.DARKGREY);
				}

				Label name = new Label(task.getName());
				name.setTextFill(Color.WHITE);
				name.setAlignment(Pos.CENTER);
				name.setLayoutX(graphStartTime);
				name.setMinWidth(graphCost);
				name.setMinHeight(taskHeight);
				name.setFont(new Font(8));
				
				rectangles.add(rectangle);
				taskNames.add(name);
			}
			
			taskPane.getChildren().addAll(rectangles);
			taskPane.getChildren().addAll(taskNames);
			taskPanes.add(taskPane);
		}

		Platform.runLater(() -> {
			processors.getChildren().setAll(processorLabels);
			processors.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
			tasks.getChildren().setAll(taskPanes);
			NumberAxis runtimeAxis = (NumberAxis) chart.getXAxis();
			runtimeAxis.setUpperBound(runtime);
			runtimeAxis.setTickUnit(runtime/10);
			
			if (FXApplication.getMonitor().hasFinished()) {
				this.title.setTextFill(Color.rgb(68, 96, 140, 1.0));
			}
		});
	}

}
