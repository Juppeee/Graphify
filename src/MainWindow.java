import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainWindow implements Runnable {

    private static JFrame mainFrame;
    private static ImageIcon icon;

    private static JMenuBar menuBar;
    private static JMenu menuFile;
    private static JMenu menuEdit;
    private static JMenuItem fileNew;
    private static JMenuItem fileImport;
    private static JMenuItem fileExit;

    private static JPanel textPanel;
    private static JPanel dataPanel;
    private static JPanel optionPanel;

    private static JTextArea nameArea;
    private static JTextArea xNameArea;
    private static JTextArea yNameArea;
    private static JTextArea xArea;
    private static JTextArea yArea;

    private static JCheckBox connectBox;
    private static JCheckBox gridBox;
    private static JCheckBox sizeBox;
    private static JCheckBox errBox;

    private static JButton plotButton;
    private static JButton addDataButton;

    private static JTable table;
    private static PointTableModel tableModel;

    private static StatusBar statusBar;

    private static JFileChooser chooser;

    private static final String[] textLabels = {"Data header", "X-axis header", "Y-axis header"};
    private static final String[] axLabels = {"x", "y"};
    private static final String[] optLabels = {"Connect points", "Display grid", "Set custom graph size", "Display error margin"};

    // NOTE: A single instance of GridBagConstraints is used for all of the components in the main window
    // Columns and rows are set by a private method
    private static GridBagConstraints grid;

    private static Data data;

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;


    public static void main(String[] args) {
        // For OS X compatibility
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        // Runs the main window
        SwingUtilities.invokeLater(new MainWindow());
    }

    private static void initMenuBar() {
        menuBar = new JMenuBar();
        mainFrame.setJMenuBar(menuBar);

        // Build menu item File
        menuFile = new JMenu("File");
        fileNew = new JMenuItem("New");

        fileNew.addActionListener(event -> {
            // NOTE: Break statements not used to allow falling through cases
            switch(JOptionPane.showConfirmDialog(mainFrame, "There is unsaved data in the application. Save before exiting?", "Confirm data wipe", JOptionPane.YES_NO_CANCEL_OPTION)){
                case JOptionPane.YES_OPTION:
                    //savesavesave
                case JOptionPane.NO_OPTION:
                    tableModel.emptyTable();
                case JOptionPane.CANCEL_OPTION:
                default: break;
            }
        });

        fileImport = new JMenuItem("Import");
        fileImport.addActionListener(event -> importData());
        fileExit = new JMenuItem("Exit");
        fileExit.addActionListener(event -> System.exit(0));
        menuFile.add(fileNew);
        menuFile.add(fileImport);
        menuFile.add(fileExit);
        menuBar.add(menuFile);

        // Build menu item Edit
        menuEdit = new JMenu("Edit");
        menuBar.add(menuEdit);
    }

    private static void initFormFields() {

        grid.insets = new Insets(2, 2, 2, 2);

        // Set graph name panel

        textPanel = new JPanel();
        textPanel.setLayout(new GridBagLayout());
        textPanel.setBorder(BorderFactory.createTitledBorder("Graph data"));
        setGrid(0, 0);
        grid.ipady = 50;
        mainFrame.add(textPanel, grid);

        GridBagConstraints subGrid = new GridBagConstraints();
        subGrid.weightx = 1;
        subGrid.weighty = 1;
        subGrid.ipadx = 5;
        subGrid.ipady = 5;

        nameArea = new JTextArea("");
        xNameArea = new JTextArea("");
        yNameArea = new JTextArea("");

        nameArea.setColumns(1);
        nameArea.setPreferredSize(new Dimension(200, 15));
        xNameArea.setColumns(1);
        xNameArea.setPreferredSize(new Dimension(200, 15));
        yNameArea.setColumns(1);
        yNameArea.setPreferredSize(new Dimension(200, 15));

        for (int i = 0; i < textLabels.length; i++) {
            subGrid.gridy = i;
            textPanel.add(new JLabel(textLabels[i]), subGrid);
        }
        subGrid.gridx = 1;
        subGrid.gridy = 0;
        textPanel.add(nameArea, subGrid);
        subGrid.gridy = 1;
        textPanel.add(xNameArea, subGrid);
        subGrid.gridy = 2;
        textPanel.add(yNameArea, subGrid);

        // Set data add panel

        dataPanel = new JPanel();
        dataPanel.setLayout(new GridBagLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("Input data"));
        setGrid(0, 1);
        grid.ipady = 25;
        mainFrame.add(dataPanel, grid);

        xArea = new JTextArea("");
        yArea = new JTextArea("");

        xArea.setColumns(1);
        xArea.setPreferredSize(new Dimension(50, 15));
        yArea.setColumns(1);
        yArea.setPreferredSize(new Dimension(50, 15));

        subGrid.gridx = 0;
        for (int i = 0; i < axLabels.length; i++) {
            subGrid.gridy = i;
            dataPanel.add(new JLabel(axLabels[i]), subGrid);
        }

        subGrid.gridx = 1;
        subGrid.gridy = 0;
        dataPanel.add(xArea, subGrid);
        subGrid.gridy = 1;
        dataPanel.add(yArea, subGrid);

        // Set option panel

        optionPanel = new JPanel();
        optionPanel.setLayout(new GridBagLayout());
        optionPanel.setBorder(BorderFactory.createTitledBorder("Graph settings"));
        setGrid(0, 2);
        mainFrame.add(optionPanel, grid);

        connectBox = new JCheckBox();
        gridBox = new JCheckBox();
        sizeBox = new JCheckBox();
        errBox = new JCheckBox();

        subGrid.gridx = 0;
        for (int i = 0; i < optLabels.length; i++) {
            subGrid.gridy = i;
            optionPanel.add(new JLabel(optLabels[i]), subGrid);
        }

        subGrid.gridx = 1;
        subGrid.gridy = 0;
        optionPanel.add(connectBox, subGrid);
        subGrid.gridy = 1;
        optionPanel.add(gridBox, subGrid);
        subGrid.gridy = 2;
        optionPanel.add(sizeBox, subGrid);
        subGrid.gridy = 3;
        optionPanel.add(errBox, subGrid);
    }

    private static void initButtons() {
        plotButton = new JButton("Plot");
        setGrid(0, 4);
        grid.insets = new Insets(10, 10, 10 ,10);
        grid.fill = GridBagConstraints.BOTH;
        mainFrame.add(plotButton, grid);
        plotButton.addActionListener(event -> {
            parseData();
            if (data.getPointCount() > 2 && data.getMaxX() > data.getMinX() && data.getMaxY() > data.getMinY()) {
                Canvas.execute(data);
                statusBar.setDefault();
            } else {
                statusBar.setStatus("Not enough data");
            }
        });

        addDataButton = new JButton("Add data point");
        setGrid(3, 0);
        grid.gridheight = 2;
        dataPanel.add(addDataButton, grid);
        addDataButton.addActionListener(event -> {
            if (isDouble(xArea.getText()) && isDouble(yArea.getText())) {
                double x = Double.parseDouble(xArea.getText());
                double y = Double.parseDouble(yArea.getText());
                if (!tableModel.contains(new DataPoint(x, y))) {
                    tableModel.addRow(x, y);
                    tableModel.fireTableDataChanged();
                    statusBar.setDefault();
                } else
                    statusBar.setStatus("Identical point already in table");
            } else {
                statusBar.setStatus("Data add failed");
            }
        });
        grid.insets = new Insets(0, 0, 0, 0);
    }

    private static void initTable() {
        String[] columnNames = {"x", "y"};
        tableModel = new PointTableModel(columnNames);
        table = new JTable();
        table.setModel(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(10);
        table.setFillsViewportHeight(true);
        setGrid(1, 0);
        grid.gridheight = 5;
        grid.anchor = GridBagConstraints.LINE_END;
        mainFrame.add(scrollPane, grid);
    }

    private static void initStatusBar() {
        statusBar = new StatusBar(WINDOW_WIDTH);
        setGrid(0, 5);
        grid.gridwidth = 2;
        grid.anchor = GridBagConstraints.PAGE_END;
        grid.weighty = 0;
        mainFrame.add(statusBar, grid);
    }

    private static void parseData() {
        ArrayList<DataPoint> pointList = new ArrayList<>();
        tableModel.sort();
        for (int i = 0; i < table.getRowCount(); i++) {
            pointList.add(new DataPoint((double) table.getValueAt(i, 0), (double) table.getValueAt(i, 1)));
        }
        data = new Data(pointList, nameArea.getText(), xNameArea.getText(), yNameArea.getText(), false, 0);
    }

    private static boolean isDouble(String input) {
        // The content of text boxes is checked by trying to parse the string into
        // double and catching the possible exception. This may not be the fastest
        // or most elegant method but it does the trick for now.
        //
        // POSSIBLE IMPROVEMENT: Would use of regular expression matching improve the
        // method efficiency?
        try{
            //noinspection ResultOfMethodCallIgnored
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void setGrid(int gridx, int gridy) {
        grid.gridx = gridx;
        grid.gridy = gridy;

        grid.gridwidth = 1;
        grid.gridheight = 1;

        grid.ipadx = 0;
        grid.ipady = 0;
    }


    private static void importData() {
        // FIXME the looping part of the code is still a bit messy with all the file error testing

        int addCounter = 0;
        chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV (Comma delimited)", "csv");
        chooser.setFileFilter(filter);
        int val = chooser.showOpenDialog(mainFrame);
        if (val == JFileChooser.APPROVE_OPTION) {
            statusBar.setStatus("File '" + chooser.getSelectedFile().getName() + "' read.");
            try {
                BufferedReader in = new BufferedReader(new FileReader(chooser.getSelectedFile()));
                tableModel.emptyTable();
                String line;
                String[] splitLine;
                double[] xyVal = new double[2];
                tableModel.emptyTable();
                while ((line = in.readLine()) != null) {
                    if (line.split(",").length != 2)
                        continue;
                    splitLine = line.split(",");
                    switch(splitLine[0]) {
                        case "H":
                            nameArea.setText(splitLine[1]);
                            break;
                        case "X":
                            xNameArea.setText(splitLine[1]);
                            break;
                        case "Y":
                            yNameArea.setText(splitLine[1]);
                            break;
                    }
                    if (!isDouble(splitLine[0]) || !isDouble(splitLine[1]))
                        continue;
                    for (int i = 0; i < 2; i++)
                        xyVal[i] = Double.parseDouble(splitLine[i]);
                    if(!tableModel.contains(new DataPoint(xyVal[0], xyVal[1]))) {
                        tableModel.addRow(xyVal[0], xyVal[1]);
                        tableModel.fireTableDataChanged();
                        addCounter++;
                    }
                }
                statusBar.setStatus(statusBar.getStatus() + " " + addCounter + " data points added.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // MainWindow is the initial class of the application so the run method is the first thing run
    // Other windows are initialized with anonymous runnable method (using a lambda expression)
    public void run() {
        mainFrame = new JFrame("Graphify");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setLayout(new GridBagLayout());
        // icon image is still a  placeholder (how did it turn that awful?!)
        icon = new ImageIcon("resource/icon_v2.png");
        mainFrame.setIconImage(icon.getImage());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.out.println("Application couldn't locate System Look And Feel class, using default look and feel options.");
            //e.printStackTrace();
        }
        // NOTE: The single layout variable initialized here is passed to each of the component initializers
        // A separate GridBagConstraints is provided for text field placing in sub panels
        grid = new GridBagConstraints();
        grid.fill = GridBagConstraints.HORIZONTAL;
        grid.weightx = 1;

        // Initialization order no longer affects component placement but shouldn't be changed to avoid bugs
        initMenuBar();
        initFormFields();
        initTable();
        initButtons();
        initStatusBar();
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}