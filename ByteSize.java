package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



@PublicEvolving
public class ByteSize implements Token {
    private static final Pattern BYTE_SIZE_PATTERN = Pattern.compile("(\\d+)\\s*([kKmMgGtTpP]?[bB]?)");
    private final String value;
    private final long bytes;

    public ByteSize(String value) {
        this.value = value;
        this.bytes = parseBytes(value);
    }


    private long parseBytes(String sizeStr) {
        Matcher matcher = BYTE_SIZE_PATTERN.matcher(sizeStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid byte size format: " + sizeStr);
        }

        long size = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2).toUpperCase();

        switch (unit) {
            case "B":
            case "":
                return size;
            case "KB":
            case "K":
                return size * 1024;
            case "MB":
            case "M":
                return size * 1024 * 1024;
            case "GB":
            case "G":
                return size * 1024 * 1024 * 1024;
            case "TB":
            case "T":
                return size * 1024 * 1024 * 1024 * 1024;
            case "PB":
            case "P":
                return size * 1024 * 1024 * 1024 * 1024 * 1024;
            default:
                throw new IllegalArgumentException("Unsupported byte size unit: " + unit);
        }
    }

    /**
     * Returns the original string representation of the byte size.
     */
    @Override
    public String value() {
        return value;
    }


    public long getBytes() {
        return bytes;
    }

    public double getKilobytes() {
        return bytes / 1024.0;
    }


    public double getMegabytes() {
        return bytes / (1024.0 * 1024.0);
    }


    public double getGigabytes() {
        return bytes / (1024.0 * 1024.0 * 1024.0);
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", TokenType.BYTE_SIZE.name());
        object.addProperty("value", value);
        object.addProperty("bytes", bytes);
        return object;
    }
}