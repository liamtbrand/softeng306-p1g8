package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
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

public class ScheduleManager extends ManagerThread {

	private final VBox tasks;
	private final VBox processors;
	
	private final int graphWidth = 550;

	private final Paint green = Color.web("#55B655");
	private final Paint orange = Color.web("#FBA51C");
	
	public ScheduleManager(VBox tasks, VBox processors) {
		this.tasks = tasks;
		this.processors = processors;
	}

	@Override
	protected void updateHook() {
		
		TreeSchedule bestSchedule = FXApplication.getMonitor().getBestSchedule();
		
		if (bestSchedule == null) {
			return;
		}
		
		int runtime = bestSchedule.getRuntime();
		int currentProcessor = 1;
		
		List<AnchorPane> taskPanes = new ArrayList<AnchorPane>();
		List<Label> processorLabels = new ArrayList<Label>();
		
		for (List<Task> list : bestSchedule.computeTaskLists()) {
			
			Label label = new Label("P"+currentProcessor);
			label.setMinWidth(50);
			label.setMinHeight(30);
			label.setAlignment(Pos.CENTER);
			processorLabels.add(label);
			
			AnchorPane taskPane = new AnchorPane();
			List<Rectangle> rectangles = new ArrayList<Rectangle>();
			List<Label> taskNames = new ArrayList<Label>();
			
			currentProcessor++;
			for (Task task : list) {
				int startTime = bestSchedule.getAlloctionFor(task).startTime;
				int graphCost = task.getCost()*graphWidth/runtime;
				int graphStartTime = startTime*graphWidth/runtime;
				
				Rectangle rectangle = new Rectangle(graphStartTime, 0, graphCost, 30);
				if (bestSchedule.isComplete()) {
					rectangle.setFill(green);
				} else {
					rectangle.setFill(orange);
				}
				rectangle.setStroke(Color.WHITE);
				rectangles.add(rectangle);

				Label name = new Label(task.getName());
				name.setTextFill(Color.WHITE);
				name.setAlignment(Pos.CENTER);
				name.setLayoutX(graphStartTime);
				name.setLayoutY(0);
				name.setMinWidth(graphCost);
				name.setMinHeight(30);
				name.setFont(new Font(8));
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
		});
		
		
	}

}
