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

    @FXML public Pane pane1_interimShapes;//панель для промежуточных (вспомогательных) линий
    @FXML public Pane pane2_points;//панель для итоговых точек
    @FXML public Pane pane3_vertices_control;//самая верхняя панель для вершин треугольника и подвижной панели управления

    @FXML private VBox controlVBox;//панель управления
    @FXML public Pane pane_A;//панель с двумя окружностями
    @FXML public Pane pane_B;
    @FXML public Pane pane_C;
    @FXML private Circle dot_A_inner;//окружность меньшего диаметра, совпадает с вершиной треугольника
    @FXML private Circle dot_A_outer;//окружность большего диаметра
    @FXML private Circle dot_B_inner;
    @FXML private Circle dot_B_outer;
    @FXML private Circle dot_C_inner;
    @FXML private Circle dot_C_outer;

    @FXML public Label limitLabel;
    @FXML public Label progressLabel;
    @FXML public ProgressBar progressBar;

    @FXML public Button minusButton;
    @FXML public Button plusButton;


    /**
     * метод для доступа к объекту окна программы
     */
    void setStage(Stage stage) {
        View.stage = stage;
        scene = stage.getScene();
    }

    /**
     * задаем радиусы окружностей вокруг вершин треугольника
     */
    void setVertexRadius() {
        dot_A_inner.setRadius(Constants.RADIUS_INNER);
        dot_B_inner.setRadius(Constants.RADIUS_INNER);
        dot_C_inner.setRadius(Constants.RADIUS_INNER);
        dot_A_outer.setRadius(Constants.RADIUS_OUTER);
        dot_B_outer.setRadius(Constants.RADIUS_OUTER);
        dot_C_outer.setRadius(Constants.RADIUS_OUTER);
    }

    /**
     * расставляем вершины трегуольника по начальным точкам
     * (с поправкой на радиус видимой окружности, чтобы ее центр совпал со значением констант X_A, X_B и т.д.)
     * после можно будет задать иное их расположение
     */
    void verticesRelocate() {
        pane_A.relocate(Constants.X_A - Constants.SHIFT, Constants.Y_A - Constants.SHIFT);
        pane_B.relocate(Constants.X_B - Constants.SHIFT, Constants.Y_B - Constants.SHIFT);
        pane_C.relocate(Constants.X_C - Constants.SHIFT, Constants.Y_C - Constants.SHIFT);
    }

    /**
     * делаем панель контроля и вершины треугольника подвижными
     */
    void makeDraggable() {
        DraggingNode.makeDraggable(pane_A);
        DraggingNode.makeDraggable(pane_B);
        DraggingNode.makeDraggable(pane_C);
        DraggingNode.makeDraggable(controlVBox);
    }


    /**
     * инициируем переменную limit (число точек, которые нужно отрисовать), отображаем ее на метке,
     * отображаем индикатор прогресса
     */
    void labelsInit() {
        limit = Constants.LIMIT;
        limitLabel.setText(String.valueOf(limit));
    }

    /**
     * очистка панелей со вспомогательными формами и основными точками (pane1, pane2)
     * НЕ ВСЕГДА СРАБАТЫВАЕТ - ИСПРАВИТЬ!
     */
    void clearPane() {
        pane1_interimShapes.getChildren().clear();
        pane2_points.getChildren().clear();
    }


    /**
     * меняем местами видимость двух текстовых меток, расположенных стопкой
     */
    void setLimitLabelTop(boolean top) {
        limitLabel.setVisible(top);
        progressLabel.setVisible(!top);
    }

    /**
     * кнопки "+" и "-" нужно несколько раз делать недоступными и наоборот
     */
    void setDisableMinusPlusButtons(boolean disable) {
        minusButton.setDisable(disable);
        plusButton.setDisable(disable);
    }


    /**
     * отрисовка всех форм на разных панелях
     */
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
