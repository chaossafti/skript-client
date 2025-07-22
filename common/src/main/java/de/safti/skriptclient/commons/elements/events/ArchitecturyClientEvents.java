package de.safti.skriptclient.commons.elements.events;

import de.safti.skriptclient.api.event.EventBuilder;
import dev.architectury.event.events.client.*;
import dev.architectury.hooks.client.screen.ScreenAccess;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.awt.*;
import java.util.List;

/**
 * This class registers all Client side architectury events.
 */
public class ArchitecturyClientEvents {
    public static final String CHAT_SEND = "chat send";
    public static final String CHAT_RECEIVE = "chat receive";

    public static final String GUI_CONTAINER_SCREEN_RENDER_BACKGROUND = "gui container render background";
    public static final String GUI_CONTAINER_SCREEN_RENDER_FOREGROUND = "gui container render foreground";
    public static final String GUI_DEBUG_TEXT = "gui debug text";
    public static final String GUI_RENDER_HUD = "gui render hud";
    public static final String GUI_SCREEN_INIT_PRE = "gui screen init pre";
    public static final String GUI_SCREEN_INIT_POST = "gui screen init post";
    public static final String GUI_SCREEN_RENDER_PRE = "gui screen render pre";
    public static final String GUI_SCREEN_RENDER_POST = "gui screen render post";
    public static final String GUI_SET_SCREEN = "gui set screen";

    public static final String PLAYER_JOIN = "player client join";
    public static final String PLAYER_QUIT = "player client quit";
    public static final String PLAYER_RESPAWN = "player client respawn";

    public static final String RAWINPUT_KEY = "raw input key pressed";
    public static final String RAWINPUT_MOUSE_CLICK_PRE = "raw input mouse clicked";
    public static final String RAWINPUT_MOUSE_CLICK_POST = "raw input mouse clicked";
    public static final String RAWINPUT_MOUSE_SCROLL = "raw input mouse scrolled";

    public static final String RELOAD_SHADERS = "reload shaders";

    public static final String SCREENINPUT_KEY_PRESSED_PRE = "pre screen key pressed";
    public static final String SCREENINPUT_KEY_PRESSED_POST = "post screen key pressed";
    public static final String SCREENINPUT_KEY_RELEASED_PRE = "pre screen key released";
    public static final String SCREENINPUT_KEY_RELEASED_POST = "post screen key released";
    public static final String SCREENINPUT_KEY_TYPED_PRE = "pre screen key typed";
    public static final String SCREENINPUT_KEY_TYPED_POST = "screen key typed";
    public static final String SCREENINPUT_MOUSE_CLICKED_PRE = "pre screen mouse clicked";
    public static final String SCREENINPUT_MOUSE_CLICKED_POST = "post screen mouse clicked";
    public static final String SCREENINPUT_MOUSE_DRAGGED_PRE = "pre screen mouse dragged";
    public static final String SCREENINPUT_MOUSE_DRAGGED_POST = "post screen mouse dragged";
    public static final String SCREENINPUT_MOUSE_RELEASED_PRE = "pre screen mouse released";
    public static final String SCREENINPUT_MOUSE_RELEASED_POST = "post screen mouse released";
    public static final String SCREENINPUT_MOUSE_SCROLLED_PRE = "pre screen mouse scrolled";
    public static final String SCREENINPUT_MOUSE_SCROLLED_POST = "post screen mouse scrolled";

    public static final String SYSTEM_MESSAGE_RECEIVED = "system message received";

    public static final String TICK_CLIENT_PER = "pre client tick";
    public static final String TICK_CLIENT_POST = "post client tick";
    public static final String TICK_CLIENT_LEVEL_PRE = "pre client level tick";
    public static final String TICK_CLIENT_LEVEL_POST = "post client level tick";

    public static final String TOOLTIP_APPEND_LINES = "tooltip append lines";
    public static final String TOOLTIP_CHANGE_COLOR = "tooltip change color";
    public static final String TOOLTIP_CHANGE_POSITION = "tooltip change position";
    public static final String TOOLTIP_RENDER = "tooltip render";

    static {
        registerAll();
    }

