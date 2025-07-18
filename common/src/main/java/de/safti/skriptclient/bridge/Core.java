package de.safti.skriptclient.bridge;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface Core {
	
	@NotNull
	Path getSkriptClientFolder();
	
	@NotNull
	default Path getScriptsFolder() {
		Path p = getSkriptClientFolder().resolve("scripts");
		if(!p.toFile().exists()) {
			p.toFile().mkdirs();
		}
		
		return p;
	}
	
	default Path getConfig() {
		Path p = getSkriptClientFolder().resolve("scripts");
		if(!p.toFile().exists()) {
			try {
				// TODO: load default configs once configs are made
				p.toFile().createNewFile();
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		return p;
	}
	
	@NotNull
	String[] getSubPackagesToLoad();
	
	@NotNull
	String getModVersionString();
	
	@NotNull
	String getMinecraftVersionString();
	
	@NotNull
	Loader getLoader();
	
	@NotNull
	ClientWrapper getClient();
	
}
