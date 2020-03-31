package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;


public class Main extends Application {
	public static Stage window;
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("OpacityChanger");
		Parent root = FXMLLoader.load(getClass().getResource("app.fxml"));
		window.getIcons().add(new Image(getClass().getResource("icon.png").toString()));
		Scene scene = new Scene(root,400,400);
		primaryStage.setMinWidth(400);
		primaryStage.setMinHeight(400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
