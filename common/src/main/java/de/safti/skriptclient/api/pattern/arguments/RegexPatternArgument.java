package de.safti.skriptclient.api.pattern.arguments;

import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.NoSuchElementException;
import java.util.Optional;

public class RegexPatternArgument implements PatternArgument<String> {
    @SuppressWarnings("unchecked")
    private static final Type<String> TYPE = (Type<String>) TypeManager.getByClass(String.class).orElseThrow(() -> new NoSuchElementException("Could not get String type!"));
    private final String name;
    @UnknownNullability
    private final ParseContext data;
    private final int index;

    public RegexPatternArgument(String name, ParseContext data, int index) {
        this.name = name;
        this.data = data;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    @Override
    public Type<String> getType() {
        return TYPE;
    }

    @Override
    public boolean isPlural() {
        return true;
    }

    @Override
    public Optional<? extends String> resolveSingle(TriggerContext context) {
        if(data.getMatches().size() < index) {
            return Optional.empty();
        }

        return Optional.ofNullable(data.getMatches().get(index).group());
    }

    @Override
    public String[] resolveAll(TriggerContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString(TriggerContext triggerContext, boolean b) {
        return "regex pattern";
    }
}
