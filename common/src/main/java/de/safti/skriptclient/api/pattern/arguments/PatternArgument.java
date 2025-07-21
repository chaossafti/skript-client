package de.safti.skriptclient.api.pattern.arguments;

import de.safti.skriptclient.api.exceptions.SyntaxRuntimeException;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.types.Type;

import java.util.Optional;

public interface PatternArgument<T> {

    Type<T> getType();

    boolean isPlural();

    default String getPatternArgumentString() {
        return "%" +
                getType().withIndefiniteArticle(isPlural())
                + "%";
    }

    Optional<? extends T> resolveSingle(TriggerContext context);

    default T resolveOrGo(TriggerContext context) {
        Optional<? extends T> optT = resolveSingle(context);
        return optT.orElseThrow(() -> SyntaxRuntimeException.IRRELEVANT);
    }

    T[] resolveAll(TriggerContext context);

    String toString(TriggerContext triggerContext, boolean b);

}
