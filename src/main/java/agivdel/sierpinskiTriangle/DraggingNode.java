package agivdel.sierpinskiTriangle;

/**
 * класс дает возможность перетаскивать узлы внутри окон и других контейнеров
 */

import javafx.scene.Cursor;
import javafx.scene.Node;

public class DraggingNode {

    public static void makeDraggable(Node node) {
        final Delta dragDelta = new Delta();

        node.setOnMouseEntered(me -> node.getScene().setCursor(Cursor.OPEN_HAND));
        node.setOnMouseExited(me -> node.getScene().setCursor(Cursor.DEFAULT));

        node.setOnMousePressed(me -> {
            if (me.isPrimaryButtonDown()) {
                node.getScene().setCursor(Cursor.CLOSED_HAND);
            }
            //зафиксировали координаты курсора при нажатии
            dragDelta.x = me.getX();
            dragDelta.y = me.getY();
        });
        node.setOnMouseReleased(me -> node.getScene().setCursor(Cursor.OPEN_HAND));

        node.setOnMouseDragged(me -> {
            // прибавили разницу в координатах курсора,
            // полученную при движении,
            // к координатам узла в родительском контейнере
            node.setLayoutX(node.getLayoutX() + me.getX() - dragDelta.x);
            node.setLayoutY(node.getLayoutY() + me.getY() - dragDelta.y);
        });
    }

    private static class Delta {
        public double x;
        public double y;
    }
}
