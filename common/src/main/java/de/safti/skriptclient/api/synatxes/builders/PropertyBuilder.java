package de.safti.skriptclient.api.synatxes.builders;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.synatxes.complexregistrars.ComplexTypeRegistrar;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyChanger;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyGetter;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyMutator;
import de.safti.skriptclient.api.synatxes.expression.property.RegistrableProperty;
import de.safti.skriptclient.api.synatxes.generated.GeneratedPropertyExpression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PropertyBuilder<T, H> implements RegistrableProperty<T, H> {
    private final Class<T> returnClass;
    private final Class<H> holderClass;
    private final String propertyName;

    // <editor-fold desc="Getter (REQUIRED)">
    private PropertyGetter<T, H> propertyGetter;
    // </editor-fold>

    // <editor-fold desc="Changers (optional)">
    private PropertyChanger<T, H> propertySetter;
    private PropertyChanger<T, H> propertyRemover;
    private PropertyChanger<T, H> propertyAdder;
    // </editor-fold>

    // <editor-fold desc="Mutators (optional)">
    private PropertyMutator<H> propertyDeleter;
    private PropertyMutator<H> propertyResetter;
    private PropertyMutator<H> propertyAllRemover;
    // </editor-fold>


    private SecurityLevel securityLevel = SecurityLevel.STANDARD;
    private final @UnknownNullability ComplexTypeRegistrar<H> parent;

    public PropertyBuilder(Class<T> typeClass, Class<H> holderClass, String propertyName, @UnknownNullability ComplexTypeRegistrar<H> parent) {
        this.returnClass = typeClass;
        this.holderClass = holderClass;
        this.propertyName = propertyName;
        this.parent = parent;
    }

    // <editor-fold desc="Getter (REQUIRED)">
    public PropertyBuilder<T, H> getter(PropertyGetter<T, H> getter) {
        this.propertyGetter = getter;
        return this;
    }

    public PropertyBuilder<T, H> getter(Function<H, T> function) {
        return getter(PropertyGetter.createSingle(function));
    }

    public PropertyBuilder<T, H> getterCollection(Function<H, Collection<T>> function) {
        return getterPlural((h) -> {
            Collection<T> collection = function.apply(h);
            //noinspection unchecked
            return collection.toArray(l -> (T[]) Array.newInstance(returnClass, l));
        });
    }

    public PropertyBuilder<T, H> getterPlural(Function<H, T[]> function) {
        return getter(PropertyGetter.createPlural(function));
    }
    // </editor-fold>

    // <editor-fold desc="Changers (optional)">
    public PropertyBuilder<T, H> setter(PropertyChanger<T, H> setter) {
        this.propertySetter = setter;
        return this;
    }

    public PropertyBuilder<T, H> setter(BiConsumer<H, T> function) {
        return setter(PropertyChanger.createSingle(function));
    }

    public PropertyBuilder<T, H> setter(TriConsumer<TriggerContext, H, T> function) {
        return setter(PropertyChanger.createSingle(function));
    }

    public PropertyBuilder<T, H> setterPlural(BiConsumer<H, T> function) {
        return setter(PropertyChanger.createPlural(function));
    }

    public PropertyBuilder<T, H> setterPlural(TriConsumer<TriggerContext, H, T[]> function) {
        return setter(PropertyChanger.createPlural(function));
    }

    public PropertyBuilder<T, H> remover(PropertyChanger<T, H> remover) {
        this.propertyRemover = remover;
        return this;
    }

    public PropertyBuilder<T, H> remover(BiConsumer<H, T> function) {
        return remover(PropertyChanger.createSingle(function));
    }

    public PropertyBuilder<T, H> remover(TriConsumer<TriggerContext, H, T> function) {
        return remover(PropertyChanger.createSingle(function));
    }

    public PropertyBuilder<T, H> removerPlural(BiConsumer<H, T> function) {
        return remover(PropertyChanger.createPlural(function));
    }

    public PropertyBuilder<T, H> removerPlural(TriConsumer<TriggerContext, H, T[]> function) {
        return remover(PropertyChanger.createPlural(function));
    }

    public PropertyBuilder<T, H> adder(PropertyChanger<T, H> adder) {
        this.propertyAdder = adder;
        return this;
    }

    public PropertyBuilder<T, H> adder(BiConsumer<H, T> function) {
        return adder(PropertyChanger.createSingle(function));
    }

    public PropertyBuilder<T, H> adder(TriConsumer<TriggerContext, H, T> function) {
        return adder(PropertyChanger.createSingle(function));
    }

    public PropertyBuilder<T, H> adderPlural(BiConsumer<H, T> function) {
        return adder(PropertyChanger.createPlural(function));
    }

    public PropertyBuilder<T, H> adderPlural(TriConsumer<TriggerContext, H, T[]> function) {
        return adder(PropertyChanger.createPlural(function));
    }
    // </editor-fold>

    // <editor-fold desc="Mutators (optional)">
    public PropertyBuilder<T, H> deleter(PropertyMutator<H> deleter) {
        this.propertyDeleter = deleter;
        return this;
    }

    public PropertyBuilder<T, H> deleter(Consumer<H> function) {
        return deleter(PropertyMutator.createSingle(function));
    }

    public PropertyBuilder<T, H> deleter(BiConsumer<TriggerContext, H> function) {
        return deleter(PropertyMutator.createSingle(function));
    }

    public PropertyBuilder<T, H> resetter(PropertyMutator<H> resetter) {
        this.propertyResetter = resetter;
        return this;
    }

    public PropertyBuilder<T, H> resetter(Consumer<H> function) {
        return resetter(PropertyMutator.createSingle(function));
    }

    public PropertyBuilder<T, H> resetter(BiConsumer<TriggerContext, H> function) {
        return resetter(PropertyMutator.createSingle(function));
    }

    public PropertyBuilder<T, H> allRemover(PropertyMutator<H> allRemover) {
        this.propertyAllRemover = allRemover;
        return this;
    }

    public PropertyBuilder<T, H> allRemover(Consumer<H> function) {
        return allRemover(PropertyMutator.createPlural(function));
    }

    public PropertyBuilder<T, H> allRemover(BiConsumer<TriggerContext, H> function) {
        return allRemover(PropertyMutator.createPlural(function));
    }
    // </editor-fold>

    public PropertyBuilder<T, H> security(@NotNull SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
        return this;
    }

    private String wrap(String str) {
        return "%" + str + "%";
    }

    @UnknownNullability
    public ComplexTypeRegistrar<H> queueRegistration() {
        RegistrableProperty.queue(this);
        return parent;
    }


    @Override
    public void registerSelf() {
        if (propertyGetter == null) {
            throw new IllegalStateException("Property getter is required but not set.");
        }

        Type<T> holderType = (Type<T>) TypeManager.getByClass(holderClass).orElseThrow();
        String[] patterns = new String[] {
                "[the] %s of [the] %s".formatted(propertyName, wrap(holderType.getBaseName())),
                "%s' %s".formatted(wrap(holderType.getBaseName()), propertyName)
        };

        Supplier<GeneratedPropertyExpression<T, H>> generatedPropertySupplier = () -> new GeneratedPropertyExpression<>(
                propertyName,
                holderType.getBaseName(),
                returnClass,
                propertyGetter,
                propertySetter,
                propertyAdder,
                propertyRemover,
                propertyDeleter,
                propertyResetter,
                propertyAllRemover
        );

        boolean isSingle = propertyGetter.isSingle();
        @SuppressWarnings("unchecked")
        Class<GeneratedPropertyExpression<T, H>> syntaxClass = (Class<GeneratedPropertyExpression<T, H>>) (Class<?>) GeneratedPropertyExpression.class;

        SkriptClient.INSTANCE.getRegistry().newExpression(syntaxClass, returnClass, isSingle, patterns)
                .setSupplier(generatedPropertySupplier)
                .addData("security", securityLevel)
                .register();
    }

    @Override
    public @NotNull Class<T> returnClass() {
        return returnClass;
    }

    @Override
    public @NotNull String propertyName() {
        return propertyName;
    }

    @Override
    public @NotNull Class<H> holderClass() {
        return holderClass;
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
    public @NotNull PropertyGetter<T, H> getter() {
        return propertyGetter;
    }

    @Override
    public @Nullable PropertyChanger<T, H> setter() {
        return propertySetter;
    }

    @Override
    public @Nullable PropertyChanger<T, H> adder() {
        return propertyAdder;
    }

    @Override
    public @Nullable PropertyChanger<T, H> remover() {
        return propertyRemover;
    }

    @Override
    public @Nullable PropertyMutator<H> resetter() {
        return propertyResetter;
    }

    @Override
    public @Nullable PropertyMutator<H> allRemover() {
        return propertyAllRemover;
    }

    @Override
    public @Nullable PropertyMutator<H> deleter() {
        return propertyDeleter;
    }
}