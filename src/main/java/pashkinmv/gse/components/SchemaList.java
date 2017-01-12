package pashkinmv.gse.components;

import pashkinmv.gse.model.Schema;
import pashkinmv.gse.model.SchemaManager;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SchemaList extends JPanel {
    private final List<SchemaSelectListener> schemaSelectListeners = new ArrayList<>();
    private final List<Schema> schemas = SchemaManager.getSchemas();
    private final List<Schema> filteredSchemas = new ArrayList<>(schemas);
    private final ListModel<String> model = new FilteredSchemaModel();
    private final JList<String> schemaList = new JList<>(model);
    private final ListSelectionModel selectionModel = schemaList.getSelectionModel();
    private final JTextField filterField = new JTextField();

    public SchemaList() {
        super(new BorderLayout());

        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                if (selectionModel.getMinSelectionIndex() != -1 && filteredSchemas.size() > selectionModel.getMinSelectionIndex()) {
                    fireSchemaSelected(filteredSchemas.get(selectionModel.getMinSelectionIndex()));
                }
            }
        });

        filterField.addKeyListener(new FilterFieldKeyListener());

        final JPanel panel = new JPanel(new BorderLayout());

        panel.add(filterField, BorderLayout.NORTH);
        panel.add(new JScrollPane(schemaList));

        add(new JLabel("Schemas", JLabel.CENTER), BorderLayout.NORTH);
        add(panel);
    }

    public void addSchemaSelectListener(SchemaSelectListener schemaSelectListener) {
        schemaSelectListeners.add(schemaSelectListener);
    }

    private void fireSchemaSelected(Schema schema) {
        for (SchemaSelectListener schemaSelectListener : schemaSelectListeners) {
            schemaSelectListener.schemaSelected(schema);
        }
    }

    public interface SchemaSelectListener {
        void schemaSelected(Schema schema);
    }

    private class FilteredSchemaModel implements ListModel<String> {
        @Override
        public int getSize() {
            return filteredSchemas.size();
        }

        @Override
        public String getElementAt(int index) {
            return filteredSchemas.get(index).getCode();
        }

        @Override
        public void addListDataListener(ListDataListener l) {

        }

        @Override
        public void removeListDataListener(ListDataListener l) {

        }
    }

    private class FilterFieldKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            final Schema selectedSchema = getSelectedSchema();
            clearSelection();
            filterSchemas();
            setSelectedSchema(selectedSchema);
            updateSchemaListUI();
        }
    }

    private void updateSchemaListUI() {
        SwingUtilities.invokeLater(schemaList::updateUI);
    }

    private void clearSelection() {
        selectionModel.clearSelection();
    }

    private void filterSchemas() {
        filteredSchemas.clear();

        final String filter = filterField.getText().trim().toLowerCase();
        for (Schema schema : schemas) {
            if (schema.getCode().toLowerCase().contains(filter)) {
                filteredSchemas.add(schema);
            }
        }
    }

    private Schema getSelectedSchema() {
        Schema selectedSchema = null;

        final int selectionIndex = selectionModel.getMinSelectionIndex();

        if (selectionIndex != -1 && selectionIndex < filteredSchemas.size()) {
            selectedSchema = filteredSchemas.get(selectionIndex);
        }

        return selectedSchema;
    }

    private void setSelectedSchema(Schema selectedSchema) {
        if (selectedSchema != null) {
            final int selectedElementIndex = filteredSchemas.indexOf(selectedSchema);
            if (selectedElementIndex != -1) {
                selectionModel.setSelectionInterval(selectedElementIndex, selectedElementIndex);
            }
        }
    }
}