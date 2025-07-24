package de.safti.skriptclient.api.synatxes.complexregistrars;

import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.synatxes.EnumLiteral;
import de.safti.skriptclient.api.synatxes.builders.PropertyBuilder;
import de.safti.skriptclient.api.synatxes.complexregistrars.info.TypeActionInfo;
import de.safti.skriptclient.api.synatxes.complexregistrars.info.TypePropertyInfo;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyChanger;
import de.safti.skriptclient.api.synatxes.expression.property.PropertyGetter;
import de.safti.skriptclient.api.synatxes.expression.property.RegistrableProperty;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ComplexTypeRegistrar<T> extends SkriptRegistration.TypeRegistrar<T> {
    /**
     * Queue to register ComplexTypeRegistrars.
     * This is required because their properties or actions might require other types.
     */
    private static final Deque<ComplexTypeRegistrar<?>> COMPLEX_REGISTRATION_QUEUE = new ArrayDeque<>();

    @ApiStatus.Internal
    public static void drainRegistrationQueue() {
        while(!COMPLEX_REGISTRATION_QUEUE.isEmpty()) {
            COMPLEX_REGISTRATION_QUEUE.pop().complexRegister();
        }
    }


    private final Set<TypePropertyInfo<?, ?>> properties = new HashSet<>();
    private final Set<TypeActionInfo<T>> actions = new HashSet<>();
    private final Class<T> clazz;
    @Nullable
    private final String friendlyEnumName;

    public ComplexTypeRegistrar(SkriptRegistration skriptRegistration, Class<T> c, String baseName, String pattern) {
        this(skriptRegistration, c, baseName, pattern, null);
    }

    public ComplexTypeRegistrar(SkriptRegistration skriptRegistration, Class<T> c, String baseName, String pattern, @Nullable String friendlyEnumName) {
        skriptRegistration.super(c, baseName, pattern);
        clazz = c;
        this.friendlyEnumName = friendlyEnumName;

        if(friendlyEnumName != null && !c.isEnum()) {
            throw new IllegalArgumentException("class is not an enum, but an enum name was provided! Class: " + c);
        }
    }

    /*
     * ===========================
     *  PROPERTY REGISTRATION API
     * ===========================
     */

    /**
     * Starts a new property builder for advanced configuration.
     */
    public <PT> PropertyBuilder<PT, T> propertyBuilder(Class<PT> ptClass, String propertyName) {
        return new PropertyBuilder<>(ptClass, clazz, propertyName, this);
    }

    /*
     * ===============================
     *  SINGLE PROPERTY REGISTRATIONS
     * ===============================
     */

    /**
     * Registers a single read-only property with a getter.
     */
    public <PT> ComplexTypeRegistrar<T> property(String propertyName, Class<PT> valueClass, Function<T, PT> getter) {
        return property(propertyName, valueClass, SecurityLevel.STANDARD, PropertyGetter.createSingle(getter));
    }

    /**
     * Registers a single read-only property with a security level and getter.
     */
    public <PT> ComplexTypeRegistrar<T> property(String propertyName, Class<PT> valueClass, SecurityLevel level, Function<T, PT> getter) {
        return property(propertyName, valueClass, level, PropertyGetter.createSingle(getter));
    }

    /**
     * Registers a single read-only property with a full PropertyGetter.
     */
    public <PT> ComplexTypeRegistrar<T> property(String propertyName, Class<PT> valueClass, PropertyGetter<PT, T> getter) {
        return property(propertyName, valueClass, SecurityLevel.STANDARD, getter);
    }

    /**
     * Registers a single read-only property with a full PropertyGetter and a security level.
     */
    public <PT> ComplexTypeRegistrar<T> property(String propertyName, Class<PT> valueClass, SecurityLevel level, PropertyGetter<PT, T> getter) {
        return property(propertyName, valueClass, level, getter, null);
    }

    /**
     * Registers a single read-only property with a full PropertyGetter and a security level.
     */
    public <PT> ComplexTypeRegistrar<T> property(String propertyName, Class<PT> valueClass, SecurityLevel level, Function<T, PT> getter, @Nullable BiConsumer<T, PT> propertySetter) {
        return property(propertyName, valueClass, level, PropertyGetter.createSingle(getter), propertySetter);
    }

    /**
     * Registers a single read-only property with a full PropertyGetter and a security level.
     */
    private <PT> ComplexTypeRegistrar<T> property(String propertyName, Class<PT> valueClass, SecurityLevel level, PropertyGetter<PT, T> getter, @Nullable BiConsumer<T, PT> propertySetter) {
        TypePropertyInfo<PT, T> info = new TypePropertyInfo<>(propertyName, valueClass, clazz, level, getter, PropertyChanger.createSingle(propertySetter));
        properties.add(info);
        return this;
    }

    /*
     * ===============================
     *  PLURAL PROPERTY REGISTRATIONS
     * ===============================
     */

    /**
     * Registers a plural read-only property with a getter.
     */
    public <PT> ComplexTypeRegistrar<T> pluralProperty(String propertyName, Class<PT> valueClass, Function<T, PT[]> getter) {
        return property(propertyName, valueClass, SecurityLevel.STANDARD, PropertyGetter.createPlural(getter));
    }

    /**
     * Registers a plural read-only property with a security level and getter.
     */
    public <PT> ComplexTypeRegistrar<T> pluralProperty(String propertyName, Class<PT> valueClass, SecurityLevel level, Function<T, PT[]> getter) {
        return property(propertyName, valueClass, level, PropertyGetter.createPlural(getter));
    }


    /*
     * ACTIONS
     */

    /**
     * @param effectPattern The pattern for this action. The first instance of {} will be replaced with the type base name.
     * @param friendlyName The friendly name used for debugging.
     * @param consumer The consumer to run when this action is executed.
     * @return this
     */
    public ComplexTypeRegistrar<T> action(String effectPattern, String friendlyName, Consumer<T> consumer) {
        return action(effectPattern, friendlyName, SecurityLevel.STANDARD, consumer);
    }

    /**
     * @param effectPattern The pattern for this action. The first instance of {} will be replaced with the type base name.
     * @param friendlyName The friendly name used for debugging.
     * @param securityLevel The security risk for running this action.
     * @param consumer The consumer to run when this action is executed.
     * @return this
     */
    public ComplexTypeRegistrar<T> action(String effectPattern, String friendlyName, SecurityLevel securityLevel, Consumer<T> consumer) {
        TypeActionInfo<T> typeActionInfo = new TypeActionInfo<>(effectPattern.replaceFirst("\\{}", super.baseName), friendlyName, securityLevel, consumer, clazz);
        actions.add(typeActionInfo);
        return this;
    }

    private void queueRegister() {
        COMPLEX_REGISTRATION_QUEUE.add(this);
        properties.forEach(RegistrableProperty::queue);
    }

    private void complexRegister() {
        // register all actions
        actions.forEach(TypeActionInfo::register);

        // register every enum field as a property expression, if available
        if(clazz.isAssignableFrom(Enum.class) && friendlyEnumName != null) {
            registerEnumLiterals();
        }
    }

    @Override
    public void register() {
        queueRegister();
        super.register();
    }

    private <E extends Enum<E>> void registerEnumLiterals() {
        // is checked; is only called inside an if statement checking this.
        @SuppressWarnings("unchecked")
        Class<E> eClass = (Class<E>) clazz;

        EnumLiteral.registerAll(eClass, friendlyEnumName);
    }

}
