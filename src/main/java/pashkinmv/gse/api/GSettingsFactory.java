package pashkinmv.gse.api;

public class GSettingsFactory {
    private static GSettings instance = new GSettingsImpl();

    public static GSettings get() {
        return instance;
    }
}
