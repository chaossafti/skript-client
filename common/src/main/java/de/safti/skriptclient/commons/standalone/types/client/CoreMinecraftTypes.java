package de.safti.skriptclient.commons.standalone.types.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.DataFixer;
import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.SkriptRegistry;
import net.minecraft.client.*;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.*;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.validation.DirectoryValidator;
import net.minecraft.world.phys.HitResult;

import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;

public class CoreMinecraftTypes {

    static {

        /*
         * MINECRAFT CLIENT
         */
        //<editor-fold desc="Minecraft client type">
        SkriptRegistry.registerType(Minecraft.class, "minecraft", "minecraft client")

                .property("resource pack directory", Path.class, SecurityLevel.UNRESTRICTED,
                        Minecraft::getResourcePackDirectory)

                .property("profile future", GameProfile.class, SecurityLevel.ENHANCED,
                        Minecraft::getGameProfile)

                .property("texture manager", TextureManager.class, SecurityLevel.ENHANCED,
                        Minecraft::getTextureManager)

                .property("data fixer [upper]", DataFixer.class,
                        Minecraft::getFixerUpper)

                .property("main window", Window.class, SecurityLevel.INTERFACE,
                        Minecraft::getWindow)

                .property("delta timer", DeltaTracker.class,
                        Minecraft::getTimer)

                .property("(world|level) renderer", LevelRenderer.class, SecurityLevel.INTERFACE,
                        client -> client.levelRenderer)

                .property("entity renderer", EntityRenderDispatcher.class, SecurityLevel.INTERFACE,
                        Minecraft::getEntityRenderDispatcher)

                .property("item renderer", ItemRenderer.class, SecurityLevel.INTERFACE,
                        Minecraft::getItemRenderer)

                .property("particle engine", ParticleEngine.class, SecurityLevel.INTERFACE,
                        client -> client.particleEngine)

                .property("user", User.class, SecurityLevel.UNRESTRICTED,
                        Minecraft::getUser)

                .property("font", Font.class,
                        client -> client.font)

                .property("filtered font", Font.class,
                        client -> client.fontFilterFishy)

                .property("game renderer", GameRenderer.class, SecurityLevel.INTERFACE,
                        client -> client.gameRenderer)

                .property("debug renderer", DebugRenderer.class, SecurityLevel.INTERFACE,
                        client -> client.debugRenderer)

                .property("progress listener", StoringChunkProgressListener.class,
                        Minecraft::getProgressListener)

                .property("(hud|gui)", Gui.class, SecurityLevel.INTERFACE,
                        client -> client.gui)

                .property("options", Options.class, SecurityLevel.ENHANCED,
                        client -> client.options)

                .property("hotbar manager", HotbarManager.class, SecurityLevel.ENHANCED,
                        Minecraft::getHotbarManager)

                .property("mouse handler", MouseHandler.class, SecurityLevel.ENHANCED,
                        client -> client.mouseHandler)

                .property("keyboard handler", KeyboardHandler.class, SecurityLevel.ENHANCED,
                        client -> client.keyboardHandler)

                .property("last input type", InputType.class, SecurityLevel.MINIMAL,
                        Minecraft::getLastInputType,
                        Minecraft::setLastInputType)

                .property("game directory", File.class, SecurityLevel.UNRESTRICTED,
                        client -> client.gameDirectory)

                .property("launched version", String.class, SecurityLevel.MINIMAL,
                        Minecraft::getLaunchedVersion)

                .property("version type", String.class, SecurityLevel.MINIMAL,
                        Minecraft::getVersionType)

                .property("proxy", Proxy.class, SecurityLevel.ENHANCED,
                        Minecraft::getProxy)

                .property("level source", LevelStorageSource.class,
                        Minecraft::getLevelSource)

                .property("demo mode", Boolean.class, SecurityLevel.MINIMAL,
                        Minecraft::isDemo)

                .property("allows multiplayer", Boolean.class, SecurityLevel.MINIMAL,
                        Minecraft::allowsMultiplayer)

                .property("resource manager", ResourceManager.class, SecurityLevel.INTERFACE,
                        Minecraft::getResourceManager)

                .property("vanilla pack resources", VanillaPackResources.class, SecurityLevel.ENHANCED,
                        Minecraft::getVanillaPackResources)

                .property("downloaded pack source", DownloadedPackSource.class, SecurityLevel.ENHANCED,
                        Minecraft::getDownloadedPackSource)

                .property("resource pack repository", PackRepository.class, SecurityLevel.ENHANCED,
                        Minecraft::getResourcePackRepository)

                .property("language manager", LanguageManager.class, SecurityLevel.ENHANCED,
                        Minecraft::getLanguageManager)

                .property("block colors", BlockColors.class,
                        Minecraft::getBlockColors)

                .property("main render target", RenderTarget.class,
                        Minecraft::getMainRenderTarget)

                .property("sound manager", SoundManager.class, SecurityLevel.ENHANCED,
                        Minecraft::getSoundManager)

                .property("music manager", MusicManager.class, SecurityLevel.ENHANCED,
                        Minecraft::getMusicManager)

                .property("splash manager", SplashManager.class, SecurityLevel.ENHANCED,
                        Minecraft::getSplashManager)

                .property("gpu warnlist", GpuWarnlistManager.class, SecurityLevel.ENHANCED,
                        Minecraft::getGpuWarnlistManager)

                .property("session service", MinecraftSessionService.class,  SecurityLevel.UNRESTRICTED,
                        Minecraft::getMinecraftSessionService)

                .property("skin manager", SkinManager.class,  SecurityLevel.ENHANCED,
                        Minecraft::getSkinManager)

                .property("model manager", ModelManager.class, SecurityLevel.INTERFACE,
                        Minecraft::getModelManager)

                .property("block renderer", BlockRenderDispatcher.class, SecurityLevel.INTERFACE,
                        Minecraft::getBlockRenderer)

                .property("painting textures", PaintingTextureManager.class, SecurityLevel.INTERFACE,
                        Minecraft::getPaintingTextures)

                .property("effect textures", MobEffectTextureManager.class, SecurityLevel.INTERFACE,
                        Minecraft::getMobEffectTextures)

                .property("map decoration textures", MapDecorationTextureManager.class, SecurityLevel.INTERFACE,
                        Minecraft::getMapDecorationTextures)

                .property("gui sprites", GuiSpriteManager.class, SecurityLevel.INTERFACE,
                        Minecraft::getGuiSprites)

                .property("toast component", ToastComponent.class,  SecurityLevel.INTERFACE,
                        Minecraft::getToasts)

                .property("tutorial", Tutorial.class,
                        Minecraft::getTutorial)

                .property("player social manager", PlayerSocialManager.class, SecurityLevel.ENHANCED,
                        Minecraft::getPlayerSocialManager)

                .property("entity model set", EntityModelSet.class, SecurityLevel.INTERFACE,
                        Minecraft::getEntityModels)

                .property("block entity renderer", BlockEntityRenderDispatcher.class, SecurityLevel.INTERFACE,
                        Minecraft::getBlockEntityRenderDispatcher)

                .property("telemetry manager", ClientTelemetryManager.class, SecurityLevel.UNRESTRICTED,
                        Minecraft::getTelemetryManager)

                .property("profile key pair manager", ProfileKeyPairManager.class, SecurityLevel.UNRESTRICTED,
                        Minecraft::getProfileKeyPairManager)

                .property("game mode", MultiPlayerGameMode.class,
                        client -> client.gameMode)

                .property("client level", ClientLevel.class,
                        client -> client.level)

                .property("local player", LocalPlayer.class,
                        client -> client.player)

                .property("singleplayer server", IntegratedServer.class,
                        Minecraft::getSingleplayerServer)

                .property("pending connection", ClientPacketListener.class, SecurityLevel.ENHANCED,
                        Minecraft::getConnection)

                .property("camera entity", Entity.class,SecurityLevel.MINIMAL,
                        client -> client.cameraEntity)

                .property("crosshair entity", Entity.class,SecurityLevel.MINIMAL,
                        client -> client.crosshairPickEntity)

                .property("hit result", HitResult.class,SecurityLevel.MINIMAL,
                        client -> client.hitResult)

                .property("pause state", Boolean.class, SecurityLevel.MINIMAL,
                        Minecraft::isPaused)

                .property("fps string", String.class,SecurityLevel.MINIMAL,
                        client -> client.fpsString)

                .property("wireframe", Boolean.class,SecurityLevel.MINIMAL,
                        client -> client.wireframe)

                .property("section path", Boolean.class,SecurityLevel.MINIMAL,
                        client -> client.sectionPath)

                .property("section visibility", Boolean.class,SecurityLevel.MINIMAL,
                        client -> client.sectionVisibility)

                .property("smart cull", Boolean.class,SecurityLevel.MINIMAL,
                        client -> client.smartCull)

                .property("main screen", Screen.class,SecurityLevel.INTERFACE,
                        client -> client.screen)

                .property("overlay", Overlay.class,SecurityLevel.INTERFACE,
                        Minecraft::getOverlay)

                .property("profiler", ProfilerFiller.class,
                        Minecraft::getProfiler)

                .property("gpu utilization", Double.class,
                        Minecraft::getGpuUtilization)

                .property("narrator", GameNarrator.class, SecurityLevel.ENHANCED,
                        Minecraft::getNarrator)

                .property("chat listener", ChatListener.class,
                        Minecraft::getChatListener)

                .property("reporting context", ReportingContext.class, SecurityLevel.ENHANCED,
                        Minecraft::getReportingContext)

                .property("command history", CommandHistory.class, SecurityLevel.ENHANCED,
                        Minecraft::commandHistory)

                .property("directory validator", DirectoryValidator.class, SecurityLevel.ENHANCED,
                        Minecraft::directoryValidator)

                .register();
        //</editor-fold>



    }

}
