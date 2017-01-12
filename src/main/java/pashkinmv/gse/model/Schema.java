package pashkinmv.gse.model;

import pashkinmv.gse.GSettingsWrapper;

import java.util.List;

public class Schema {
    private final String code;

    public Schema(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public List<Key> getKeys() {
        return GSettingsWrapper.listKeys(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schema schema = (Schema) o;

        return code.equals(schema.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
