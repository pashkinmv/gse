package pashkinmv.gse;

import pashkinmv.gse.components.KeyList;
import pashkinmv.gse.components.SchemaList;
import pashkinmv.gse.components.ValuePanel;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {
        final JFrame jFrame = new JFrame("GSettings configuration GUI tool");
        final SchemaList schemaList = new SchemaList();
        final KeyList keyList = new KeyList();
        final ValuePanel valuePanel = new ValuePanel();
        final JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keyList, valuePanel);
        final JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, schemaList, splitPane2);

        schemaList.addSchemaSelectListener(schema -> {
            keyList.setKeys(schema == null ? Collections.emptyList() : schema.getKeys());
            valuePanel.setValue(null);
        });
        keyList.addKeySelectListener(key -> valuePanel.setValue(key.getValue()));
        splitPane1.setOneTouchExpandable(true);
        splitPane1.setDividerLocation(300);
        splitPane2.setOneTouchExpandable(true);
        splitPane2.setDividerLocation(300);

        jFrame.setSize(900, 600);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jFrame.setVisible(true);

        jFrame.add(splitPane1);
    }
}