package pashkinmv.gse.api;

import pashkinmv.gse.model.Key;
import pashkinmv.gse.model.Schema;
import pashkinmv.gse.model.Value;

import java.util.List;

public interface GSettings {
    List<Schema> listSchemas();
    List<Key> listKeys(Schema schema);
    Value get(Key key);
    void set(Value value);
    Value reset(Key key);
}
