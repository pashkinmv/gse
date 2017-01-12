package pashkinmv.gse.components;

import pashkinmv.gse.GSettingsWrapper;
import pashkinmv.gse.model.Value;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class ValuePanel extends JPanel {
    private final JTextArea textArea = new JTextArea();
    private final JButton saveButton = new JButton("Save");
    private final JButton resetButton = new JButton("Reset");
    private final JLabel rangeLabel = new JLabel();

    private Value value;

    public ValuePanel() {
        super(new BorderLayout());

        saveButton.addActionListener(action -> GSettingsWrapper.set(new Value(value.getKey(), textArea.getText(), value.getRange(), value.getWritable())));
        resetButton.addActionListener(action -> {
            final Value newValue = GSettingsWrapper.reset(value.getKey());

            textArea.setText(newValue.getValue());
        });
        rangeLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        final JPanel buttonPanel = new JPanel();

        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);

        add(rangeLabel, BorderLayout.NORTH);
        add(new JScrollPane(textArea));
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setValue(Value value) {
        this.value = value;
        this.textArea.setText(value != null ? value.getValue() : "");
        this.rangeLabel.setText(value != null ? ("Range: " + value.getRange()) : null);

        this.textArea.setEditable(value != null && value.getWritable());
        this.saveButton.setEnabled(value != null && value.getWritable());
        this.resetButton.setEnabled(value != null && value.getWritable());
    }
}
