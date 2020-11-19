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

    @FXML private ToggleButton pauseButton;
    @FXML private ToggleButton slowButton;
    @FXML private ToggleButton mediumButton;
    @FXML private ToggleButton fastButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setVertexRadius();
        verticesRelocate();
        makeDraggable();
        setVertices();
        rateToggleGroupInit();
        labelsInit();
    }

    /**
     * привязываем координаты сдвигаемых вершин к переменным для корректной отрисовки фрактала
     */
    private void setVertices() {
        vertices = new double[] {Constants.X_A, Constants.Y_A, Constants.X_B, Constants.Y_B, Constants.X_C, Constants.Y_C};

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

    /**
     * геттер для массива координат трех вершин
     */
    public double[] getVertices() {
        return vertices;
    }

    /**
     * группируем кнопки выбора скорости, привязываем к каждой время задержки (по сути: скорость отрисовки)
     */
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

        slowButton.setSelected(true);//по умолчанию выбрана малая скорость

        rateToggleGroup.selectedToggleProperty().addListener((observableValue, toggle, t1) ->
                timeDelay = (int) rateToggleGroup.getSelectedToggle().getUserData());
    }

    /**
     * создание задачи и отдельного потока для нее,
     * связывание свойств задачи и свойств индикатора прогресса, метки отображения итераций, доступности части кнопок
     */
    @FXML
    private void start(ActionEvent actionEvent) {
        if (task != null && task.isRunning()) {
            task.cancel();
        }

        task = createTask();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        startLimit = limit;//запоминаем стартовое значение числа точек
        setLimitLabelTop(false);
        progressLabel.textProperty().bind(task.messageProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        pauseButton.disableProperty().bind(task.runningProperty().not());//кнопка паузы неактивна, когда задача не выполняется
        setDisableMinusPlusButtons(true);
    }

    /**
     * отмена запущенной задачи во время выполнения или паузы,
     * очистка панелей во время выполнения задачи, паузы или после завершения
     */
    @FXML
    private void cancel(ActionEvent actionEvent) throws InterruptedException {
        if (task != null) {
            task.cancel();
            task = null;
        } else {
            clearPane();
        }
    }

    /**
     * описание задачи:
     * цикл с отрисовкой на двух нижних панелях вспомогательных линий и точек, а также итоговых точек
     * (отрисовка с помощью Platform.runLater и new Runnable()
     */
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
                Platform.runLater(() -> {
                    clearPane();
                });

                SierpinskiTriangle st = new SierpinskiTriangle();
                st.correctVertices();

                ShapeBuilder sb = new ShapeBuilder();
                Shape dashLine, circle, tracePoint;

                //цикл отрисовки
                for (int i = limit; i >= 0; i--) {
                    if (isCancelled()) {
                        updateMessage("задача прервана");
                        Platform.runLater(() -> {
                            clearPane();
                        });
                        break;
                    }
                    while (pauseButton.isSelected()) {
                        Thread.onSpinWait();
                        updateMessage("пауза");
                    }
                    updateMessage("" + i);
                    updateProgress(i, limit);

                    double[] threeDotsArray = st.getThreeDots();//получение вычисленных координат следующих точек

                    /**
                     * рисуем пунктир, делим его пополам красной точкой в черной обводке - это вспомогательные фигуры;
                     * через паузу удаляем обводку и пунктир,
                     * на месте середины линии остается только красная точка.
                     * черную обводку сопровождает текст "tracepoint"
                     */
                    dashLine = sb.getLine(threeDotsArray);//рисование пунктира
                    Platform.runLater(new DrawShape().param(pane1_interimShapes, dashLine, true));

                    pause(timeDelay);

                    circle = sb.getCircle(threeDotsArray);//рисование текущей точки (с обводкой)
                    tracePoint = sb.getText(threeDotsArray);//позиционирование подписи к текущей точке
                    Platform.runLater(new DrawShape().param(pane1_interimShapes, circle, false));
                    Platform.runLater(new DrawShape().param(pane1_interimShapes, tracePoint, false));

                    pause(timeDelay);

                    //"стирание" обводки текущей точки (на самом деле просто окрашивание обводки точки в цвет точки)
                    circle.setStroke(Color.RED);
                    Platform.runLater(new DrawShape().param(pane2_points, circle, false));
                }
            }

            private void pause(int timeDelay) {
                try {
                    Thread.sleep(timeDelay);
                } catch (InterruptedException interrupted) {
                    if (isCancelled()) {//
                        updateMessage("задача прервана");
//                        break;
                        return;
                    }
                }
            }

            @Override
            protected void succeeded() {
                updateMessage("задача завершена");
                limit = startLimit;
                setDisableMinusPlusButtons(false);
                task = null;
            }

            @Override
            protected void cancelled() {
                pauseButton.setSelected(false);
                setDisableMinusPlusButtons(false);
                limit = startLimit;
            }
        };
    }

    /**
     * управление числом точек отрисовки
     */
    @FXML
    private void limitMinus() {
        setLimitLabelTop(true);
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

    /**
     * управление числом точек отрисовки
     */
    @FXML
    private void limitPlus() {
        setLimitLabelTop(true);
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
