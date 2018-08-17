package se306group8.scheduleoptimizer.visualisation.manager;

import javafx.scene.control.TextArea;
import se306group8.scheduleoptimizer.visualisation.ObservableRuntimeMonitor;

public class ConsoleManager extends Manager {

	private TextArea textArea;

	public ConsoleManager(TextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	protected void updateHook(ObservableRuntimeMonitor monitor) {
		String message;
		while((message = monitor.nextMessage()) != null) {
			String msg = message;
			textArea.appendText(msg+"\n");
		}
	}

}
