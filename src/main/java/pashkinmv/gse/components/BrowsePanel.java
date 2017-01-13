package pashkinmv.gse.components;

import pashkinmv.gse.api.GSettingsFactory;
import pashkinmv.gse.model.Key;
import pashkinmv.gse.model.Schema;

import javax.swing.JSplitPane;
import java.util.Collections;

public class BrowsePanel extends JSplitPane {
    private final SchemaList schemaList = new SchemaList();
    private final KeyList keyList = new KeyList();
    private final ValuePanel valuePanel = new ValuePanel();
    private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keyList, valuePanel);

    public BrowsePanel() {
        init();
    }

    private void init() {
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

        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(300);

        setOneTouchExpandable(true);
        setDividerLocation(300);
        setLeftComponent(schemaList);
        setRightComponent(splitPane);
    }

    public void selectKey(Key key) {
        schemaList.selectSchema(key.getSchema());
        keyList.setKeysAndSelect(GSettingsFactory.get().listKeys(key.getSchema()), key);
    }
}
