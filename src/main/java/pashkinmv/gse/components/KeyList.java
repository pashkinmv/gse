package pashkinmv.gse.components;

import pashkinmv.gse.model.Key;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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
    private static final String TITLE = "Keys";

    private final List<ActionListener> actionListeners = new ArrayList<>();
    private final List<Key> keys = new ArrayList<>();
    private final List<Key> filteredKeys = new ArrayList<>();
    private final ListModel<String> model = new FilteredKeyModel();
    private final JList<String> keyList = new JList<>(model);
    private final ListSelectionModel selectionModel = keyList.getSelectionModel();
    private final JTextField filterField = new JTextField();

    private Key currentKey;
    private boolean ignoreChangingKey;

    public KeyList() {
        super(new BorderLayout());

        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(event -> {
            if (ignoreChangingKey) {
                return;
            }

            if (!event.getValueIsAdjusting()) {
                Key newKey = null;

                if (selectionModel.getMinSelectionIndex() != -1 && filteredKeys.size() > selectionModel.getMinSelectionIndex()) {
                    newKey = filteredKeys.get(selectionModel.getMinSelectionIndex());
                }

                if (newKey != currentKey) {
                    currentKey = newKey;
                    fireKeyChanged();
                }
            }
        });

        keyList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "none");
        keyList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "none");

        keyList.addKeyListener(new KeyListKeyListener());
        filterField.addKeyListener(new FilterFieldKeyListener());

        final JPanel panel = new JPanel(new BorderLayout());

        panel.add(filterField, BorderLayout.NORTH);
        panel.add(new JScrollPane(keyList));

        add(new JLabel(TITLE, JLabel.CENTER), BorderLayout.NORTH);
        add(panel);
    }

    public void setKeysAndSelect(List<Key> keys, Select select) {
        if (!this.keys.isEmpty() && !keys.isEmpty() && this.keys.get(0).getSchema().equals(keys.get(0).getSchema())) {
            return;
        }

        this.filterField.setText("");
        this.keys.clear();
        this.keys.addAll(keys);
        this.filteredKeys.clear();
        this.filteredKeys.addAll(keys);

        if (keys.isEmpty()) {
            if (currentKey != null) {
                currentKey = null;
                selectionModel.clearSelection();
                fireKeyChanged();
            }
        } else if (select == Select.FIRST) {
            if (selectionModel.getMinSelectionIndex() == 0) {
                currentKey = keys.get(0);
                fireKeyChanged();
            } else {
                selectionModel.setSelectionInterval(0, 0);
            }
        } else if (select == Select.LAST){
            final int lastKeyIndex = keys.size() - 1;
            if (selectionModel.getMinSelectionIndex() == lastKeyIndex) {
                currentKey = keys.get(lastKeyIndex);
                fireKeyChanged();
            } else {
                selectionModel.setSelectionInterval(lastKeyIndex, lastKeyIndex);
            }
        }

        SwingUtilities.invokeLater(() -> {
            keyList.updateUI();

            if (selectionModel.getMaxSelectionIndex() != -1) {
                keyList.ensureIndexIsVisible(selectionModel.getMaxSelectionIndex());
            }
        });
    }

    public void setKeysAndSelect(List<Key> keys, Key key) {
        this.filterField.setText("");
        this.keys.clear();
        this.keys.addAll(keys);
        this.filteredKeys.clear();
        this.filteredKeys.addAll(keys);

        currentKey = key;

        final int currentSelectionIndex = selectionModel.getMinSelectionIndex();
        final int newSelectionIndex = keys.indexOf(key);

        if (currentSelectionIndex != newSelectionIndex) {
            selectionModel.setSelectionInterval(newSelectionIndex, newSelectionIndex);
            fireKeyChanged();
        }

        SwingUtilities.invokeLater(() -> {
            keyList.updateUI();

            if (selectionModel.getMaxSelectionIndex() != -1) {
                keyList.ensureIndexIsVisible(selectionModel.getMaxSelectionIndex());
            }

            keyList.requestFocusInWindow();
        });
    }

    public void addKeySelectListener(ActionListener actionListener) {
        actionListeners.add(actionListener);
    }

    public void startKeyNavigation() {
        keyList.requestFocusInWindow();
    }

    public enum Select {
        LAST, FIRST
    }

    public interface ActionListener {
        void keyChanged(Key key);
        void goToSchemaRequired();
        void selectNextSchema();
        void selectPreviousSchema();
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

    private class KeyListKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            final int currentSelectionIndex = selectionModel.getMinSelectionIndex();

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    fireGoToSchemaRequired();
                    break;
                case KeyEvent.VK_UP:
                    if (currentSelectionIndex == 0 || currentSelectionIndex == -1) {
                        fireSelectPreviousSchema();
                    } else {
                        final int newSelectionIndex = currentSelectionIndex - 1;
                        selectionModel.setSelectionInterval(newSelectionIndex, newSelectionIndex);
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (selectionModel.getMinSelectionIndex() == filteredKeys.size() - 1) {
                        fireSelectNextSchema();
                    } else {
                        final int newSelectionIndex = currentSelectionIndex + 1;
                        selectionModel.setSelectionInterval(newSelectionIndex, newSelectionIndex);
                    }
                    break;
            }
        }
    }

    private class FilterFieldKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            ignoreChangingKey = true;

            final Key selectedKey = getSelectedKey();
            clearSelection();
            filterKeys();
            setSelectedKey(selectedKey);
            SwingUtilities.invokeLater(keyList::updateUI);

            ignoreChangingKey = false;
        }
    }

    private void fireKeyChanged() {
        for (ActionListener actionListener : actionListeners) {
            actionListener.keyChanged(currentKey);
        }
    }

    private void fireGoToSchemaRequired() {
        for (ActionListener actionListener : actionListeners) {
            actionListener.goToSchemaRequired();
        }
    }

    private void fireSelectNextSchema() {
        for (ActionListener actionListener : actionListeners) {
            actionListener.selectNextSchema();
        }
    }

    private void fireSelectPreviousSchema() {
        for (ActionListener actionListener : actionListeners) {
            actionListener.selectPreviousSchema();
        }
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
        boolean selectionNotRestored = true;

        if (selectedKey != null) {
            final int selectedElementIndex = filteredKeys.indexOf(selectedKey);
            if (selectedElementIndex != -1) {
                selectionNotRestored = false;
                selectionModel.setSelectionInterval(selectedElementIndex, selectedElementIndex);
            }
        }

        if (selectionNotRestored && currentKey != null) {
            currentKey = null;
            fireKeyChanged();
        }
    }
}
