package de.safti.skriptclient.api.synatxes.generated;

import de.safti.skriptclient.api.synatxes.expression.property.PropertyChanger;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyMutator;
import de.safti.skriptclient.api.synatxes.expression.property.RegistrableProperty;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import io.github.syst3ms.skriptparser.util.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;


// Please ignore any unfunny jokes you see.
// TODO: remove jokes and also test this.
public class MergedGeneratedPropertyExpression<T> implements Expression<T> {
    private final List<RegistrableProperty<T, ?>> registrableProperties;
    private final String propertyName;
    private final String[] patterns;
    private final Map<String, RegistrableProperty<T, ?>> pattern2property;

    private Expression<?> holderExpression;
    private RegistrableProperty<T, ?> registrableProperty;


    public MergedGeneratedPropertyExpression(List<RegistrableProperty<T, ?>> registrableProperties, String propertyName, String[] patterns) {
        this.registrableProperties = registrableProperties;
        this.propertyName = propertyName;
        this.patterns = patterns;

        // generate the pattern2property map
        pattern2property = new HashMap<>();
        for (int i = 0, patternsLength = patterns.length; i < patternsLength; i++) {
            String pattern = patterns[i];
            int index = Math.floorDiv(i, 2);
            pattern2property.put(pattern, registrableProperties.get(index));
        }
    }


    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPatternIndex, @NotNull ParseContext parseContext) {
        // I guess there's always a first time for everything
        // even not having to suppress an unchecked cast warning here
        this.holderExpression = expressions[0];
        String matchedPatternString = patterns[matchedPatternIndex];
        this.registrableProperty = pattern2property.get(matchedPatternString);

        return true;
    }

    @Override
    public boolean isSingle() {
        // all of these properties will either be single or plural
        return registrableProperties.getFirst().isSingle();
    }

    @Override
    public T[] getValues(@NotNull TriggerContext triggerContext) {
        if(isSingle()) {
            @SuppressWarnings("unchecked")
            T[] arr = (T[]) Array.newInstance(registrableProperty.holderClass(), 1);
            arr[0] = resolveSingle(triggerContext);
            return arr;
        }

        return resolvePlural(triggerContext);
    }

    // well, we escaped it for a while.
    @SuppressWarnings("unchecked")
    private <H> T resolveSingle(@NotNull TriggerContext context) {
        Optional<H> holderOpt = getHolder(context);
        if(holderOpt.isEmpty()) return null;


        RegistrableProperty<T, H> registrablePropertyH = (RegistrableProperty<T, H>) registrableProperty;

        return registrablePropertyH.getter().getSingle(context, holderOpt.get());

    }
    // dang, we even had to suppress twice for it.
    // it's grown stronger.
    @SuppressWarnings("unchecked")
    private <H> T[] resolvePlural(@NotNull TriggerContext context) {
        Optional<H> holderOpt = getHolder(context);
        if(holderOpt.isEmpty()) return null;


        RegistrableProperty<T, H> registrablePropertyH = (RegistrableProperty<T, H>) registrableProperty;

        return registrablePropertyH.getter().getArray(context, holderOpt.get());

    }

    // and a third time even!
    @SuppressWarnings("unchecked")
    private <H> Optional<H> getHolder(TriggerContext context) {
        Expression<H> holderExpressionH = (Expression<H>) holderExpression;
        // properties only support single holders
        return (Optional<H>) holderExpressionH.getSingle(context);
    }


    @Override
    public Optional<Class<?>[]> acceptsChange(@NotNull ChangeMode mode) {
        boolean isSupported = switch (mode) {
            case SET -> registrableProperty.setter() != null;
            case ADD -> registrableProperty.adder() != null;
            case REMOVE -> registrableProperty.remover() != null;
            case DELETE -> registrableProperty.deleter() != null;
            case RESET -> registrableProperty.resetter() != null;
            case REMOVE_ALL -> registrableProperty.allRemover() != null;
        };

        if(isSupported) {
            return Optional.of(new Class<?>[] {getReturnType()});
        }

        return Optional.empty();
    }

    @Override
    public void change(@NotNull TriggerContext context, @NotNull ChangeMode changeMode, Object @NotNull [] delta) {
        switch (changeMode) {
            case SET -> changeBy(context, registrableProperty.setter(), delta);
            case ADD -> changeBy(context, registrableProperty.adder(), delta);
            case REMOVE -> changeBy(context, registrableProperty.remover(), delta);
            case DELETE -> mutate(context, registrableProperty.deleter());
            case RESET -> mutate(context, registrableProperty.resetter());
            case REMOVE_ALL -> mutate(context, registrableProperty.allRemover());
        }
    }

    // I'm out of jokes.
    @SuppressWarnings("unchecked")
    public <H> void changeBy(TriggerContext context, @Nullable PropertyChanger<T, H> changer, Object[] delta) {
        Optional<H> holderOpt = getHolder(context);
        if(holderOpt.isEmpty()) return;

        if(changer == null) {
            throw new UnsupportedOperationException();
        }

        changer.changeBy(context,holderOpt.get(), (T[]) delta);
    }

    public <H> void mutate(TriggerContext context, @Nullable PropertyMutator<H> changer) {
        Optional<H> holderOpt = getHolder(context);
        if(holderOpt.isEmpty()) return;

        if(changer == null) {
            throw new UnsupportedOperationException();
        }

        changer.mutate(context,holderOpt.get());
    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        Type<?> type = TypeManager.getByClass(registrableProperty.holderClass()).orElseThrow();
        return propertyName + " of " + type.getBaseName();
    }

    public static <T> String[] generatePatterns(List<RegistrableProperty<T, ?>> registrableProperties) {
        String lastPropertyName = "";

        Set<Type<?>> validHolderTypes = new HashSet<>();

        // collect all holder types into validHolderTypes set
        // and also check that all registrable properties have the same property name
        for (RegistrableProperty<T, ?> registrableProperty : registrableProperties) {
            // check - make sure every registrable property has the same property name
            if(!lastPropertyName.isEmpty() && !lastPropertyName.equals(registrableProperty.propertyName())) {
                throw new IllegalArgumentException("Not all patterns of provided properties are the same! Patterns: " +
                        registrableProperties.stream()
                                .map(RegistrableProperty::propertyName)
                                .collect(Collectors.toSet())
                );
            }
            lastPropertyName = registrableProperty.propertyName();

            Type<?> type = TypeManager.getByClass(registrableProperty.holderClass()).orElseThrow();
            validHolderTypes.add(type);
        }


        // create a pair of patterns for every holder type
        MultiMap<Type<?>, String> holderPatternPairs = new MultiMap<>();
        for (Type<?> type : validHolderTypes) {
            String surroundedBaseName = "%" + type.getBaseName() + "%";

            String[] patterns = new String[] {
                    "[the] %s of [the] %s".formatted(lastPropertyName, surroundedBaseName),
                    "%s' %s'".formatted(surroundedBaseName, lastPropertyName)
            };


            holderPatternPairs.put(type, Arrays.asList(patterns));
        }

        return holderPatternPairs.getAllValues().toArray(String[]::new);
    }

}
