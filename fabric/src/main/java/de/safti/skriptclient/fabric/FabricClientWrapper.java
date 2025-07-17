package de.safti.skriptclient.fabric;

import de.safti.skriptclient.bridge.ClientWrapper;
import net.minecraft.client.Minecraft;

import java.time.Duration;

public class FabricClientWrapper implements ClientWrapper {
	static final FabricClientWrapper INSTANCE = new FabricClientWrapper();
	private final Minecraft client = Minecraft.getInstance();
	
	
	private FabricClientWrapper() {
	}
	
	@Override
	public void showErrorToast(String message, Duration duration) {
	
	}
}
