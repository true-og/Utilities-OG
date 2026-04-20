// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.utils;

import java.util.EnumSet;
import java.util.Set;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;

// Modular MiniMessage tag supporter via TextUtils.
public final class MessageFormat {

    // Enumerated set of applicable MiniMessage type families.
    public enum Tag {

        COLOR, DECORATIONS, RAINBOW, GRADIENT, RESET;

    }

    // Preset: every supported tag enabled. Equivalent semantic to pre Utilities-OG
    // 1.7 behavior.
    public static final MessageFormat FULL = new MessageFormat(EnumSet.allOf(Tag.class));

    // Preset: every supported tag disabled.
    public static final MessageFormat PLAIN = new MessageFormat(EnumSet.noneOf(Tag.class));

    private final EnumSet<Tag> enabled;

    private MessageFormat(EnumSet<Tag> enabled) {

        this.enabled = enabled;

    }

    // Start from the all-enabled preset.
    public static MessageFormat full() {

        return FULL;

    }

    // Start from the none-enabled preset.
    public static MessageFormat none() {

        return PLAIN;

    }

    // Enable color tags (<red>, <#ff0000>, <color:red>).
    public MessageFormat withColor() {

        return with(Tag.COLOR);

    }

    // Disable color tags.
    public MessageFormat withoutColor() {

        return without(Tag.COLOR);

    }

    // Enable decoration tags (<bold>, <italic>, <underlined>, <strikethrough>,
    // <obfuscated>, <!bold>, etc).
    public MessageFormat withDecorations() {

        return with(Tag.DECORATIONS);

    }

    // Disable decoration tags.
    public MessageFormat withoutDecorations() {

        return without(Tag.DECORATIONS);

    }

    // Enable rainbow tag (<rainbow>, <rainbow:phase>).
    public MessageFormat withRainbow() {

        return with(Tag.RAINBOW);

    }

    // Disable rainbow tag.
    public MessageFormat withoutRainbow() {

        return without(Tag.RAINBOW);

    }

    // Enable gradient tag (<gradient:color1:color2:...>).
    public MessageFormat withGradient() {

        return with(Tag.GRADIENT);

    }

    // Disable gradient tag.
    public MessageFormat withoutGradient() {

        return without(Tag.GRADIENT);

    }

    // Enable reset tag (<reset>).
    public MessageFormat withReset() {

        return with(Tag.RESET);

    }

    // Disable reset tag.
    public MessageFormat withoutReset() {

        return without(Tag.RESET);

    }

    // True when the given tag family is currently enabled.
    public boolean has(Tag tag) {

        return enabled.contains(tag);

    }

    // Snapshot of currently enabled tag families.
    public Set<Tag> enabled() {

        return EnumSet.copyOf(enabled);

    }

    private MessageFormat with(Tag tag) {

        if (enabled.contains(tag)) {

            return this;

        }

        final EnumSet<Tag> next = EnumSet.copyOf(enabled);
        next.add(tag);

        return new MessageFormat(next);

    }

    private MessageFormat without(Tag tag) {

        if (!enabled.contains(tag)) {

            return this;

        }

        final EnumSet<Tag> next = EnumSet.copyOf(enabled);
        next.remove(tag);

        return new MessageFormat(next);

    }

    // Consumed by TextUtils, not part of public API.
    TagResolver buildResolver() {

        final TagResolver.Builder builder = TagResolver.builder();

        if (enabled.contains(Tag.COLOR)) {

            builder.resolver(StandardTags.color());

        }

        if (enabled.contains(Tag.DECORATIONS)) {

            builder.resolver(StandardTags.decorations());

        }

        if (enabled.contains(Tag.RAINBOW)) {

            builder.resolver(StandardTags.rainbow());

        }

        if (enabled.contains(Tag.GRADIENT)) {

            builder.resolver(StandardTags.gradient());

        }

        if (enabled.contains(Tag.RESET)) {

            builder.resolver(StandardTags.reset());

        }

        return builder.build();

    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {

            return true;

        }

        if (!(other instanceof MessageFormat that)) {

            return false;

        }

        return enabled.equals(that.enabled);

    }

    @Override
    public int hashCode() {

        return enabled.hashCode();

    }

    @Override
    public String toString() {

        return "MessageFormat" + enabled;

    }

}