package pashkinmv.gse.components;

import pashkinmv.gse.api.GSettingsFactory;
import pashkinmv.gse.model.Key;
import pashkinmv.gse.model.Schema;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import java.awt.HeadlessException;
import java.util.Collections;

public class MainFrame extends JFrame {
    private static final String TITLE = "GSettingsImpl configuration GUI tool";

    public MainFrame() throws HeadlessException {
        super(TITLE);

        final SchemaList schemaList = new SchemaList();
        final KeyList keyList = new KeyList();
        final ValuePanel valuePanel = new ValuePanel();
        final JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keyList, valuePanel);
        final JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, schemaList, splitPane2);

        schemaList.addActionListener(new SchemaList.ActionListener() {
            @Override
            public void schemaChanged(Schema schema) {
                keyList.setKeysAndSelect(schema == null ? Collections.emptyList() : GSettingsFactory.get().listKeys(schema), KeyList.Select.FIRST);
            }

            @Override
            public void goToKeysRequired(Schema schema) {
                keyList.startKeyNavigation();
            }
        });
        keyList.addKeySelectListener(new KeyList.ActionListener() {
            @Override
            public void keyChanged(Key key) {
                valuePanel.setValue(key == null ? null : GSettingsFactory.get().get(key));
            }

            @Override
            public void goToSchemaRequired() {
                schemaList.startKeyNavigation();
            }

            @Override
            public void selectNextSchema() {
                final Schema newlySelectedSchema = schemaList.selectNextSchema();
                if (newlySelectedSchema != null) {
                    keyList.setKeysAndSelect(GSettingsFactory.get().listKeys(newlySelectedSchema), KeyList.Select.FIRST);
                }
            }

            @Override
            public void selectPreviousSchema() {
                final Schema newlySelectedSchema = schemaList.selectPreviousSchema();
                if (newlySelectedSchema != null) {
                    keyList.setKeysAndSelect(GSettingsFactory.get().listKeys(newlySelectedSchema), KeyList.Select.LAST);
                }
            }
        });
        splitPane1.setOneTouchExpandable(true);
        splitPane1.setDividerLocation(300);
        splitPane2.setOneTouchExpandable(true);
        splitPane2.setDividerLocation(300);

        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        add(splitPane1);
    }
}
