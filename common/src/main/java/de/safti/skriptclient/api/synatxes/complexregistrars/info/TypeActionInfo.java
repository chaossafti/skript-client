package de.safti.skriptclient.api.synatxes.complexregistrars.info;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.synatxes.generated.GeneratedAction;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @param actionPattern The pattern to register this action with.
 * @param securityLevel The security level of this action.
 * @param valueConsumer The value consumer, essentially the heart of the action.
 * @param <T> The type to consume
 */
public record TypeActionInfo<T>(String actionPattern, String friendlyName, SecurityLevel securityLevel, Consumer<T> valueConsumer, Class<T> clazz) {

    public void register() {
        SkriptClient.INSTANCE.getRegistry()
                .newEffect(GeneratedAction.class, actionPattern)
                .setSupplier(() -> new GeneratedAction<>(friendlyName, valueConsumer))
                .addData(SecurityLevel.SECURITY_LEVEL_DATA_STRING, securityLevel)
                .register();
    }
}
