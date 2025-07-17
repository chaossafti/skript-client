package de.safti.skriptclient.bridge;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface Core {
	
	@NotNull
	File getSkriptClientFolder();
	
	@NotNull
	default File getScriptsFolder() {
		File f = new File(getSkriptClientFolder(), "scripts");
		if(f.exists()) {
			f.mkdirs();
		}
		
		return f;
	}
	
	@NotNull
	void initSkript();
	
	@NotNull
	String getModVersionString();
	
	@NotNull
	String getMinecraftVersionString();
	
	@NotNull
	Loader getLoader();
	
	@NotNull
	ClientWrapper getClient();
	
}
