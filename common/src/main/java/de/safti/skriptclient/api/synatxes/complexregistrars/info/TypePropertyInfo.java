package de.safti.skriptclient.api.synatxes.complexregistrars.info;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyChanger;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyMutator;
import de.safti.skriptclient.api.synatxes.expression.property.RegistrableProperty;
import de.safti.skriptclient.api.synatxes.generated.GeneratedPropertyExpression;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyGetter;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public final class TypePropertyInfo<T, H> implements RegistrableProperty<T, H> {
    private static final Logger log = LoggerFactory.getLogger(TypePropertyInfo.class);




    private final String propertyName;
    private final Class<T> returnClass;
    private final Class<H> holderClass;
    private final SecurityLevel securityLevel;
    private final PropertyGetter<T, H> propertyGetter;
    private final PropertyChanger<T, H> propertySetter;



    public TypePropertyInfo(String propertyName, Class<T> returnTypeClass, Class<H> holderClass, SecurityLevel securityLevel,
                            PropertyGetter<T, H> propertyGetter, PropertyChanger<T, H> propertySetter) {
        this.propertyName = propertyName;
        this.returnClass = returnTypeClass;
        this.holderClass = holderClass;
        this.securityLevel = securityLevel;
        this.propertyGetter = propertyGetter;
        this.propertySetter = propertySetter;
    }

    public String resolveHolderBaseName() {
        Optional<String> baseNameOpt = TypeManager.getByClass(holderClass).map(Type::getBaseName);
        if(baseNameOpt.isPresent()) {
            return baseNameOpt.get();
        }

        log.warn("Could not find a base Type name for class: {}", holderClass);
        log.warn("Defaulting to: {}", holderClass.getSimpleName());
        return holderClass.getSimpleName();
    }

    public String[] pattern() {

        // fixme: This pattern won't ever match a Type in the case the Type isn't defined
        return new String[]{
                "[the] %s of [the] %s".formatted(propertyName, "%" + resolveHolderBaseName() + "%"),
                "%s's %s".formatted("%" + resolveHolderBaseName() + "%", propertyName)
        };
    }

    public void registerSelf() {
        Class<T> returnType = returnClass;
        boolean isSingle = propertyGetter.isSingle();
        @SuppressWarnings("unchecked")
        Class<GeneratedPropertyExpression<T, H>> syntaxClass = (Class<GeneratedPropertyExpression<T, H>>) (Class<?>) GeneratedPropertyExpression.class;

        SkriptClient.INSTANCE.getRegistry().newExpression(syntaxClass, returnType, isSingle, pattern())
                .setSupplier(() -> new GeneratedPropertyExpression<>(propertyName, resolveHolderBaseName(), returnType, propertyGetter, propertySetter))
                .addData("security", securityLevel)
                .register();
    }

    @Override
    public @NotNull Class<T> returnClass() {
        return returnClass;
    }

    public @NotNull String propertyName() {
        return propertyName;
    }

    @Override

    public @NotNull SecurityLevel securityLevel() {
        return securityLevel;
    }

    @Override
    public boolean isSingle() {
        return getter().isSingle();
    }

    @Override
    public @NotNull Class<H> holderClass() {
        return holderClass;
    }

    @Override
    public @NotNull PropertyGetter<T, H> getter() {
        return propertyGetter;
    }

    @Override
    public PropertyChanger<T, H> setter() {
        return propertySetter;
    }

    @Override
    public PropertyChanger<T, H> adder() {
        return null;
    }

    @Override
    public PropertyChanger<T, H> remover() {
        return null;
    }

    @Override
    public PropertyMutator<H> resetter() {
        return null;
    }

    @Override
    public PropertyMutator<H> allRemover() {
        return null;
    }

    @Override
    public PropertyMutator<H> deleter() {
        return null;
    }


}
