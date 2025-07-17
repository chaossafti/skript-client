package de.safti.skriptclient.fabric;

import de.safti.skriptclient.SkriptClient;
import net.fabricmc.api.ModInitializer;

public final class SkriptclientFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SkriptClient.init();
    }
}
