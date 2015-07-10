/*
    PointTableModel provides the model for the data table including all current features
    needed. All future extensions to the table should be implemented here.
*/
import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.Vector;

public class PointTableModel extends AbstractTableModel {

    public static final int X_INDEX = 0;
    public static final int Y_INDEX = 1;

    protected Vector dataVector;
    protected String[] columnNames;

    public PointTableModel(String[] columns) {
        this.columnNames = columns;
        dataVector = new Vector();
    }

    @Override
    public int getRowCount() {
        return dataVector.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        DataPoint point = (DataPoint)dataVector.get(row);
        switch (col) {
            case X_INDEX:
                return point.getX();
            case Y_INDEX:
                return point.getY();
            default:
                return new Object();
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case X_INDEX:
            case Y_INDEX:
                return Double.class;
            default:
                return Object.class;
        }
    }

    public void addRow(double x, double y) {
        dataVector.add(new DataPoint(x, y));
    }

    public void sort() {
        Collections.sort(dataVector);
        fireTableDataChanged();
    }

    public void emptyTable() {
        if (!dataVector.isEmpty()) {
            dataVector.removeAllElements();
            fireTableDataChanged();
        }
    }

    public boolean contains(DataPoint p) {
        for(int i = 0; i < getRowCount(); i++) {
            if (p.getX() == (double)getValueAt(i, X_INDEX) && p.getY() == (double)getValueAt(i, Y_INDEX))
                return true;
        }
        return false;
    }
}
