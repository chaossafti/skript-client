package de.safti.skriptclient.commons.elements.converters;

import de.safti.skriptclient.SkriptClient;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Optional;

public class MinecraftConverters {

    static {
        SkriptClient.INSTANCE.getRegistry()
                .addConverter(String.class, MutableComponent.class, s -> Optional.of(Component.literal(s)));

        SkriptClient.INSTANCE.getRegistry()
                .addConverter(String.class, Component.class, s -> Optional.of(Component.literal(s)));
    }

}
