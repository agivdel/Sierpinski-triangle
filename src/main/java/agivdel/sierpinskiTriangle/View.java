package agivdel.sierpinskiTriangle;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class View {
    static Stage stage;
    static Scene scene;

    int limit;

    @FXML public Pane pane1_interimShapes;
    @FXML public Pane pane2_points;
    @FXML public Pane pane3_vertices_control;

    @FXML private VBox controlVBox;
    @FXML public Pane pane_A;
    @FXML public Pane pane_B;
    @FXML public Pane pane_C;
    @FXML private Circle dot_A_inner;
    @FXML private Circle dot_A_outer;
    @FXML private Circle dot_B_inner;
    @FXML private Circle dot_B_outer;
    @FXML private Circle dot_C_inner;
    @FXML private Circle dot_C_outer;

    @FXML public Label limitLabel;
    @FXML public Label progressLabel;
    @FXML public ProgressBar progressBar;

    @FXML public Button minusButton;
    @FXML public Button plusButton;



    void setStage(Stage stage) {
        View.stage = stage;
        scene = stage.getScene();
    }

    void setVerticesRadius() {
        dot_A_inner.setRadius(Constants.RADIUS_INNER);
        dot_B_inner.setRadius(Constants.RADIUS_INNER);
        dot_C_inner.setRadius(Constants.RADIUS_INNER);
        dot_A_outer.setRadius(Constants.RADIUS_OUTER);
        dot_B_outer.setRadius(Constants.RADIUS_OUTER);
        dot_C_outer.setRadius(Constants.RADIUS_OUTER);
    }

    void verticesRelocate() {
        pane_A.relocate(Constants.X_A - Constants.SHIFT, Constants.Y_A - Constants.SHIFT);
        pane_B.relocate(Constants.X_B - Constants.SHIFT, Constants.Y_B - Constants.SHIFT);
        pane_C.relocate(Constants.X_C - Constants.SHIFT, Constants.Y_C - Constants.SHIFT);
    }

    void makeDraggable() {
        DraggingNode.makeDraggable(pane_A);
        DraggingNode.makeDraggable(pane_B);
        DraggingNode.makeDraggable(pane_C);
        DraggingNode.makeDraggable(controlVBox);
    }

    void limitLabelInit() {
        limit = Constants.LIMIT;
        limitLabel.setText(String.valueOf(limit));
    }

    void clearPane() {
        clearInterimShapes();
        pane2_points.getChildren().clear();
    }

    void clearInterimShapes() {
        pane1_interimShapes.getChildren().clear();
    }

    void setLimitLabelVisible(boolean top) {
        limitLabel.setVisible(top);
        progressLabel.setVisible(!top);
    }

    void setMinusAndPlusButtonsDisable(boolean disable) {
        minusButton.setDisable(disable);
        plusButton.setDisable(disable);
    }

    static class DrawShape implements Runnable {
        Pane pane;
        Shape shape;
        boolean clearPane = false;//по умолчанию чистить панели не нужно

        @Override
        public void run() {
            if (clearPane) {
                pane.getChildren().clear();//если очистку убрать, поток начинает сильно тормозить (с canvas такого не было!)
            }
            pane.getChildren().add(shape);
        }

        public Runnable param(Pane pane, Shape shape, boolean clearPane) {
            this.shape = shape;
            this.clearPane = clearPane;
            this.pane = pane;
            return this;
        }
    }
}
