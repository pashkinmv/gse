package pashkinmv.gse.components;

import pashkinmv.gse.api.GSettingsFactory;
import pashkinmv.gse.model.Value;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.util.logging.Logger;

public class ValuePanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(ValuePanel.class.getName());
    private final JTextArea textArea = new JTextArea();
    private final JButton saveButton = new JButton("Save");
    private final JButton resetButton = new JButton("Reset");
    private final JLabel rangeLabel = new JLabel();

    private Value value;

    public ValuePanel() {
        super(new BorderLayout());

        saveButton.addActionListener(action -> GSettingsFactory.get().set(new Value(value.getKey(), textArea.getText(), value.getRange(), value.getWritable())));
        resetButton.addActionListener(action -> {
            final Value newValue = GSettingsFactory.get().reset(value.getKey());

            textArea.setText(newValue.getValue());
        });
        rangeLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        final JPanel buttonPanel = new JPanel();

        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);

        add(rangeLabel, BorderLayout.NORTH);
        add(new JScrollPane(textArea));
        add(buttonPanel, BorderLayout.SOUTH);

        setValue(null);
    }

    public void setValue(Value value) {
        this.value = value;
        this.textArea.setText(value != null ? value.getValue() : "");
        this.rangeLabel.setText(value != null ? ("Type: " + getRangeAsType(value.getRange())) : null);

        this.textArea.setEditable(value != null && value.getWritable());
        this.saveButton.setEnabled(value != null && value.getWritable());
        this.resetButton.setEnabled(value != null && value.getWritable());
    }

    private String getRangeAsType(String range) {
        if (range.startsWith("range i ") || range.startsWith("range u ")) {
            return range.replaceAll("range . (.+) (.+)", "Integer [$1..$2]");
        }

        switch (range) {
            case "enum":
                return "Enum";
            case "type a(ss)":
                return "a(ss)";
            case "type a{ss}":
                return "a{ss}";
            case "type a{si}":
                return "a{si}";
            case "type as":
                return "as";
            case "type b":
                return "Boolean";
            case "type d":
                return "Double [2.2250738585072014e-308..1.7976931348623157e+308]";
            case "type i":
                return "Integer [-2147483648..2147483647]";
            case "type o":
                return "o";
            case "type s":
                return "String";
            case "type u":
                return "Integer [0..4294967295]";
            default:
                LOGGER.warning(String.format("Unknown type: %s for schema %s and key %s", range, value.getKey().getSchema().getCode(), value.getKey().getCode()));
                return range;
        }
    }
}
