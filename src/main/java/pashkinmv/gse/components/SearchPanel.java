package pashkinmv.gse.components;

import pashkinmv.gse.model.Key;
import pashkinmv.gse.model.Schema;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SearchPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(SearchPanel.class.getName());

    private final JTextField searchField = new JTextField();
    private final JButton searchButton = new JButton("Search");
    private final TableModel resultModel = new ResultModel();
    private final JTable resultTable = new JTable(resultModel);
    private final List<Row> resultRows = new ArrayList<>();
    private final JCheckBox searchInSchemaNameCheckBox = new JCheckBox("schema name", true);
    private final JCheckBox searchInKeyNameCheckBox = new JCheckBox("key name", true);
    private final JCheckBox searchInValueCheckBox = new JCheckBox("value", true);
    private final List<ActionListener> actionListeners = new ArrayList<>();

    public SearchPanel() {
        super(new BorderLayout());

        searchButton.addActionListener((actionEvent) -> doSearch());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                        doSearch();
                        break;
                }
            }
        });
        resultTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                JTable table =(JTable) me.getSource();
                int rowNumber = table.rowAtPoint(me.getPoint());
                if (me.getClickCount() == 2) {
                    final Row row = resultRows.get(rowNumber);
                    final Schema schema = new Schema(row.getSchemaName());
                    final Key key = new Key(schema, row.getKeyName());

                    fireGoToKeyRequired(key);
                }
            }
        });

        add(getSearchPanel(), BorderLayout.NORTH);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
    }

    private JPanel getSearchPanel() {
        final JPanel globalPanel = new JPanel(new BorderLayout());
        final JPanel topPanel = new JPanel(new BorderLayout());
        final JPanel checkboxPanel = new JPanel();

        checkboxPanel.add(new JLabel("Search in:"));
        checkboxPanel.add(searchInSchemaNameCheckBox);
        checkboxPanel.add(searchInKeyNameCheckBox);
        checkboxPanel.add(searchInValueCheckBox);

        topPanel.add(searchField);
        topPanel.add(searchButton, BorderLayout.EAST);

        globalPanel.add(topPanel, BorderLayout.NORTH);
        globalPanel.add(checkboxPanel);

        return globalPanel;
    }

    private void fireGoToKeyRequired(Key key) {
        for (ActionListener actionListener : actionListeners) {
            actionListener.goToKeyRequired(key);
        }
    }

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    public interface ActionListener {
        void goToKeyRequired(Key key);
    }

    private class ResultModel implements TableModel {
        private final List<TableModelListener> tableModelListeners = new ArrayList<>();
        private final String[] columnNames = {"Schema name", "Key name", "Value"};

        @Override
        public int getRowCount() {
            return resultRows.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final Row row = resultRows.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return row.getSchemaName();
                case 1:
                    return row.getKeyName();
                case 2:
                    return row.getValue();
            }

            throw new IllegalArgumentException("Unexpected column index");
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            throw new RuntimeException("Not implemented yet");
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            tableModelListeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            tableModelListeners.remove(l);
        }
    }

    private class Row {
        private final String schemaName;
        private final String keyName;
        private final String value;

        private Row(String schemaName, String keyName, String value) {
            this.schemaName = schemaName;
            this.keyName = keyName;
            this.value = value;
        }

        public String getSchemaName() {
            return schemaName;
        }

        public String getKeyName() {
            return keyName;
        }

        public String getValue() {
            return value;
        }
    }

    private void doSearch() {
        try {
            resultRows.clear();

            final String command = "gsettings list-recursively";
            LOGGER.info("Execute command: " + command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final Row row = convertToRow(line);

                    if (matchesFilter(row)) {
                        resultRows.add(row);
                    }
                }
            }

            resultRows.sort((row1, row2) -> {
                if (row1.getSchemaName().equals(row2.getSchemaName())) {
                    return row1.getKeyName().compareTo(row2.getKeyName());
                }

                return row1.getSchemaName().compareTo(row2.getSchemaName());
            });

            SwingUtilities.invokeLater(resultTable::updateUI);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Row convertToRow(String line) {
        final int firstSpaceIndex = line.indexOf(" ");
        final int secondSpaceIndex = line.indexOf(" ", firstSpaceIndex + 1);

        return new Row(line.substring(0, firstSpaceIndex), line.substring(firstSpaceIndex + 1, secondSpaceIndex), line.substring(secondSpaceIndex + 1));
    }

    private boolean matchesFilter(Row row) {
        final String searchString = searchField.getText().trim().toLowerCase();

        return searchInSchemaNameCheckBox.isSelected() && row.getSchemaName().toLowerCase().contains(searchString) ||
                searchInKeyNameCheckBox.isSelected() && row.getKeyName().toLowerCase().contains(searchString) ||
                searchInValueCheckBox.isSelected() && row.getValue().toLowerCase().contains(searchString);
    }
}
