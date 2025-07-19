package de.safti.skriptclient.api;

import de.safti.skriptclient.api.pattern.Pattern;
import io.github.syst3ms.skriptparser.Parser;

public class SkriptRegistry {
	
	public static void registerEffect(Class<? extends AbstractEffect> clazz, Pattern pattern) {
		Parser.getMainRegistration()
				.addEffect(clazz, pattern.pattern());
		
	}
	
}
