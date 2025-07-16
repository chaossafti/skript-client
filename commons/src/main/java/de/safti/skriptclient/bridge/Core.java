package de.safti.skriptclient.bridge;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface Core {
	File getSkriptClientFolder();
	
	default File getScriptsFolder() {
		File f = new File(getSkriptClientFolder(), "scripts");
		if(f.exists()) {
			f.mkdirs();
		}
		
		return f;
	}
	
	void initSkript();
	
	String getModVersionString();
	
	String getMinecraftVersionString();
	
	Loader getLoader();
	
	@NotNull
	ClientWrapper getClient();
	
}
