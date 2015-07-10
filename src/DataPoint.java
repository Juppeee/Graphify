/*
    Simple class for a 2d-point presented as a pair of double float values. Offers sorting interface.
*/
public class DataPoint implements Comparable<DataPoint>{

    private double x;
    private double y;

    public DataPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(DataPoint other) {
        return (int)(this.getX() - other.getX());
    }
}