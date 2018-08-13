package se306group8.scheduleoptimizer.visualisation.manager;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import se306group8.scheduleoptimizer.algorithm.TreeSchedule;
import se306group8.scheduleoptimizer.taskgraph.Task;

public class ScheduleManager {

	private Pane pane;
	private VBox processors;

	public ScheduleManager(Pane pane, VBox processors) {
		this.pane = pane;
		this.processors = processors;
	}

	public void update(TreeSchedule schedule) {
		
		List<Rectangle> rectangles = new ArrayList<Rectangle>();
		List<Label> names = new ArrayList<Label>();
		List<Label> processorLabels = new ArrayList<Label>();
		
		int processor = 1;
		int y = 0;
		
		for (List<Task> list : schedule.computeTaskLists()) {
			
			Label label = new Label("P"+processor);
			label.setMinWidth(50);
			label.setMinHeight(30);
			label.setAlignment(Pos.CENTER);
			processorLabels.add(label);
			
			processor++;
			for (Task task : list) {
				int startTime = schedule.getAlloctionFor(task).startTime*550/schedule.getRuntime();
				
				Rectangle rectangle = new Rectangle(startTime, y, task.getCost()*550/schedule.getRuntime(), 30);
				if (schedule.isComplete()) {
					rectangle.setFill(Color.CORNFLOWERBLUE);
				} else {
					rectangle.setFill(Color.DARKORANGE);
				}
				rectangle.setStroke(Color.WHITE);
				rectangles.add(rectangle);
				
				Label name = new Label(task.getName());
				name.setTextFill(Color.WHITE);
				name.setAlignment(Pos.CENTER);
				name.setLayoutX(startTime);
				name.setLayoutY(y);
				name.setMinWidth(task.getCost()*550/schedule.getRuntime());
				name.setMinHeight(30);
				name.setMaxWidth(task.getCost()*550/schedule.getRuntime());
				name.setFont(new Font(8));
				names.add(name);
			}
			y = y + 40;
		}
		
		Platform.runLater(() -> {
			processors.getChildren().clear();
			processors.getChildren().addAll(processorLabels);
			pane.getChildren().clear();
			pane.getChildren().addAll(rectangles);
			pane.getChildren().addAll(names);
		});
	}

}
