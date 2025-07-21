package de.safti.skriptclient;

import de.safti.skriptclient.bridge.Core;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.lang.event.SkriptEventManager;
import io.github.syst3ms.skriptparser.registration.SkriptAddon;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import org.jetbrains.annotations.NotNull;

public class SkriptClient extends SkriptAddon {
	public static final String MOD_ID = "skript-client";
	
	public static boolean IS_INITIALIZED = false;
	public static Core CORE;
	public static SkriptClient INSTANCE;
	public static boolean IS_TEST_ENV;

	private final SkriptRegistration registry = new SkriptRegistration(this);
	private final SkriptEventManager eventManager = SkriptEventManager.GLOBAL_EVENT_MANAGER;
	
	
	@SuppressWarnings("LoggingSimilarMessage")
	public static void init(Core core) {
		if(IS_INITIALIZED)
			throw new IllegalStateException("SkriptClient already initialized!");

		INSTANCE = new SkriptClient();
		
		SkriptClient.CORE = core;
        // load the skript parser
		// this includes loading scripts
		SkriptParserBootstrap.initSkript(core);
	}

	public static void initTests(Core core) {
		IS_TEST_ENV = true;
		init(core);
	}

	private SkriptClient() {

	}

	public SkriptRegistration getRegistry() {
		return registry;
	}

	public SkriptEventManager getEventManager() {
		return eventManager;
	}

	@Override
	public void handleTrigger(@NotNull Trigger trigger) {
		// I don't get it. what is this supposed to do?
		// triggers themselves have an init method for this.

	}
}
