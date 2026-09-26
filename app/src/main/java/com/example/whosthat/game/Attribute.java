package com.example.whosthat.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * A comparable property of a subject (a Pokemon or a champion), e.g. "Type 1" or "Release year".
 * Unknown values are represented by null and compare as {@link Match#UNKNOWN}.
 */
public final class Attribute<T> {
    public static final String UNKNOWN_VALUE = "?";

    private enum Kind { TEXT, SET, NUMBER }

    private final String label;
    private final Kind kind;
    private final Function<T, String> textGetter;
    private final Function<T, List<String>> setGetter;
    private final Function<T, Double> numberGetter;
    private final Function<Double, String> numberFormatter;
    private Function<T, Collection<String>> partialSource;

    private Attribute(String label, Kind kind, Function<T, String> textGetter, Function<T, List<String>> setGetter,
                      Function<T, Double> numberGetter, Function<Double, String> numberFormatter) {
        this.label = label;
        this.kind = kind;
        this.textGetter = textGetter;
        this.setGetter = setGetter;
        this.numberGetter = numberGetter;
        this.numberFormatter = numberFormatter;
    }

    /** Single value; correct only when equal. */
    public static <T> Attribute<T> text(String label, Function<T, String> getter) {
        return new Attribute<>(label, Kind.TEXT, getter, null, null, null);
    }

    /** Several values (e.g. positions); partial when the guess shares at least one with the answer. */
    public static <T> Attribute<T> set(String label, Function<T, List<String>> getter) {
        return new Attribute<>(label, Kind.SET, null, getter, null, null);
    }

    /** Number; when wrong, an arrow says whether the answer is higher or lower. */
    public static <T> Attribute<T> number(String label, Function<T, Double> getter, Function<Double, String> formatter) {
        return new Attribute<>(label, Kind.NUMBER, null, null, getter, formatter);
    }

    /** For text attributes: partial when the guessed value appears in these values of the answer. */
    public Attribute<T> partialIfIn(Function<T, Collection<String>> source) {
        this.partialSource = source;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public String format(T subject) {
        switch (kind) {
            case TEXT: {
                String value = textGetter.apply(subject);
                return value != null ? value : UNKNOWN_VALUE;
            }
            case SET: {
                List<String> values = setGetter.apply(subject);
                return values != null && !values.isEmpty() ? join(values) : UNKNOWN_VALUE;
            }
            default: {
                Double value = numberGetter.apply(subject);
                return value != null ? numberFormatter.apply(value) : UNKNOWN_VALUE;
            }
        }
    }

    public Clue compare(T guess, T target) {
        String shown = format(guess);
        switch (kind) {
            case TEXT: {
                String g = textGetter.apply(guess);
                String t = textGetter.apply(target);
                if (g == null || t == null) {
                    return new Clue(label, shown, Match.UNKNOWN, "");
                }
                if (g.equalsIgnoreCase(t)) {
                    return new Clue(label, shown, Match.CORRECT, "");
                }
                if (partialSource != null && containsIgnoreCase(partialSource.apply(target), g)) {
                    return new Clue(label, shown, Match.PARTIAL, "");
                }
                return new Clue(label, shown, Match.WRONG, "");
            }
            case SET: {
                List<String> g = setGetter.apply(guess);
                List<String> t = setGetter.apply(target);
                if (g == null || t == null || g.isEmpty() || t.isEmpty()) {
                    return new Clue(label, shown, Match.UNKNOWN, "");
                }
                Set<String> guessSet = lower(g);
                Set<String> targetSet = lower(t);
                if (guessSet.equals(targetSet)) {
                    return new Clue(label, shown, Match.CORRECT, "");
                }
                for (String value : guessSet) {
                    if (targetSet.contains(value)) {
                        return new Clue(label, shown, Match.PARTIAL, "");
                    }
                }
                return new Clue(label, shown, Match.WRONG, "");
            }
            default: {
                Double g = numberGetter.apply(guess);
                Double t = numberGetter.apply(target);
                if (g == null || t == null) {
                    return new Clue(label, shown, Match.UNKNOWN, "");
                }
                int cmp = Double.compare(t, g);
                if (cmp == 0) {
                    return new Clue(label, shown, Match.CORRECT, "");
                }
                return new Clue(label, shown, Match.WRONG, cmp > 0 ? Clue.ARROW_HIGHER : Clue.ARROW_LOWER);
            }
        }
    }

    private static boolean containsIgnoreCase(Collection<String> values, String value) {
        if (values == null) {
            return false;
        }
        for (String v : values) {
            if (v != null && v.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    private static Set<String> lower(List<String> values) {
        Set<String> set = new HashSet<>();
        for (String v : values) {
            set.add(v.toLowerCase());
        }
        return set;
    }

    public static String join(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(v);
        }
        return sb.toString();
    }

    /** Splits "Top, Jungle" style strings into a list; returns an empty list for null/blank. */
    public static List<String> split(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<String> out = new ArrayList<>();
        for (String part : csv.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) out.add(trimmed);
        }
        return Collections.unmodifiableList(out);
    }
}
