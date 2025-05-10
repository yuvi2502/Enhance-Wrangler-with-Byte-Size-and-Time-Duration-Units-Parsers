package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@PublicEvolving
public class TimeDuration implements Token {
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)\\s*([smhdwy]|mo)");
    private final String value;
    private final long milliseconds;


    public TimeDuration(String value) {
        this.value = value;
        this.milliseconds = parseMilliseconds(value);
    }

    /**
     * Parses a string representation of time duration into milliseconds.
     *
     * @param durationStr String representation of a time duration (e.g., "5s")
     * @return The duration in milliseconds
     * @throws IllegalArgumentException if the string cannot be parsed
     */
    private long parseMilliseconds(String durationStr) {
        Matcher matcher = TIME_PATTERN.matcher(durationStr);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid time duration format: " + durationStr);
        }

        long amount = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2).toLowerCase();

        switch (unit) {
            case "s":
                return TimeUnit.SECONDS.toMillis(amount);
            case "m":
                return TimeUnit.MINUTES.toMillis(amount);
            case "h":
                return TimeUnit.HOURS.toMillis(amount);
            case "d":
                return TimeUnit.DAYS.toMillis(amount);
            case "w":
                return TimeUnit.DAYS.toMillis(amount * 7);
            case "mo":
                // Approximate a month as 30 days
                return TimeUnit.DAYS.toMillis(amount * 30);
            case "y":
                // Approximate a year as 365 days
                return TimeUnit.DAYS.toMillis(amount * 365);
            default:
                throw new IllegalArgumentException("Unsupported time unit: " + unit);
        }
    }

    /**
     * Returns the original string representation of the time duration.
     */
    @Override
    public String value() {
        return value;
    }

    /**
     * Returns the duration in milliseconds.
     *
     * @return The duration in milliseconds
     */
    public long getMilliseconds() {
        return milliseconds;
    }

    /**
     * Returns the duration in seconds.
     *
     * @return The duration in seconds
     */
    public double getSeconds() {
        return milliseconds / 1000.0;
    }

    /**
     * Returns the duration in minutes.
     *
     * @return The duration in minutes
     */
    public double getMinutes() {
        return milliseconds / (1000.0 * 60);
    }

    /**
     * Returns the duration in hours.
     *
     * @return The duration in hours
     */
    public double getHours() {
        return milliseconds / (1000.0 * 60 * 60);
    }

    /**
     * Returns the duration in days.
     *
     * @return The duration in days
     */
    public double getDays() {
        return milliseconds / (1000.0 * 60 * 60 * 24);
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION;
    }

    @Override
    public JsonElement toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", TokenType.TIME_DURATION.name());
        object.addProperty("value", value);
        object.addProperty("milliseconds", milliseconds);
        return object;
    }
}
