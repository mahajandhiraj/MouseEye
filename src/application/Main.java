package application;
	
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
			Parent root =loader.load();
			
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image("file:icon.png"));
			primaryStage.setTitle("MouseEye");
			primaryStage.setResizable(false);
			primaryStage.show();
			
			// set the proper behavior on closing the application
			MainController controller = loader.getController();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we)
			{
				controller.stopCamera();
			}
				}));
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		launch(args);
	}
}
