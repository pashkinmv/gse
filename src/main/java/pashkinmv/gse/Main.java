package pashkinmv.gse;

import pashkinmv.gse.components.KeyList;
import pashkinmv.gse.components.SchemaList;
import pashkinmv.gse.components.ValuePanel;
import pashkinmv.gse.model.Key;
import pashkinmv.gse.model.Schema;

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

        schemaList.addActionListener(new SchemaList.ActionListener() {
            @Override
            public void schemaChanged(Schema schema) {
                keyList.setKeysAndSelect(schema == null ? Collections.emptyList() : schema.getKeys(), KeyList.Select.FIRST);
            }

            @Override
            public void goToKeysRequired(Schema schema) {
                keyList.startKeyNavigation();
            }
        });
        keyList.addKeySelectListener(new KeyList.ActionListener() {
            @Override
            public void keyChanged(Key key) {
                valuePanel.setValue(key == null ? null : key.getValue());
            }

            @Override
            public void goToSchemaRequired() {
                schemaList.startKeyNavigation();
            }

            @Override
            public void selectNextSchema() {
                final Schema newlySelectedSchema = schemaList.selectNextSchema();
                if (newlySelectedSchema != null) {
                    keyList.setKeysAndSelect(newlySelectedSchema.getKeys(), KeyList.Select.FIRST);
                }
            }

            @Override
            public void selectPreviousSchema() {
                final Schema newlySelectedSchema = schemaList.selectPreviousSchema();
                if (newlySelectedSchema != null) {
                    keyList.setKeysAndSelect(newlySelectedSchema.getKeys(), KeyList.Select.LAST);
                }
            }
        });
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