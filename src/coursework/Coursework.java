package coursework;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Coursework extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Coursework.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Coursework.class.getResource("coursework.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("Vehicle Sales Dashboard");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

