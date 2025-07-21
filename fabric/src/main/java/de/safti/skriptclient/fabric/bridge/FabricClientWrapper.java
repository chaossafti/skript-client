package de.safti.skriptclient.fabric.bridge;

import de.safti.skriptclient.bridge.ClientWrapper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Queue;

public class FabricClientWrapper implements ClientWrapper {
	static final FabricClientWrapper INSTANCE = new FabricClientWrapper();
	private final Minecraft client = Minecraft.getInstance();
	private final Queue<Runnable> startupRunnableQueue = new ArrayDeque<>();
	
	
	private FabricClientWrapper() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
			while (!startupRunnableQueue.isEmpty()) {
				Runnable r = startupRunnableQueue.poll();
				r.run();
			}
			
		});
	
	}
	
	@Override
	public void showErrorToast(String title, String message, Duration duration) {
		// TODO: replace with meaningful error data
		runOnStartup(() ->
				client.getToasts().addToast(new SystemToast(SystemToast.SystemToastId.UNSECURE_SERVER_WARNING, Component.literal(title), Component.literal(message))));
		
	}
	
	@Override
	public void runOnStartup(Runnable runnable) {
		if(client.isRunning()) {
			runnable.run();
			return;
		}
		startupRunnableQueue.add(runnable);
	}
}
