package org.ostrya.presencepublisher.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hypertrack.hyperlog.HyperLog;

import org.ostrya.presencepublisher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum MessageFormat {
    PLAINTEXT {
        @Override
        public List<String> formatContent(
                List<StringEntry> stringEntries, List<ListEntry> listEntries) {
            List<String> result = new ArrayList<>();
            for (StringEntry entry : stringEntries) {
                result.add(entry.getValue());
            }
            for (ListEntry entry : listEntries) {
                result.addAll(entry.getValues());
            }
            return result;
        }
    },
    CSV {
        @Override
        public List<String> formatContent(
                List<StringEntry> stringEntries, List<ListEntry> listEntries) {
            StringBuilder sb = new StringBuilder();
            boolean continuation = false;
            for (StringEntry entry : stringEntries) {
                if (continuation) {
                    sb.append(',');
                }
                sb.append('"')
                        .append(entry.getName())
                        .append('=')
                        .append(entry.getValue())
                        .append('"');
                continuation = true;
            }
            for (ListEntry entry : listEntries) {
                for (String value : entry.getValues()) {
                    if (continuation) {
                        sb.append(',');
                    }
                    sb.append('"').append(entry.getName()).append('=').append(value).append('"');
                    continuation = true;
                }
            }
            return Collections.singletonList(sb.toString());
        }
    },
    JSON {
        @Override
        public List<String> formatContent(
                List<StringEntry> stringEntries, List<ListEntry> listEntries) {
            StringBuilder sb = new StringBuilder();
            sb.append('{').append('\n');
            boolean continuation = false;
            for (StringEntry entry : stringEntries) {
                if (continuation) {
                    sb.append(',').append('\n');
                }
                sb.append("  ")
                        .append('"')
                        .append(entry.getName())
                        .append('"')
                        .append(':')
                        .append(' ')
                        .append('"')
                        .append(entry.getValue())
                        .append('"');
                continuation = true;
            }
            for (ListEntry entry : listEntries) {
                if (continuation) {
                    sb.append(',').append('\n');
                }
                sb.append("  ")
                        .append('"')
                        .append(entry.getName())
                        .append('"')
                        .append(':')
                        .append(' ')
                        .append('[');
                boolean innerContinuation = false;
                for (String value : entry.getValues()) {
                    if (innerContinuation) {
                        sb.append(',');
                    }
                    sb.append('"').append(value).append('"');
                    innerContinuation = true;
                }
                sb.append(']');
                continuation = true;
            }
            sb.append('\n').append('}');
            return Collections.singletonList(sb.toString());
        }
    },
    YAML {
        @Override
        public List<String> formatContent(
                List<StringEntry> stringEntries, List<ListEntry> listEntries) {
            StringBuilder sb = new StringBuilder();
            boolean continuation = false;
            for (StringEntry entry : stringEntries) {
                if (continuation) {
                    sb.append('\n');
                }
                sb.append(entry.getName()).append(':').append(' ').append(entry.getValue());
                continuation = true;
            }
            for (ListEntry entry : listEntries) {
                if (continuation) {
                    sb.append('\n');
                }
                sb.append(entry.getName()).append(':').append(' ').append('[');
                boolean innerContinuation = false;
                for (String value : entry.getValues()) {
                    if (innerContinuation) {
                        sb.append(',');
                    }
                    sb.append(value);
                    innerContinuation = true;
                }
                sb.append(']');
                continuation = true;
            }
            return Collections.singletonList(sb.toString());
        }
    };

    private static final String TAG = "MessageFormat";

    public abstract List<String> formatContent(
            List<StringEntry> stringEntries, List<ListEntry> listEntries);

    /**
     * Note: the values returned here must match with the descriptions given in {@link
     * R.array.message_format_descriptions}
     */
    public static String[] settingValues() {
        return new String[] {PLAINTEXT.name(), CSV.name(), JSON.name(), YAML.name()};
    }

    public static int settingDescriptions() {
        return R.array.message_format_descriptions;
    }

    public static MessageFormat fromStringOrDefault(
            @NonNull String rawValue, @Nullable MessageFormat defaultValue) {
        try {
            return MessageFormat.valueOf(rawValue);
        } catch (IllegalArgumentException e) {
            HyperLog.w(
                    TAG,
                    "Invalid setting '"
                            + rawValue
                            + "'. Used default value '"
                            + defaultValue
                            + "'.");
            return defaultValue;
        }
    }
}
