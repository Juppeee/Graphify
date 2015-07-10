/*
    Implements object Data, which represents a single array of
    data points and the information related to it.

    NOTE: The class doesn't provide a method for adding new data
    points to an existing Data object. Instead, all data is to be
    parsed again and a new object to be created when a the set of
    data is updated. This is for the sake of consistency in class
    hierarchy (no conflicting data add methods).
 */
import java.util.ArrayList;

public class Data {

    String dataName, xName, yName;
    ArrayList<DataPoint> points;
    boolean linReg;
    double errMargin;


    public Data(ArrayList<DataPoint> points, String name, String x, String y, boolean reg, double err) {

        this.dataName = name;
        this.xName = x;
        this.yName = y;
        this.points = new ArrayList<>();
        this.points = points;
        this.linReg = reg;
        this.errMargin = err;
    }

    public String getName() {

        return dataName;
    }

    public String getXname() {

        return xName;
    }

    public String getYname() {

        return yName;
    }

    public int getPointCount() {

        return points.size();
    }

    public double getMaxX() {

        double val = points.get(0).getX();
        for (int i = 0; i < points.size(); i++)
            if (points.get(i).getX() > val) val = points.get(i).getX();
        return val;
    }

    public double getMinX() {

        double val = points.get(0).getX();
        for (int i = 0; i < points.size(); i++)
            if (points.get(i).getX() < val) val = points.get(i).getX();
        return val;
    }

    public double getMaxY() {

        double val = points.get(0).getY();
        for (int i = 0; i < points.size(); i++)
            if (points.get(i).getY() > val) val = points.get(i).getY();
        return val;
    }

    public double getMinY() {

        double val = points.get(0).getY();
        for (int i = 0; i < points.size(); i++)
            if (points.get(i).getY() < val) val = points.get(i).getY();
        return val;
    }

    // TODO method for saving inputted data
    public void saveData() {}

    public DataPoint getDataPoint(int index) {
        return points.get(index);
    }
}
