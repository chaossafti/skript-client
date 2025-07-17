package de.safti.skriptclient.fabric;

import de.safti.skriptclient.bridge.ClientWrapper;
import de.safti.skriptclient.bridge.Core;
import de.safti.skriptclient.bridge.Loader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.CoderResult;

public class FabricCore implements Core {
	@Override
	public @NotNull File getSkriptClientFolder() {
		return null;
	}
	
	@Override
	public void initSkript() {
	
	}
	
	@Override
	public @NotNull String getModVersionString() {
		return "";
	}
	
	@Override
	public @NotNull String getMinecraftVersionString() {
		return "";
	}
	
	@Override
	public @NotNull Loader getLoader() {
		return Loader.FABRIC;
	}
	
	@Override
	public @NotNull ClientWrapper getClient() {
		return null;
	}
}
