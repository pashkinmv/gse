package pashkinmv.gse.model;

import pashkinmv.gse.GSettingsWrapper;

public class Key {
    private final Schema schema;
    private final String code;

    public Key(Schema schema, String code) {
        this.schema = schema;
        this.code = code;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getCode() {
        return code;
    }

    public Value getValue() {
        return GSettingsWrapper.get(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key key = (Key) o;

        if (!schema.equals(key.schema)) return false;
        return code.equals(key.code);
    }

    @Override
    public int hashCode() {
        int result = schema.hashCode();
        result = 31 * result + code.hashCode();
        return result;
    }
}
