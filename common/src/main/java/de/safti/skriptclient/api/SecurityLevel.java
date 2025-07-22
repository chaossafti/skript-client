package de.safti.skriptclient.api;

/**
 * The Security level of a Syntax. Essentially, this defined how risky a syntax is to run.
 * In the future, This can stop downloaded scripts from directly accessing anything from your pc.
 */
public enum SecurityLevel {
    /**
     * Minimal Risk. A Syntax requiring this permission level won't be able to hurt, or even interact with you in most ways.
     * This includes most events, logging to console or other minor changes.
     * I highly doubt you can write a script with only these.
     */
    MINIMAL(true),

    /**
     * The default permission. This lets you do a lot more than minimal.
     * A syntax with this can interact with basic parts of the game,
     * like your chat or already existing components like boss bars, the scoreboard, etc.
     */
    STANDARD(true),

    /**
     * A higher than standard permission levels. Syntaxes requiring this permission typically play the game for you.
     * By that, I mean they can simulate key presses or talk to the server directly.
     */
    ENHANCED(true),

    /**
     * The interface permission level lets the mod interact with the screen. Syntaxes with this level usually modify things rendered on your screen.
     * This doesn't include existing minecraft components like boss bars or scoreboards; those fall into the {@link #STANDARD} security level.
     */
    INTERFACE(true),

    /**
     * The Reflective security level is special: Syntaxes with this permission are able to directly access java code.
     * Running java code this way won't have any protection in place. That means, scripts using this can bypass the permission level.
     * They can simply access {@link #UNRESTRICTED} level security without having it granted.
     * Reflective is also required for Mixins.
     * <p>
     * Reflective is "Unsafe" as it can be used to do things you don't intend to do. Only give this permission to sources you trust!
     */
    REFLECTIVE(false),

    /**
     * Unrestricted is very straightforward. It's Unrestricted. It can access every syntax. New syntaxes include complete file system access,
     * java properties.
     * <p>
     * Unrestricted is "Unsafe" as it can be used to do things you don't intend to do. Only give this permission to sources you trust!
     */
    UNRESTRICTED(false);

    public static final String SECURITY_LEVEL_DATA_STRING = "security_level";

    private final boolean safe;

    SecurityLevel(boolean safe) {
        this.safe = safe;
    }

    public boolean isSafe() {
        return safe;
    }

    /**
     * Returns true if the given level has equal or higher privileges than this.
     * I.e. canAccessTo(MINIMAL) is true for anything, but canAccessTo(UNRESTRICTED) is only true for UNRESTRICTED.
     */
    public boolean canBeAccessedBy(SecurityLevel other) {
        return other.ordinal() >= this.ordinal();
    }
}