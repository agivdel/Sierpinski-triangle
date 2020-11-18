package agivdel.sierpinskiTriangle;

public class Constants {
    public static final int PREF_WIDTH = 1000;
    public static final int PREF_HEIGHT = 800;

    public static final double X_A = PREF_WIDTH * 0.05;
    public static final double Y_A = PREF_HEIGHT / 2.0;
    public static final double X_B = PREF_WIDTH / 2.0;
    public static final double Y_B = PREF_HEIGHT * 0.06;
    public static final double X_C = PREF_WIDTH * 0.8;
    public static final double Y_C = PREF_HEIGHT * 0.8;
    public static final double SHIFT = Constants.RADIUS_OUTER + 1;//для учета толщины обводки фигур (по умолчанию 1px)

    public static final String RATE_SPEED = "draw speed choice";
    public static final int SLOW = 500;
    public static final int MED = 200;
    public static final int FAST = 1;

    public static final int LIMIT = 1000;//max=50000, min=1

    public static final double RADIUS_INNER = 2;
    public static final double RADIUS_OUTER = 15;

    public static final String TEXT = "tracepoint";
}