    public static void registerAll() {
        // Chat
        EventBuilder.create(CHAT_SEND)
                .pattern("chat send")
                .eventValue("message", String.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientChatEvent.SEND, ClientChatEvent.Send.class, 0)
                .register();

        EventBuilder.create(CHAT_RECEIVE)
                .pattern("chat receive")
                .eventValue("type", ChatType.Bound.class)
                .eventValue("message", Component.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientChatEvent.RECEIVED, ClientChatEvent.Received.class, 0, 1, 2)
                .register();

        // GUI Subevents
        EventBuilder.create(GUI_CONTAINER_SCREEN_RENDER_BACKGROUND)
                .pattern("gui container render background")
                .eventValue("screen", AbstractContainerScreen.class)
                .eventValue("graphics", GuiGraphics.class)
                .eventValue("mouseX", Integer.class)
                .eventValue("mouseY", Integer.class)
                .eventValue("delta", Float.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.RENDER_CONTAINER_BACKGROUND,
                        ClientGuiEvent.ContainerScreenRenderBackground.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(GUI_CONTAINER_SCREEN_RENDER_FOREGROUND)
                .pattern("gui container render foreground")
                .eventValue("screen", AbstractContainerScreen.class)
                .eventValue("graphics", GuiGraphics.class)
                .eventValue("mouseX", Integer.class)
                .eventValue("mouseY", Integer.class)
                .eventValue("delta", Float.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.RENDER_CONTAINER_FOREGROUND,
                        ClientGuiEvent.ContainerScreenRenderForeground.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(GUI_DEBUG_TEXT)
                .pattern("gui left debug text")
                .eventValue("list", List.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.DEBUG_TEXT_LEFT, ClientGuiEvent.DebugText.class, 0)
                .register();

        EventBuilder.create(GUI_DEBUG_TEXT)
                .pattern("gui right debug text")
                .eventValue("list", List.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.DEBUG_TEXT_RIGHT, ClientGuiEvent.DebugText.class, 0)
                .register();

        EventBuilder.create(GUI_RENDER_HUD)
                .pattern("gui render hud")
                .eventValue("graphics", GuiGraphics.class)
                .eventValue("delta tracker", DeltaTracker.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.RENDER_HUD, ClientGuiEvent.RenderHud.class, 0, 1)
                .register();

        EventBuilder.create(GUI_SCREEN_INIT_PRE)
                .pattern("gui screen init pre")
                .eventValue("screen", Screen.class)
                .eventValue("screen access", ScreenAccess.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.INIT_PRE, ClientGuiEvent.ScreenInitPre.class, 0, 1)
                .register();

        EventBuilder.create(GUI_SCREEN_INIT_POST)
                .pattern("gui screen init post")
                .eventValue("screen", Screen.class)
                .eventValue("screen access", ScreenAccess.class)


                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.INIT_POST, ClientGuiEvent.ScreenInitPost.class, 0, 1)
                .register();

        EventBuilder.create(GUI_SCREEN_RENDER_PRE)
                .pattern("gui screen render pre")
                .eventValue("screen", Screen.class)
                .eventValue("graphics", Graphics.class)
                .eventValue("mouseX", Integer.class)
                .eventValue("mouseY", Integer.class)
                .eventValue("delta tracker", DeltaTracker.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.RENDER_PRE, ClientGuiEvent.ScreenRenderPre.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(GUI_SCREEN_RENDER_POST)
                .pattern("gui screen render post")
                .eventValue("screen", Screen.class)
                .eventValue("graphics", Graphics.class)
                .eventValue("mouseX", Integer.class)
                .eventValue("mouseY", Integer.class)
                .eventValue("delta tracker", DeltaTracker.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.RENDER_POST, ClientGuiEvent.ScreenRenderPost.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(GUI_SET_SCREEN)
                .pattern("gui set screen")
                .eventValue("screen", Screen.class)
                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientGuiEvent.SET_SCREEN, ClientGuiEvent.SetScreen.class, 0)
                .register();

        // Player events
        EventBuilder.create(PLAYER_JOIN)
                .pattern("connect")
                .eventValue("player", LocalPlayer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientPlayerEvent.CLIENT_PLAYER_JOIN, ClientPlayerEvent.ClientPlayerJoin.class, 0)
                .register();

        EventBuilder.create(PLAYER_QUIT)
                .pattern("client player quit")
                .eventValue("player", LocalPlayer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientPlayerEvent.CLIENT_PLAYER_QUIT, ClientPlayerEvent.ClientPlayerQuit.class, 0)
                .register();

        EventBuilder.create(PLAYER_RESPAWN)
                .pattern("client player respawn")
                .eventValue("past player", LocalPlayer.class)
                .eventValue("player", LocalPlayer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientPlayerEvent.CLIENT_PLAYER_RESPAWN, ClientPlayerEvent.ClientPlayerRespawn.class, 0)
                .register();

        // Raw input
        EventBuilder.create(RAWINPUT_KEY)
                .pattern("key pressed")
                .eventValue("client", Minecraft.class)
                .eventValue("keycode", Integer.class)
                .eventValue("scancode", Integer.class)
                .eventValue("action", Integer.class)
                .eventValue("modifiers", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientRawInputEvent.KEY_PRESSED, ClientRawInputEvent.KeyPressed.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(RAWINPUT_MOUSE_CLICK_PRE)
                .pattern("pre mouse clicked")
                .eventValue("client", Minecraft.class)
                .eventValue("button", Integer.class)
                .eventValue("action", Integer.class)
                .eventValue("modifiers", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientRawInputEvent.MOUSE_CLICKED_PRE, ClientRawInputEvent.MouseClicked.class, 0, 1, 2, 3)
                .register();

        EventBuilder.create(RAWINPUT_MOUSE_CLICK_POST)
                .pattern("post mouse clicked")
                .eventValue("client", Minecraft.class)
                .eventValue("button", Integer.class)
                .eventValue("action", Integer.class)
                .eventValue("modifiers", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientRawInputEvent.MOUSE_CLICKED_POST, ClientRawInputEvent.MouseClicked.class, 0, 1, 2, 3)
                .register();

        EventBuilder.create(RAWINPUT_MOUSE_SCROLL)
                .pattern("mouse scrolled")
                .eventValue("client", Minecraft.class)
                .eventValue("amountX", Double.class)
                .eventValue("amountY", Double.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientRawInputEvent.MOUSE_SCROLLED, ClientRawInputEvent.MouseScrolled.class, 0, 1, 2)
                .register();

        // Reload shaders
        EventBuilder.create(RELOAD_SHADERS)
                .pattern("reload shaders")
                .eventValue("resource provider", ResourceProvider.class)
                .eventValue("shader sink", ClientReloadShadersEvent.ShadersSink.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientReloadShadersEvent.EVENT, ClientReloadShadersEvent.class, 0, 1)
                .register();

        // Screen input
        EventBuilder.create(SCREENINPUT_KEY_PRESSED_PRE)
                .pattern("pre onscreen key press")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("keycode", Integer.class)
                .eventValue("scancode", Integer.class)
                .eventValue("modifiers", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.KEY_PRESSED_PRE, ClientScreenInputEvent.KeyPressed.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_KEY_PRESSED_POST)
                .pattern("post onscreen key press")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("keycode", Integer.class)
                .eventValue("scancode", Integer.class)
                .eventValue("modifiers", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.KEY_PRESSED_POST, ClientScreenInputEvent.KeyPressed.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_KEY_RELEASED_PRE)
                .pattern("pre screen key release")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("keycode", Integer.class)
                .eventValue("scancode", Integer.class)
                .eventValue("modifiers", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.KEY_RELEASED_PRE, ClientScreenInputEvent.KeyReleased.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_KEY_RELEASED_POST)
                .pattern("post screen key release")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("keycode", Integer.class)
                .eventValue("scancode", Integer.class)
                .eventValue("modifiers", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.KEY_RELEASED_POST, ClientScreenInputEvent.KeyReleased.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_KEY_TYPED_PRE)
                .pattern("pre screen key typed")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("character", Character.class)
                .eventValue("keycode", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.CHAR_TYPED_PRE, ClientScreenInputEvent.KeyTyped.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_KEY_TYPED_POST)
                .pattern("pre screen key typed")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("character", Character.class)
                .eventValue("keycode", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.CHAR_TYPED_POST, ClientScreenInputEvent.KeyTyped.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_MOUSE_CLICKED_PRE)
                .pattern("pre screen mouse clicked")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("mouseX", Double.class)
                .eventValue("mouseY", Double.class)
                .eventValue("button", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.MOUSE_CLICKED_PRE, ClientScreenInputEvent.MouseClicked.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_MOUSE_CLICKED_POST)
                .pattern("post screen mouse clicked")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("mouseX", Double.class)
                .eventValue("mouseY", Double.class)
                .eventValue("button", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.MOUSE_CLICKED_POST, ClientScreenInputEvent.MouseClicked.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_MOUSE_DRAGGED_PRE)
                .pattern("pre screen mouse dragged")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("startX", Double.class)
                .eventValue("startY", Double.class)
                .eventValue("button", Integer.class)
                .eventValue("endX", Double.class)
                .eventValue("endY", Double.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.MOUSE_DRAGGED_PRE, ClientScreenInputEvent.MouseDragged.class, 0, 1, 2, 3, 4, 5, 6)
                .register();

        EventBuilder.create(SCREENINPUT_MOUSE_DRAGGED_POST)
                .pattern("post  screen mouse dragged")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("startX", Double.class)
                .eventValue("startY", Double.class)
                .eventValue("button", Integer.class)
                .eventValue("endX", Double.class)
                .eventValue("endY", Double.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.MOUSE_DRAGGED_POST, ClientScreenInputEvent.MouseDragged.class, 0, 1, 2, 3, 4, 5, 6)
                .register();

        EventBuilder.create(SCREENINPUT_MOUSE_RELEASED_PRE)
                .pattern("pre screen mouse released")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("mouseX", Double.class)
                .eventValue("mouseY", Double.class)
                .eventValue("button", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.MOUSE_RELEASED_PRE, ClientScreenInputEvent.MouseReleased.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_MOUSE_RELEASED_POST)
                .pattern("pre screen mouse released")
                .eventValue("client", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("mouseX", Double.class)
                .eventValue("mouseY", Double.class)
                .eventValue("button", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.MOUSE_RELEASED_POST, ClientScreenInputEvent.MouseReleased.class, 0, 1, 2, 3, 4)
                .register();

        EventBuilder.create(SCREENINPUT_MOUSE_SCROLLED_PRE)
                .pattern("pre screen mouse scrolled")
                .eventValue("minecraft", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("mouse-x", Double.class)
                .eventValue("mouse-y", Double.class)
                .eventValue("amount-x", Double.class)
                .eventValue("amount-y", Double.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.MOUSE_SCROLLED_PRE, ClientScreenInputEvent.MouseScrolled.class, 0, 1, 2, 3, 4, 5)
                .register();

        EventBuilder.create(SCREENINPUT_MOUSE_SCROLLED_POST)
                .pattern("post screen mouse scrolled")
                .eventValue("minecraft", Minecraft.class)
                .eventValue("screen", Screen.class)
                .eventValue("mouse-x", Double.class)
                .eventValue("mouse-y", Double.class)
                .eventValue("amount-x", Double.class)
                .eventValue("amount-y", Double.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientScreenInputEvent.MOUSE_SCROLLED_POST, ClientScreenInputEvent.MouseScrolled.class, 0, 1, 2, 3, 4, 5)
                .register();

        // System messages
        EventBuilder.create(SYSTEM_MESSAGE_RECEIVED)
                .pattern("system message received")
                .eventValue("component", Component.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientSystemMessageEvent.RECEIVED, ClientSystemMessageEvent.Received.class, 0)
                .register();

        // Tick events
        EventBuilder.create(TICK_CLIENT_PER)
                .pattern("pre client tick")
                .eventValue("client", Minecraft.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientTickEvent.CLIENT_PRE, ClientTickEvent.Client.class, 0)
                .register();

        EventBuilder.create(TICK_CLIENT_POST)
                .pattern("post client tick")
                .eventValue("client", Minecraft.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientTickEvent.CLIENT_POST, ClientTickEvent.Client.class, 0)
                .register();

        EventBuilder.create(TICK_CLIENT_LEVEL_PRE)
                .pattern("pre tick world")
                .eventValue("world", ClientLevel.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientTickEvent.CLIENT_LEVEL_PRE, ClientTickEvent.ClientLevel.class, 0)
                .register();

        EventBuilder.create(TICK_CLIENT_LEVEL_POST)
                .pattern("post tick world")
                .eventValue("world", ClientLevel.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientTickEvent.CLIENT_LEVEL_POST, ClientTickEvent.ClientLevel.class, 0)
                .register();

        // Tooltip events
        EventBuilder.create(TOOLTIP_APPEND_LINES)
                .pattern("tooltip append callback")
                .eventValue("item", ItemStack.class)
                .eventValue("components", List.class)
                .eventValue("tooltip context", Item.TooltipContext.class)
                .eventValue("tooltip flag", TooltipFlag.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientTooltipEvent.ITEM, ClientTooltipEvent.Item.class, 0, 1, 2, 3)
                .register();

        EventBuilder.create(TOOLTIP_RENDER)
                .pattern("pre tooltip render")
                .eventValue("graphics", GuiGraphics.class)
                .eventValue("tooltip list", List.class)
                .eventValue("x", Integer.class)
                .eventValue("y", Integer.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientTooltipEvent.RENDER_PRE, ClientTooltipEvent.Render.class, 0, 1, 2, 3)
                .register();

        EventBuilder.create(TOOLTIP_CHANGE_POSITION)
                .pattern("tooltip position change callback")
                .eventValue("graphics", GuiGraphics.class)
                .eventValue("position context", ClientTooltipEvent.PositionContext.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientTooltipEvent.RENDER_MODIFY_POSITION, ClientTooltipEvent.RenderModifyPosition.class, 0, 1)
                .register();

        EventBuilder.create(TOOLTIP_CHANGE_COLOR)
                .pattern("tooltip color change callback")
                .eventValue("graphics", GuiGraphics.class)
                .eventValue("x", Integer.class)
                .eventValue("y", Integer.class)
                .eventValue("color context", ClientTooltipEvent.ColorContext.class)

                .enterWrappedRegistrationStage()
                .wrappedRedirector(ClientTooltipEvent.RENDER_MODIFY_COLOR, ClientTooltipEvent.RenderModifyColor.class, 0, 1, 2, 3)
                .register();
    }

}
