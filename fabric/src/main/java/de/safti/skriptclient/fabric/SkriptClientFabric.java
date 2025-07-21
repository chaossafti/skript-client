package de.safti.skriptclient.fabric;

import de.safti.skriptclient.SkriptClient;
import net.fabricmc.api.ModInitializer;

public final class SkriptClientFabric implements ModInitializer {
    private static final FabricCore CORE = new FabricCore();
    
    @Override
    public void onInitialize() {
        SkriptClient.initTests(CORE);
    }
}
