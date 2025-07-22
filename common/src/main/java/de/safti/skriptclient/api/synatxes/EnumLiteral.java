package de.safti.skriptclient.api.synatxes;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.synatxes.generated.GeneratedLiteral;

public record EnumLiteral<E extends Enum<E>>(Class<E> enumClass, E field, String friendlyEnumName) {

    public static <E extends Enum<E>> void registerAll(Class<E> enumClass, String friendlyEnumName) {

        E[] enumConstants = enumClass.getEnumConstants();
        for (E enumConstant : enumConstants) {
            new EnumLiteral<>(enumClass, enumConstant, friendlyEnumName)
                    .register();
        }

    }

    public void register() {
        @SuppressWarnings("unchecked")
        Class<GeneratedLiteral<E>> clazz = (Class<GeneratedLiteral<E>>) (Class<?>) GeneratedLiteral.class;

        String literalName = field.name().replace("_", "[_| ]");

        SkriptClient.INSTANCE.getRegistry()
                .newExpression(clazz, enumClass, true, "[" + friendlyEnumName + "] [enum] " + literalName)
                .setSupplier(() -> new GeneratedLiteral<E>(field, literalName))
                .register();

        // [friendly enum name] [enum] name with_optional underscores

    }

}
