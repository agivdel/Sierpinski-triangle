package agivdel.sierpinskiTriangle;

/**
 * класс дает возможность перетаскивать узлы внутри окон и других контейнеров
 */

import javafx.scene.Cursor;
import javafx.scene.Node;

public class DraggingNode {

    public static void makeDraggable(Node node) {
        final Delta dragDelta = new Delta();

        node.setOnMousePressed(me -> {
            if (me.isPrimaryButtonDown()) {
                node.getScene().setCursor(Cursor.DEFAULT);
            }
            //зафиксировали координаты курсора при нажатии
            dragDelta.x = me.getX();
            dragDelta.y = me.getY();
        });

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
