package agivdel.sierpinskiTriangle;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class ShapeBuilder {

    public Line getLine(double[] threeDotsArray) {
        Line line = new Line(threeDotsArray[0], threeDotsArray[1], threeDotsArray[2], threeDotsArray[3]);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(0.5);//толщина по умолчанию 1.0/
        line.getStrokeDashArray().addAll(0.5, 5.0);//пунктир: линия 0.5px, пропуск 5.0px
        return line;
    }

    public Circle getCircle(double[] threeDotsArray) {
        Circle circle = new Circle(threeDotsArray[4], threeDotsArray[5], Constants.RADIUS_INNER, Color.RED);
        circle.setStroke(Color.BLACK);
        return circle;
    }

    public Text getText(double[] threeDotsArray) {
        Text text = new Text(threeDotsArray[4] + 3 * Constants.RADIUS_INNER,
                threeDotsArray[5] - 2 * Constants.RADIUS_INNER, Constants.TEXT);
        text.setFill(Color.BLACK);
        return text;
    }
}
