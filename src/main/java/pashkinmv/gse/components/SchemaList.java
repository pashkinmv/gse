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
    private final List<ActionListener> actionListeners = new ArrayList<>();
    private final List<Schema> schemas = SchemaManager.getSchemas();
    private final List<Schema> filteredSchemas = new ArrayList<>(schemas);
    private final ListModel<String> model = new FilteredSchemaModel();
    private final JList<String> schemaList = new JList<>(model);
    private final ListSelectionModel selectionModel = schemaList.getSelectionModel();
    private final JTextField filterField = new JTextField();

    private Schema currentSchema;
    private boolean ignoreChangingSchema;

    public SchemaList() {
        super(new BorderLayout());

        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(event -> {
            if (ignoreChangingSchema) {
                return;
            }

            if (!event.getValueIsAdjusting()) {
                Schema newSchema = null;

                if (selectionModel.getMinSelectionIndex() != -1 && filteredSchemas.size() > selectionModel.getMinSelectionIndex()) {
                    newSchema = filteredSchemas.get(selectionModel.getMinSelectionIndex());
                }

                if (newSchema != currentSchema) {
                    currentSchema = newSchema;
                    fireSchemaChanged();
                }
            }
        });

        schemaList.addKeyListener(new SchemaListKeyListener());
        filterField.addKeyListener(new FilterFieldKeyListener());

        final JPanel panel = new JPanel(new BorderLayout());

        panel.add(filterField, BorderLayout.NORTH);
        panel.add(new JScrollPane(schemaList));

        add(new JLabel("Schemas", JLabel.CENTER), BorderLayout.NORTH);
        add(panel);
    }

    public void startKeyNavigation() {
        schemaList.requestFocus();
    }

    public Schema selectNextSchema() {
        ignoreChangingSchema = true;

        final int currentSelectionIndex = selectionModel.getMinSelectionIndex();
        final int newSelectionIndex = currentSelectionIndex + 1;

        if (currentSelectionIndex < filteredSchemas.size() - 1) {
            selectionModel.setSelectionInterval(newSelectionIndex, newSelectionIndex);
            currentSchema = filteredSchemas.get(newSelectionIndex);
        }

        ignoreChangingSchema = false;

        return currentSchema;
    }

    public Schema selectPreviousSchema() {
        ignoreChangingSchema = true;

        final int currentSelectionIndex = selectionModel.getMinSelectionIndex();
        final int newSelectionIndex = currentSelectionIndex - 1;

        if (currentSelectionIndex > 0) {
            selectionModel.setSelectionInterval(newSelectionIndex, newSelectionIndex);
            currentSchema = filteredSchemas.get(newSelectionIndex);
        }

        ignoreChangingSchema = false;

        return currentSchema;
    }

    public void addActionListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    private void fireSchemaChanged() {
        for (ActionListener actionListener : actionListeners) {
            actionListener.schemaChanged(currentSchema);
        }
    }

    private void fireGoToKeysRequired() {
        for (ActionListener actionListener : actionListeners) {
            actionListener.goToKeysRequired(currentSchema);
        }
    }

    public interface ActionListener {
        void schemaChanged(Schema schema);
        void goToKeysRequired(Schema schema);
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

    private class SchemaListKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                fireGoToKeysRequired();
            }
        }
    }

    private class FilterFieldKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            ignoreChangingSchema = true;

            final Schema selectedSchema = getSelectedSchema();
            clearSelection();
            filterSchemas();
            setSelectedSchema(selectedSchema);
            updateSchemaListUI();

            ignoreChangingSchema = false;
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
        boolean selectionNotRestored = true;

        if (selectedSchema != null) {
            final int selectedElementIndex = filteredSchemas.indexOf(selectedSchema);
            if (selectedElementIndex != -1) {
                selectionNotRestored = false;
                selectionModel.setSelectionInterval(selectedElementIndex, selectedElementIndex);
            }
        }

        if (selectionNotRestored && currentSchema != null) {
            currentSchema = null;
            fireSchemaChanged();
        }
    }
}
