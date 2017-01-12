package pashkinmv.gse.model;

public class Value {
    private final Key key;
    private final String value;
    private final String range;
    private final boolean writable;

    public Value(Key key, String value, String range, boolean writable) {
        this.key = key;
        this.value = value;
        this.range = range;
        this.writable = writable;
    }

    public Key getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getRange() {
        return range;
    }

    public boolean getWritable() {
        return writable;
    }
}
