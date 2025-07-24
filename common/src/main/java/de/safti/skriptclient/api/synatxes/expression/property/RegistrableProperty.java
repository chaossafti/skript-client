package de.safti.skriptclient.api.synatxes.expression.property;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.synatxes.generated.MergedGeneratedPropertyExpression;
import io.github.syst3ms.skriptparser.util.MultiMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;


public interface RegistrableProperty<T, H> {
    @ApiStatus.Internal
    MultiMap<PropertyDescription, RegistrableProperty<?, ?>> PROPERTY_REGISTRATION_QUEUE = new MultiMap<>();


    @ApiStatus.Internal
    static void queue(RegistrableProperty<?, ?> property) {
        PropertyDescription description = new PropertyDescription(property.holderClass(), property.propertyName());
        PROPERTY_REGISTRATION_QUEUE.putOne(description, property);
    }

    record PropertyDescription(Class<?> holderClass, String propertyName) {

    }


    static void drainRegistrationQueue() {
        for (Map.Entry<PropertyDescription, List<RegistrableProperty<?, ?>>> entry : PROPERTY_REGISTRATION_QUEUE.entrySet()) {
            List<RegistrableProperty<?, ?>> properties = entry.getValue();

            // cannot invoke without the usage of these warns
            //noinspection unchecked,rawtypes
            finishPropertyRegistration((List) properties);

        }
    }


    @ApiStatus.Internal
    static <T> void finishPropertyRegistration(List<RegistrableProperty<T, ?>> properties) {
        Class<T> returnClass = properties.getFirst().returnClass();
        if(properties.size() == 1) {
            // simply register the property
            // there is no colliding patterns

            properties.getFirst().registerSelf();
            return;
        }

        // we found multiple colliding patterns
        // merge them into a MergedGeneratedPropertyExpression
        String[] patterns = MergedGeneratedPropertyExpression.generatePatterns(properties);

        @SuppressWarnings("unchecked")
        Class<MergedGeneratedPropertyExpression<T>> exprClass = (Class<MergedGeneratedPropertyExpression<T>>) (Class<?>) MergedGeneratedPropertyExpression.class;

        // find the highest security level
        // it will be used to register the expression
        properties.sort(Comparator.comparing(RegistrableProperty::securityLevel));
        SecurityLevel securityLevel = properties.getFirst().securityLevel();


        SkriptClient.INSTANCE.getRegistry()
                .newExpression(exprClass, returnClass, properties.getFirst().isSingle(), patterns)
                .setSupplier(() -> new MergedGeneratedPropertyExpression<>(properties, properties.getFirst().propertyName(), patterns))
                .addData(SecurityLevel.SECURITY_LEVEL_DATA_STRING, securityLevel)
                .register();


    }

    @NotNull
    Class<T> returnClass();

    @NotNull
    String propertyName();

    @NotNull
    Class<H> holderClass();

    @NotNull
    SecurityLevel securityLevel();

    boolean isSingle();


    @NotNull
    PropertyGetter<T, H> getter();


    @Nullable
    PropertyChanger<T, H> setter();

    @Nullable
    PropertyChanger<T, H> adder();

    @Nullable
    PropertyChanger<T, H> remover();


    @Nullable
    PropertyMutator<H> resetter();

    @Nullable
    PropertyMutator<H> allRemover();

    @Nullable
    PropertyMutator<H> deleter();

    void registerSelf();
}
