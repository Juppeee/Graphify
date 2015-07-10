import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class Canvas extends JPanel {

    public static JFrame frame;

    public static final int width = 500;
    public static final int height = 500;
    public static final int pad = 30;
    public static final int textPad = 20;

    public static final DecimalFormat numFormat = new DecimalFormat("#.##");

    public static double xScale;
    public static double yScale;
    public static int count;

    public static JMenuBar menuBar;
    public static JMenu menuFile;
    public static JMenuItem fileSave;

    public static Data data;


    public Canvas () {
        setPreferredSize(new Dimension(width, height));
    }

    private void drawing(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        // Set antialiasing on
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Derive variables to help coordinate mapping
        int leftX = pad + textPad;
        int rightX = width - pad;
        int upY = pad;
        int downY = height - pad - textPad;
        int boxWidth = rightX - leftX;
        int boxHeight = downY - upY;
        count = data.getPointCount();

        // Graph style parameters
        int dotDiameter = 3;
        int yGrid = 10;
        int xGrid = 10;

        xScale = (double) boxWidth / (data.getMaxX()- data.getMinX());
        yScale = (double) boxHeight / (data.getMinY() - data.getMaxY());

        g2d.drawString("Error occurred in graph creation", width / 2 - 50, height / 2);

        // Draw background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(leftX, upY, boxWidth, boxHeight);

        // Draw axises
        g2d.setColor((Color.black));
        g2d.drawLine(leftX, upY, leftX, downY); //y-axis
        g2d.drawLine(leftX, downY, rightX, downY); //x-axis
        g2d.drawString(data.getName(), width / 2, pad / 2);
        g2d.drawString(data.getXname(), width / 2, height - pad / 2);


        // Draw axis zero markers in case of mixed positive and negative values
        if (data.getMinY() < 0 && data.getMaxY() > 0) {
            int zeroY = (int)(boxHeight * (data.getMaxY() / (data.getMaxY() - data.getMinY()))+ pad);
            g2d.drawLine(pad + textPad, zeroY, width - pad, zeroY);
        }
        if (data.getMinX() < 0 && data.getMaxX() > 0) {
            int zeroX = (int)(boxWidth * (-data.getMinX() / (data.getMaxX() - data.getMinX())) + pad + textPad);
            g2d.drawLine(zeroX, pad, zeroX, height - pad - textPad);
        }

        // Draw x-grid, hash marks
        double step = (data.getMaxX() - data.getMinX()) / xGrid;
        for (double gridMark = getStartPointX(step, leftX); scaleX(gridMark) <= rightX; gridMark += step) {
            g2d.drawLine(scaleX(gridMark), downY, scaleX(gridMark), downY - boxHeight / 50);
            g2d.drawString(numFormat.format(gridMark), scaleX(gridMark) - 5, downY + 15);
        }

        // draw y-grid, hash marks, NOTE: inverse marks in algorithm
        step = (data.getMaxY() - data.getMinY()) / yGrid; // NOTE: OK to reuse variable?
        for (double gridMark = getStartPointY(step, downY); scaleY(gridMark) >= upY; gridMark += step) {
            g2d.drawLine(leftX, scaleY(gridMark), leftX + boxWidth / 50, scaleY(gridMark));
            g2d.drawString(numFormat.format(gridMark), leftX - 25, scaleY(gridMark) + 5);
        }

        // draw data points, OPTIONAL: connect data points
        for (int i = 0; i < count; i++) {
            g2d.fillOval(scaleX(data.getDataPoint(i).getX()) - dotDiameter / 2,
                    scaleY(data.getDataPoint(i).getY()) - dotDiameter / 2, dotDiameter, dotDiameter);
            g2d.setColor(Color.red);
            if (i < count - 1){
                g2d.drawLine(scaleX(data.getDataPoint(i).getX()), scaleY(data.getDataPoint(i).getY()),
                        scaleX(data.getDataPoint(i + 1).getX()), scaleY(data.getDataPoint(i + 1).getY()));
            }
            g2d.setColor(Color.black);
        }
    }

    // Private methods used by draw

    private DataPoint getAnchorPoint() {
        // TODO improve anchor point lookup method ?
        DataPoint val = new DataPoint(0, 0);
        if (data.getMinX() > 0 || data.getMaxX() < 0) {
            val.setX(data.getMaxX() - data.getMinX());
        }
        if (data.getMinY() > 0 || data.getMaxY() < 0) {
            val.setY(data.getMinY()); // FIXME
        }
        Debug.print(val.getY());
        return val;
    }

    private double getStartPointX(double step, int leftX) {
        int n = 0;
        double start = getAnchorPoint().getX();
        for (double iterative = start - step; scaleX(iterative) >= leftX; iterative -= step)
            n++;
        return start - n * step;
    }

    private double getStartPointY(double step, int downY) {
        int n = 0;
        double start = getAnchorPoint().getY();
        for (double iterative = start - step; scaleY(iterative) <= downY; iterative -= step)
            n++;
        return start - n * step;
    }

    private int scaleX(double x) {
        return (int) (pad + textPad + xScale * (x - data.getMinX()));
    }

    private int scaleY(double y) {
        return (int) (pad + yScale * (y - data.getMaxY()));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawing(g);
    }

    private static void initMenuBar() {
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // Build menu item File
        menuFile = new JMenu("File");
        fileSave = new JMenuItem("Save");
        menuFile.add(fileSave);
        menuBar.add(menuFile);
    }

    public static void execute(Data input) {
        data = input;
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame(data.getName().equals("")  ? "Untitled graph" : data.getName());
            frame.setContentPane(new Canvas());
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            initMenuBar();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}