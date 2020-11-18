package agivdel.sierpinskiTriangle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.getIcons().add(new Image("/images/sierpinskiTriangle.png"));
        stage.setTitle("Sierpinski Triangle");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, Constants.PREF_WIDTH, Constants.PREF_HEIGHT);
        stage.setScene(scene);
        stage.setOpacity(0.95);
        scene.getStylesheets().addAll(getClass().getResource("/scene.css").toExternalForm());
        stage.show();

        View view = loader.getController();
        view.setStage(stage);
    }
}