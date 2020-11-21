package agivdel.sierpinskiTriangle;

import java.util.Random;

public class SierpinskiTriangle {
    private final Random random = new Random();
    private double xA, yA, xB, yB, xC, yC;
    private static double targetX, targetY;
    private static double previousX, previousY;
    private static double currentX, currentY;
    private static double midpointX, midpointY;

    /**
     * перед началом отрисовки корреткируем координаты вершин и первой текущей точки
     */
    void correctVertices() {
        double[] vertices = new Controller().getVertices();

        xA = vertices[0];
        yA = vertices[1];
        xB = vertices[2];
        yB = vertices[3];
        xC = vertices[4];
        yC = vertices[5];

        currentX = xA;
        currentY = yA;
    }

    public double[] getThreeDots() {
        generateSierpinskiTriangle();
        return new double[]{previousX, previousY, targetX, targetY, midpointX, midpointY};
    }

    private void generateSierpinskiTriangle() {

        int randomPoint = random.nextInt(3);
        if (randomPoint <= 0.99) {
            targetX = xA;
            targetY = yA;
        } else if (randomPoint <= 1.99) {
            targetX = xB;
            targetY = yB;
        } else {
            targetX = xC;
            targetY = yC;
        }
        midpointX = (currentX + targetX) / 2;
        midpointY = (currentY + targetY) / 2;

        previousX = currentX;
        previousY = currentY;

        currentX = midpointX;
        currentY = midpointY;
    }
}
