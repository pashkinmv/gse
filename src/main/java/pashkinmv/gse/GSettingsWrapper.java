package pashkinmv.gse;

import pashkinmv.gse.model.Key;
import pashkinmv.gse.model.Schema;
import pashkinmv.gse.model.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class GSettingsWrapper {
    private static final Logger LOGGER = Logger.getLogger(GSettingsWrapper.class.getName());
    private static final String COMMAND_LIST_SCHEMAS = "gsettings list-schemas";
    private static final String COMMAND_LIST_KEYS = "gsettings list-keys %s";
    private static final String COMMAND_GET = "gsettings get %s %s";
    private static final String COMMAND_SET = "gsettings set %s %s %s";
    private static final String COMMAND_RESET = "gsettings reset %s %s";
    private static final String COMMAND_MONITOR = "gsettings monitor %s";
    private static final String COMMAND_RANGE = "gsettings range %s %s";
    private static final String COMMAND_WRITABLE = "gsettings writable %s %s";

    public static List<Schema> listSchemas() {
        final List<Schema> schemas = new ArrayList<>();

        try {
            final String command = COMMAND_LIST_SCHEMAS;
            LOGGER.info("Execute command: " + command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(COMMAND_LIST_SCHEMAS).getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    schemas.add(new Schema(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        schemas.sort(Comparator.comparing(Schema::getCode));

        return schemas;
    }

    public static List<Key> listKeys(Schema schema) {
        final List<Key> keys = new ArrayList<>();

        try {
            final String command = String.format(COMMAND_LIST_KEYS, schema.getCode());
            LOGGER.info("Execute command: " + command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    keys.add(new Key(schema, line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        keys.sort(Comparator.comparing(Key::getCode));

        return keys;
    }

    public static Value get(Key key) {
        try {
            final StringBuilder value = new StringBuilder();

            final String command = String.format(COMMAND_GET, key.getSchema().getCode(), key.getCode());
            LOGGER.info("Execute command: " + command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (value.length() > 0) {
                        value.append("\r\n");
                    }

                    value.append(line);
                }
            }

            return new Value(key, value.toString(), range(key), writable(key));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(Value value) {
        try {
            final String command = String.format(COMMAND_SET, value.getKey().getSchema().getCode(), value.getKey().getCode(), value.getValue());
            LOGGER.info("Execute command: " + command);

            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Value reset(Key key) {
        try {
            final String command = String.format(COMMAND_MONITOR, key.getSchema().getCode());
            LOGGER.info("Execute command: " + command);

            final Process process = Runtime.getRuntime().exec(command);

            Executors.newSingleThreadExecutor().submit(() -> {
                doReset(key);
            });

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(key.getCode() + ": ")) {
                        return new Value(key, line.substring((key.getCode() + ": ").length()), range(key), writable(key));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Unexpected exception");
    }

    private static void doReset(Key key) {
        try {
            final String command = String.format(COMMAND_RESET, key.getSchema().getCode(), key.getCode());
            LOGGER.info("Execute command: " + command);

            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String range(Key key) {
        try {
            final String command = String.format(COMMAND_RANGE, key.getSchema().getCode(), key.getCode());
            LOGGER.info("Execute command: " + command);

            final Process process = Runtime.getRuntime().exec(command);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean writable(Key key) {
        try {
            final String command = String.format(COMMAND_WRITABLE, key.getSchema().getCode(), key.getCode());
            LOGGER.info("Execute command: " + command);

            final Process process = Runtime.getRuntime().exec(command);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                return Boolean.valueOf(reader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
