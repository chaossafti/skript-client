package de.safti.skriptclient.api;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.pattern.Pattern;

public class SkriptRegistry {
	
	public static void registerEffect(Class<? extends AbstractEffect> clazz, Pattern pattern) {
		SkriptClient.INSTANCE
				.getRegistry()
				.addEffect(clazz, pattern.pattern());
		
	}
	
}
