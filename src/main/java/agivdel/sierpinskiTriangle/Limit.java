package agivdel.sierpinskiTriangle;

/**
 * с каждым нажатием кнопок minusButton / plusButton значение переменной limit
 * меняется в зависимости от предыдущего (с разницей в 1, 10, 100, 1000 или 5000)
 */
public class Limit {

    public int minus(int limit) {
        if (limit >= 15000 & limit <= 50000) {
            limit -= 5000;
        } else if (limit >= 2000 & limit <= 10000) {
            limit -= 1000;
        } else if (limit >= 200 & limit <= 1000) {
            limit -= 100;
        } else if (limit >= 20 & limit <= 100) {
            limit -= 10;
        } else if (limit >= 2 & limit <= 10) {
            limit -= 1;
        }
        return limit;
    }

    public int plus(int limit) {
        if (limit >= 10000 & limit <= 45000) {
            limit += 5000;
        } else if (limit >= 1000 & limit < 10000) {
            limit += 1000;
        } else if (limit >= 100 & limit < 1000) {
            limit += 100;
        } else if (limit >= 10 & limit < 100) {
            limit += 10;
        } else if (limit < 10) {
            limit += 1;
        }
        return limit;
    }
}
