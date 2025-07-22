package de.safti.skriptclient.api.synatxes.generated;

import de.safti.skriptclient.api.synatxes.expression.property.PropertyChanger;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyGetter;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyMutator;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Optional;

public class GeneratedPropertyExpression<T, H> implements Expression<T> {
    private final String propertyName;
    private final String holderName;
    private final Class<T> tClass;

    private final PropertyGetter<T, H> propertyGetter;
    private final PropertyChanger<T, H> propertySetter;
    private final PropertyChanger<T, H> propertyAdder;
    private final PropertyChanger<T, H> propertyRemover;

    private final PropertyMutator<H> propertyDeleter;
    private final PropertyMutator<H> propertyResetter;
    private final PropertyMutator<H> propertyAllRemover;

    private Expression<H> holderExpr;

    public GeneratedPropertyExpression(
            String propertyName,
            String holderName,
            Class<T> tClass,
            PropertyGetter<T, H> propertyGetter,
            @Nullable PropertyChanger<T, H> propertySetter,
            @Nullable PropertyChanger<T, H> propertyAdder,
            @Nullable PropertyChanger<T, H> propertyRemover,
            @Nullable PropertyMutator<H> propertyDeleter,
            @Nullable PropertyMutator<H> propertyResetter,
            @Nullable PropertyMutator<H> propertyAllRemover
    ) {
        this.propertyName = propertyName;
        this.holderName = holderName;
        this.tClass = tClass;
        this.propertyGetter = propertyGetter;
        this.propertySetter = propertySetter;
        this.propertyAdder = propertyAdder;
        this.propertyRemover = propertyRemover;
        this.propertyDeleter = propertyDeleter;
        this.propertyResetter = propertyResetter;
        this.propertyAllRemover = propertyAllRemover;
    }

    public GeneratedPropertyExpression(String propertyName, String baseName, Class<T> returnType, PropertyGetter<T, H> propertyGetter, PropertyChanger<T, H> propertySetter) {
        this(propertyName, baseName, returnType, propertyGetter, propertySetter, null, null, null, null, null);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        //noinspection unchecked
        holderExpr = ((Expression<H>) expressions[0]);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] getValues(@NotNull TriggerContext triggerContext) {
        Optional<H> holderOpt = (Optional<H>) holderExpr.getSingle(triggerContext);
        if (holderOpt.isEmpty()) {
            return null;
        }

        H holder = holderOpt.get();

        if (isSingle()) {
            T[] tArr = (T[]) Array.newInstance(tClass, 1);
            tArr[0] = propertyGetter.getSingle(triggerContext, holder);
            return tArr;
        }

        return propertyGetter.getArray(triggerContext, holder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        return switch (mode) {
            case SET -> propertySetter != null ? Optional.of(new Class[]{tClass}) : Optional.empty();
            case ADD -> propertyAdder != null ? Optional.of(new Class[]{tClass}) : Optional.empty();
            case REMOVE -> propertyRemover != null ? Optional.of(new Class[]{tClass}) : Optional.empty();
            case DELETE -> propertyDeleter != null ? Optional.of(new Class[0]) : Optional.empty();
            case RESET -> propertyResetter != null ? Optional.of(new Class[0]) : Optional.empty();
            case REMOVE_ALL -> propertyAllRemover != null ? Optional.of(new Class[0]) : Optional.empty();
        };
    }

    @SuppressWarnings("unchecked")
    @Override
    public void change(@NotNull TriggerContext context, @NotNull ChangeMode mode, Object @NotNull [] delta) {
        Optional<H> holderOpt = (Optional<H>) holderExpr.getSingle(context);
        if (holderOpt.isEmpty()) return;

        H holder = holderOpt.get();

        switch (mode) {
            case SET -> {
                if (propertySetter == null) throw new UnsupportedOperationException();
                propertySetter.changeBy(context, holder, (T[]) delta);
            }
            case ADD -> {
                if (propertyAdder == null) throw new UnsupportedOperationException();
                propertyAdder.changeBy(context, holder, (T[]) delta);
            }
            case REMOVE -> {
                if (propertyRemover == null) throw new UnsupportedOperationException();
                propertyRemover.changeBy(context, holder, (T[]) delta);
            }
            case DELETE -> {
                if (propertyDeleter == null) throw new UnsupportedOperationException();
                propertyDeleter.mutate(context, holder);
            }
            case RESET -> {
                if (propertyResetter == null) throw new UnsupportedOperationException();
                propertyResetter.mutate(context, holder);
            }
            case REMOVE_ALL -> {
                if (propertyAllRemover == null) throw new UnsupportedOperationException();
                propertyAllRemover.mutate(context, holder);
            }
            default -> throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean isSingle() {
        return propertyGetter.isSingle();
    }

    @Override
    public String toString(@Nullable TriggerContext context, boolean debug) {
        return propertyName + " of " + holderName;
    }
}
