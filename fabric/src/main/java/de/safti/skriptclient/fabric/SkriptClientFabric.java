package de.safti.skriptclient.fabric;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.SkriptParserBootstrap;
import net.fabricmc.api.ModInitializer;

import static de.safti.skriptclient.SkriptParserBootstrap.registerSyntaxPackage;

public final class SkriptClientFabric implements ModInitializer {
    private static final FabricCore CORE = new FabricCore();
    
    @Override
    public void onInitialize() {
        registerSyntaxPackage(SkriptClientFabric.class, "de.safti.skriptclient.fabric.elements");
        SkriptClient.initTests(CORE);
    }
}
