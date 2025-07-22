package de.safti.skriptclient.api.synatxes.generated;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.Literal;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;

public class GeneratedLiteral<T> implements Literal<T> {

    private final T[] values;
    private final String literalName;

    public GeneratedLiteral(@NotNull T[] values, String literalName) {
        this.values = values;
        this.literalName = literalName;
    }

    @SuppressWarnings("unchecked")
    public GeneratedLiteral(@NotNull T value, String literalName) {
        this.literalName = literalName;
        Class<? extends T> tClass = (Class<? extends T>) value.getClass();
        T[] tArr = (T[]) Array.newInstance(tClass, 1);
        tArr[0] = value;
        values = tArr;
    }


    @Override
    public T[] getValues() {
        return values;
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull ParseContext parseContext) {
        return true;
    }

    @Override
    public boolean isSingle() {
        return values.length == 1;
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return "literal " + literalName;
    }
}
