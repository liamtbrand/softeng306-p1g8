package se306group8.scheduleoptimizer.visualisation.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import se306group8.scheduleoptimizer.visualisation.manager.CanvasFillManager;

public class SearchSpacePageController extends Controller {
	
	@FXML
	private Canvas canvas;

    // This method is called by the 
    // FXMLLoader when initialization 
    // is complete
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	    GraphicsContext gc = canvas.getGraphicsContext2D();
	    draw(gc, canvas.getWidth(), canvas.getHeight());
	    
		startManager(new CanvasFillManager(canvas));	
	}

	// Method to INITIALLY draw what's needed for the SearchSpace page
    private void draw(GraphicsContext gc, double width, double height) {
 
      gc.setFill(Color.ORANGE);
      gc.setStroke(Color.ORANGE);
      
      double[] x = {width/6.0, width/2.0, 5*width/6.0};
      double[] y = {3.0*height/4.0, height/4.0, 3.0*height/4.0};
      int n;
      // A simple triangle.
      n = 3;

      gc.strokePolygon(x, y, n);
      
      
     // gc.fillPolygon(x, y, n);     // Fills the triangle above.
      
    }

}
