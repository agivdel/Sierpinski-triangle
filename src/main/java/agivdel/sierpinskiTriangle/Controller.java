package agivdel.sierpinskiTriangle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;


import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends View implements Initializable {
    public static int timeDelay = Constants.SLOW;
    public static double[] vertices;
    private Task<Void> task;
    private int startLimit;

    @FXML
    private ToggleButton pauseButton;
    @FXML
    private ToggleButton slowButton;
    @FXML
    private ToggleButton mediumButton;
    @FXML
    private ToggleButton fastButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setVerticesRadius();
        verticesRelocate();
        makeDraggable();
        getNewVerticesCoordinates();
        rateToggleGroupInit();
        limitLabelInit();
    }

    private void getNewVerticesCoordinates() {
        vertices = new double[]{Constants.X_A, Constants.Y_A, Constants.X_B, Constants.Y_B, Constants.X_C, Constants.Y_C};

        pane_A.layoutXProperty().addListener((obj, oldValue, newValue) -> {
            vertices[0] = newValue.doubleValue() + Constants.SHIFT;
        });
        pane_A.layoutYProperty().addListener((obj, oldValue, newValue) -> {
            vertices[1] = newValue.doubleValue() + Constants.SHIFT;
        });

        pane_B.layoutXProperty().addListener((obj, oldValue, newValue) -> {
            vertices[2] = newValue.doubleValue() + Constants.SHIFT;
        });
        pane_B.layoutYProperty().addListener((obj, oldValue, newValue) -> {
            vertices[3] = newValue.doubleValue() + Constants.SHIFT;
        });

        pane_C.layoutXProperty().addListener((obj, oldValue, newValue) -> {
            vertices[4] = newValue.doubleValue() + Constants.SHIFT;
        });
        pane_C.layoutYProperty().addListener((obj, oldValue, newValue) -> {
            vertices[5] = newValue.doubleValue() + Constants.SHIFT;
        });
    }

    public double[] getVertices() {
        return vertices;
    }

    private void rateToggleGroupInit() {
        ToggleGroup rateToggleGroup = new ToggleGroup();
        slowButton.setToggleGroup(rateToggleGroup);
        mediumButton.setToggleGroup(rateToggleGroup);
        fastButton.setToggleGroup(rateToggleGroup);

        Tooltip rateTooltip = new Tooltip(Constants.RATE_SPEED);
        Tooltip.install(slowButton, rateTooltip);
        Tooltip.install(mediumButton, rateTooltip);
        Tooltip.install(fastButton, rateTooltip);

        slowButton.setUserData(Constants.SLOW);//500 мс пауза1, 500 мс пауза2
        mediumButton.setUserData(Constants.MED);//200 мс пауза1, 200 мс пауза2
        fastButton.setUserData(Constants.FAST);//1 мс пауза1, 1 мс пауза2

        slowButton.setSelected(true);

        rateToggleGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) ->
                timeDelay = (int) rateToggleGroup.getSelectedToggle().getUserData());
    }

    @FXML
    private void start(ActionEvent actionEvent) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }

        task = createTask();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        startLimit = limit;
        setLimitLabelVisible(false);
        progressLabel.textProperty().bind(task.messageProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        pauseButton.disableProperty().bind(task.runningProperty().not());
        setMinusAndPlusButtonsDisable(true);
        pane_A.disableProperty().bind(task.runningProperty());
        pane_B.disableProperty().bind(task.runningProperty());
        pane_C.disableProperty().bind(task.runningProperty());
    }

    @FXML
    private void cancel(ActionEvent actionEvent) throws InterruptedException {
        if (task != null) {
            task.cancel();
            task = null;
        } else {
            clearPane();
        }
    }

    private Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    draw();
                } catch (Exception ex) {
                    updateMessage(ex.getMessage());
                }
                return null;
            }

            private void draw() {
                Platform.runLater(() -> clearPane());

                SierpinskiTriangle st = new SierpinskiTriangle();
                st.correctVertices();

                ShapeBuilder shapeBuilder = new ShapeBuilder();
                Shape dashLine, circle, tracePoint;

                boolean isInterimPaneClear = false;

                //цикл отрисовки
                for (int i = limit; i >= 0; i--) {
                    if (isCancelled()) {
                        updateMessage("задача прервана");
                        Platform.runLater(() -> clearPane());
                        break;
                    }
                    while (pauseButton.isSelected()) {
                        Thread.onSpinWait();
                        updateMessage("пауза");
                    }
                    updateMessage("" + i);
                    updateProgress(i, limit);

                    double[] threeDotsArray = st.getThreeDots();

                    /**
                     * рисуем пунктир, делим его пополам красной точкой в черной обводке - это вспомогательные фигуры;
                     * через паузу удаляем обводку и пунктир,
                     * на месте середины линии остается только красная точка.
                     * черную обводку сопровождает текст "tracepoint"
                     */
                    if (fastButton.isSelected() & !isInterimPaneClear) {
                        Platform.runLater(() -> clearInterimShapes());//одноразовая очистка панели после перехода со slow/medium на fast
                        isInterimPaneClear = true;
                    }
                    if (!fastButton.isSelected()) {//рисование пунктира и подписи к точке - на скорости slow & medium
                        isInterimPaneClear = false;//если произойдет переключение со slow/medium на fast, это обеспечит одноразовую очистку панели
                        dashLine = shapeBuilder.getLine(threeDotsArray);//рисование пунктира
                        Platform.runLater(new DrawShape().param(pane1_interimShapes, dashLine, true));

                        //если использовать pause(), стирание происходит уже после первого же нажатия на cancel (через заметную паузу).
                        pause(timeDelay);
                        //если вместо pause() использовать "настоящее прерывание",
                        //после прерывания (первого нажатия на cancel) не происходит стирания.
                        //(т.к. стирание прописано только в самом первом if(isCanceled()), а не во всех трех)
                        //для полного стирания в этом случае нужно нажать на cancel еще раз.
//                    try {
//                        Thread.sleep(timeDelay);
//                    } catch (InterruptedException interrupted) {
//                        if (isCancelled()) {//
//                            updateMessage("задача прервана");
//                            break;
//                        }
//                    }

                        tracePoint = shapeBuilder.getText(threeDotsArray);//позиционирование подписи текущей точки
                        Platform.runLater(new DrawShape().param(pane1_interimShapes, tracePoint, false));
                    }
                    circle = shapeBuilder.getCircle(threeDotsArray);//рисование текущей точки (с обводкой)
                    Platform.runLater(new DrawShape().param(pane1_interimShapes, circle, false));

                    pause(timeDelay);
//                    try {
//                        Thread.sleep(timeDelay);
//                    } catch (InterruptedException interrupted) {
//                        if (isCancelled()) {//
//                            updateMessage("задача прервана");
//                        break;
//                        }
//                    }

                    //"стирание" обводки текущей точки (на самом деле просто окрашивание обводки точки в цвет точки)
                    circle.setStroke(Color.RED);
                    Platform.runLater(new DrawShape().param(pane2_points, circle, false));
                }
            }

            private void pause(int timeDelay) {
                try {
                    Thread.sleep(timeDelay);
                } catch (InterruptedException interrupted) {
//                    if (isCancelled()) {//
//                        updateMessage("задача прервана во время паузы");
////                        break;
//                        return;
//                    }
                }
            }

            @Override
            protected void succeeded() {
                updateMessage("задача завершена");
                limit = startLimit;
                setMinusAndPlusButtonsDisable(false);
                task = null;
            }

            @Override
            protected void cancelled() {
                pauseButton.setSelected(false);
                setMinusAndPlusButtonsDisable(false);
                limit = startLimit;
            }
        };
    }

    @FXML
    private void limitMinus() {
        setLimitLabelVisible(true);
        limit = new Limit().minus(limit);

        //если было 50000, а после изменения стало 45000, нужно активировать plusButton
        if (limit == 45000) {
            plusButton.setDisable(false);
        }
        //если было 2, а после изменения стало 1, нужно деактивировать minusButton
        if (limit == 1) {
            minusButton.setDisable(true);
        }
        limitLabel.setText(String.valueOf(limit));
    }

    @FXML
    private void limitPlus() {
        setLimitLabelVisible(true);
        limit = new Limit().plus(limit);

        //если было 1, а после изменения стало 2, нужно активировать minusButton//
        if (limit == 2) {
            minusButton.setDisable(false);
        }
        //если было 45000, а после изменения стало 50000, нужно деактивировать plusButton//
        if (limit == 50000) {
            plusButton.setDisable(true);
        }
        limitLabel.setText(String.valueOf(limit));
    }
}
