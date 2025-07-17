package de.safti.skriptclient.neoforge;

import de.safti.skriptclient.Skriptclient;
import net.neoforged.fml.common.Mod;

@Mod(Skriptclient.MOD_ID)
public final class SkriptclientNeoForge {
    public SkriptclientNeoForge() {
        // Run our common setup.
        Skriptclient.init();
    }
}
