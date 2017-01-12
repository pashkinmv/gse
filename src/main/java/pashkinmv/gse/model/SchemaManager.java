package pashkinmv.gse.model;

import pashkinmv.gse.GSettingsWrapper;

import java.util.List;

public class SchemaManager {
    public static List<Schema> getSchemas() {
        return GSettingsWrapper.listSchemas();
    }
}
