package se306group8.scheduleoptimizer.visualisation.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
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
	public void setup() {
	    GraphicsContext gc = canvas.getGraphicsContext2D();
	    draw(gc, canvas.getWidth(), canvas.getHeight());

		startManager(new CanvasFillManager(canvas, searchSpaceTitle), UpdateFrequency.FAST);
	}

	/**
	 * 
	 * @param gc - input graphics context
	 * @param width - input canvas width
	 * @param height - input canvas height
	 * Method to INITIALLY draw what's needed for the SearchSpace page including triangle bounds, etc.
	 */
    private void draw(GraphicsContext gc, double width, double height) {
    	
    	// Draw Triangle
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(4);
		
		gc.save();
		      
		double[] x = {width/6.0, width/2.0, 5*width/6.0};
		double[] y = {7.0*height/8.0, height/8.0, 7.0*height/8.0};
		int n = 3;
		      
		gc.strokePolygon(x, y, n);
		      
		// Draw axes
		
		gc.setLineWidth(2);
		
		int offset = 20;
		
		int x1 = (int) (width / 6.0);
		int y1 = (int) (7.0 * height / 8.0 + offset);
		int x2 = (int) (5.0 * width / 6.0);
		
		// Horizontal Axis
		drawArrow(gc, x1, y1, x2, y1);
		drawArrow(gc, x2, y1, x1, y1);
		
		x1 = (int) (5.0 * width / 6.0 + offset);
		y1 = (int) (height / 8.0);
		int y2 = (int) (7.0 * height / 8.0);
		
		// Vertical Axis
		drawArrow(gc, x1, y1, x1, y2);
		
		gc.restore();    
    }
    
    // Method, as found on: https://stackoverflow.com/questions/35751576/javafx-draw-line-with-arrow-canvas
    private final int ARR_SIZE = 6;
    
    void drawArrow(GraphicsContext gc, int x1, int y1, int x2, int y2) {
        gc.setFill(Color.BLACK);

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx * dx + dy * dy);

        Transform transform = Transform.translate(x1, y1);
        transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
        gc.setTransform(new Affine(transform));

        gc.strokeLine(0, 0, len, 0);
        gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE, len}, new double[]{0, -ARR_SIZE, ARR_SIZE, 0},
                4);
    }

}
