package de.safti.skriptclient.api.synatxes.complexregistrars.info;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyChanger;
import de.safti.skriptclient.api.synatxes.generated.GeneratedPropertyExpression;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyGetter;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class TypePropertyInfo<T, H> {
    private static final Logger log = LoggerFactory.getLogger(TypePropertyInfo.class);
    private final String propertyName;
    private final Class<T> typeClass;
    private final SecurityLevel securityLevel;
    private final PropertyGetter<T, H> propertyGetter;
    private final PropertyChanger<T, H> propertySetter;

    public TypePropertyInfo(String propertyName, Class<T> typeClass, SecurityLevel securityLevel,
                            PropertyGetter<T, H> propertyGetter, PropertyChanger<T, H> propertySetter) {
        this.propertyName = propertyName;
        this.typeClass = typeClass;
        this.securityLevel = securityLevel;
        this.propertyGetter = propertyGetter;
        this.propertySetter = propertySetter;
    }

    public String resolveTypeBaseName() {
        Optional<String> baseNameOpt = TypeManager.getByClass(typeClass).map(Type::getBaseName);
        if(baseNameOpt.isPresent()) {
            return baseNameOpt.get();
        }

        log.warn("Could not find a base type name for class: {}", typeClass);
        log.warn("Defaulting to: {}", typeClass.getSimpleName());
        return typeClass.getSimpleName();
    }

    public String[] pattern() {

        return new String[]{
                "[the] %s of [the] %s".formatted(propertyName, "%" + resolveTypeBaseName() + "%"),
                "%s's %s".formatted("%" + resolveTypeBaseName() + "%", propertyName)
        };
    }

    public void register() {
        Class<T> returnType = typeClass;
        boolean isSingle = propertyGetter.isSingle();
        @SuppressWarnings("unchecked")
        Class<GeneratedPropertyExpression<T, H>> syntaxClass = (Class<GeneratedPropertyExpression<T, H>>) (Class<?>) GeneratedPropertyExpression.class;

        SkriptClient.INSTANCE.getRegistry().newExpression(syntaxClass, returnType, isSingle, pattern())
                .setSupplier(() -> new GeneratedPropertyExpression<>(propertyName, resolveTypeBaseName(), returnType, propertyGetter, propertySetter))
                .addData("security", securityLevel)
                .register();
    }

    public String propertyName() {
        return propertyName;
    }

    public SecurityLevel securityLevel() {
        return securityLevel;
    }

    public PropertyGetter<T, H> propertyGetter() {
        return propertyGetter;
    }

    public PropertyChanger<T, H> propertySetter() {
        return propertySetter;
    }


}
