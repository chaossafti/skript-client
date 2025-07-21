package de.safti.skriptclient.fabric.elements.effects;

import de.safti.skriptclient.api.SkriptRegistry;
import de.safti.skriptclient.commons.elements.effects.EffShowToast;
import de.safti.skriptclient.logging.runtime.RuntimeLogger;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class EffShowToastFabric extends EffShowToast {
	
	static {
		SkriptRegistry.registerEffect(EffShowToastFabric.class, PATTERN);
	}
	
	
	@Override
	public boolean validate(int matchedPattern, @NotNull ParseContext parseContext) {
		return true;
	}
	
	@Override
	protected void execute(@NotNull TriggerContext triggerContext, RuntimeLogger logger) {
		Minecraft client = Minecraft.getInstance();
		
		Component titleComponent = Component.literal(titleArgument.resolveOrGo(triggerContext));
		Component messageComponent = Component.literal(messageArgument.resolveOrGo(triggerContext));
		
		Toast toast = new SystemToast(SystemToast.SystemToastId.NARRATOR_TOGGLE, titleComponent, messageComponent);
		client.getToasts().addToast(toast);
		
	}
	
	@Override
	public String toString(@NotNull TriggerContext triggerContext, boolean b) {
		return "show toast to the client";
	}
}
