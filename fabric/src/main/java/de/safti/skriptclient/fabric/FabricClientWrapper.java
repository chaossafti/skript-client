package de.safti.skriptclient.fabric;

import de.safti.skriptclient.bridge.ClientWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import java.time.Duration;

public class FabricClientWrapper implements ClientWrapper {
	static final FabricClientWrapper INSTANCE = new FabricClientWrapper();
	private final Minecraft client = Minecraft.getInstance();
	
	
	private FabricClientWrapper() {
	}
	
	@Override
	public void showErrorToast(String title, String message, Duration duration) {
		// TODO: replace with meaningful error data
		client.getToasts().addToast(new SystemToast(SystemToast.SystemToastId.UNSECURE_SERVER_WARNING, Component.literal(title), Component.literal(message)));
	
	}
}
