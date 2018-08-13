package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import se306group8.scheduleoptimizer.visualisation.FXApplication;

public class ConsoleManager extends ManagerThread {

	private TextArea textArea;

	public ConsoleManager(TextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	protected void updateHook() {
		String message;
		while((message = FXApplication.getMonitor().nextMessage()) != null) {
			String msg = message;
			Platform.runLater(() -> {
				textArea.appendText(msg);
			});
		}
	}

}
