package se306group8.scheduleoptimizer.visualisation.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import se306group8.scheduleoptimizer.visualisation.manager.CanvasFillManager;

public class SearchSpacePageController extends Controller {
	
	@FXML
	private Canvas canvas;

	@FXML
	private Label searchSpaceTitle;
	
	@FXML 
	private Label explanation;

	/**
	 * Overridden method that draw's the initial SearchSpace triangle, and starts the
	 * relevant manager, with a specified period to update.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	    GraphicsContext gc = canvas.getGraphicsContext2D();
	    draw(gc, canvas.getWidth(), canvas.getHeight());
	    
		startManager(new CanvasFillManager(canvas, searchSpaceTitle),0l,1l);
	}

	/**
	 * 
	 * @param gc - input graphics context
	 * @param width - input canvas width
	 * @param height - input canvas height
	 * Method to INITIALLY draw what's needed for the SearchSpace page including triangle bounds, etc.
	 */
    private void draw(GraphicsContext gc, double width, double height) {
 
      gc.setStroke(Color.BLACK);
      gc.setLineWidth(2);
      
      double[] x = {width/6.0 - 5, width/2.0, 5*width/6.0 + 5};
      double[] y = {7.0*height/8.0, height/8.0 - 5, 7.0*height/8.0};
      int n = 3;
      
      gc.strokePolygon(x, y, n);
    }

}
