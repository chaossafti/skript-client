package de.safti.skriptclient.fabric.bridge;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.bridge.ClientWrapper;
import de.safti.skriptclient.bridge.Core;
import de.safti.skriptclient.bridge.Loader;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class FabricCore implements Core {
	private final ModMetadata metadata;
	
	
	public FabricCore() {
		this.metadata = FabricLoader.getInstance()
				.getModContainer(SkriptClient.MOD_ID)
				.orElseThrow()
				.getMetadata();
	}
	
	@Override
	public @NotNull Path getSkriptClientFolder() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve(SkriptClient.MOD_ID);
		if(!path.toFile().exists()) {
			path.toFile().mkdirs();
		}
		
		return path;
	}
	
	@Override
	public @NotNull String[] getSubPackagesToLoad() {
		// the skript parser adds expressions, effects etc. packages by default; we don't have to add duplicates.
		return new String[] {};
	}
	
	@Override
	public @NotNull String getModVersionString() {
		return metadata.getVersion().getFriendlyString();
	}
	
	@Override
	public @NotNull String getMinecraftVersionString() {
		return Minecraft.getInstance().getLaunchedVersion();
	}
	
	@Override
	public @NotNull Loader getLoader() {
		return Loader.FABRIC;
	}
	
	@Override
	public @NotNull ClientWrapper getClient() {
		return FabricClientWrapper.INSTANCE;
	}
}
