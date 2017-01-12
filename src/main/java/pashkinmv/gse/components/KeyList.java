package pashkinmv.gse.components;

import pashkinmv.gse.model.Key;

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

public class KeyList extends JPanel {
    private final List<KeySelectListener> keySelectListeners = new ArrayList<>();
    private final List<Key> keys = new ArrayList<>();
    private final List<Key> filteredKeys = new ArrayList<>();
    private final ListModel<String> model = new FilteredKeyModel();
    private final JList<String> keyList = new JList<>(model);
    private final ListSelectionModel selectionModel = keyList.getSelectionModel();
    private final JTextField filterField = new JTextField();

    public KeyList() {
        super(new BorderLayout());

        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                if (selectionModel.getMinSelectionIndex() != -1) {
                    fireKeySelected(filteredKeys.get(selectionModel.getMinSelectionIndex()));
                }
            }
        });

        filterField.addKeyListener(new FilterFieldKeyListener());

        final JPanel panel = new JPanel(new BorderLayout());

        panel.add(filterField, BorderLayout.NORTH);
        panel.add(new JScrollPane(keyList));

        add(new JLabel("Key", JLabel.CENTER), BorderLayout.NORTH);
        add(panel);
    }

    public void setKeys(List<Key> keys) {
        if (!this.keys.isEmpty() && !keys.isEmpty() && this.keys.get(0).getSchema().equals(keys.get(0).getSchema())) {
            return;
        }

        this.keys.clear();
        this.keys.addAll(keys);
        this.filterField.setText("");

        clearSelection();
        filterKeys();
        updateKeyListUI();
    }

    public void addKeySelectListener(KeySelectListener keySelectListener) {
        keySelectListeners.add(keySelectListener);
    }

    private void fireKeySelected(Key key) {
        for (KeySelectListener keySelectListener : keySelectListeners) {
            keySelectListener.keySelected(key);
        }
    }

    public interface KeySelectListener {
        void keySelected(Key key);
    }

    private class FilteredKeyModel implements ListModel<String> {
        @Override
        public int getSize() {
            return filteredKeys.size();
        }

        @Override
        public String getElementAt(int index) {
            return filteredKeys.get(index).getCode();
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
            final Key selectedKey = getSelectedKey();
            clearSelection();
            filterKeys();
            setSelectedKey(selectedKey);
            updateKeyListUI();
        }
    }

    private void updateKeyListUI() {
        SwingUtilities.invokeLater(keyList::updateUI);
    }

    private void clearSelection() {
        selectionModel.clearSelection();
    }

    private void filterKeys() {
        filteredKeys.clear();

        final String filter = filterField.getText().trim().toLowerCase();
        for (Key key : keys) {
            if (key.getCode().toLowerCase().contains(filter)) {
                filteredKeys.add(key);
            }
        }
    }

    private Key getSelectedKey() {
        Key selectedKey = null;

        final int selectionIndex = selectionModel.getMinSelectionIndex();

        if (selectionIndex != -1 && selectionIndex < filteredKeys.size()) {
            selectedKey = filteredKeys.get(selectionIndex);
        }

        return selectedKey;
    }

    private void setSelectedKey(Key selectedKey) {
        if (selectedKey != null) {
            final int selectedElementIndex = filteredKeys.indexOf(selectedKey);
            if (selectedElementIndex != -1) {
                selectionModel.setSelectionInterval(selectedElementIndex, selectedElementIndex);
            }
        }
    }
}
