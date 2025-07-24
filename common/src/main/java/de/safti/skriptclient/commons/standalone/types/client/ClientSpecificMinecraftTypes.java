package de.safti.skriptclient.commons.standalone.types.client;

import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.SkriptRegistry;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.StatsCounter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClientSpecificMinecraftTypes {

    static {
        SkriptRegistry.registerType(LocalPlayer.class, "localplayer", "[local[-| ]]player")

                // connection (public final — read-only)
                .property("connection listener", ClientPacketListener.class,
                        player -> player.connection)

                // stats (private final — assume getter & no setter)
                .property("stats", StatsCounter.class,
                        LocalPlayer::getStats)

                // recipe book (private final — assume getter & no setter)
                .property("recipe book", ClientRecipeBook.class,
                        LocalPlayer::getRecipeBook)

                // permissionLevel
                .property("permission level", Integer.class, SecurityLevel.STANDARD,
                        localPlayer -> getPrivate("getPermissionLevel", localPlayer),
                        LocalPlayer::setPermissionLevel)


                // crouching, wasShiftKeyDown, wasSprinting
                .property("crouching", Boolean.class,
                        LocalPlayer::isCrouching)
                .property("shift key down", Boolean.class,
                        LocalPlayer::isShiftKeyDown)
                .property("sprinting", Boolean.class,
                        LocalPlayer::isSprinting)

                // input (public non-final — mutable field)
                .property("input", Input.class, SecurityLevel.ENHANCED,
                        player -> player.input,
                        (player, in) -> player.input = in)

                // yBob, xBob, yBobO, xBobO
                .property("y bob", Float.class, SecurityLevel.STANDARD,
                        player -> player.yBob,
                        (player, v) -> player.yBob = v)
                .property("x bob", Float.class, SecurityLevel.STANDARD,
                        player -> player.xBob,
                        (player, v) -> player.xBob = v)
                .property("y bob old", Float.class, SecurityLevel.STANDARD,
                        player -> player.yBobO,
                        (player, v) -> player.yBobO = v)
                .property("x bob old", Float.class, SecurityLevel.STANDARD,
                        player -> player.xBobO,
                        (player, v) -> player.xBobO = v)

                // spinningEffectIntensity / oSpinningEffectIntensity
                .property("spinning intensity", Float.class, SecurityLevel.STANDARD,
                        player -> player.spinningEffectIntensity,
                        (player, v) -> player.spinningEffectIntensity = v)
                .property("old spinning intensity", Float.class, SecurityLevel.STANDARD,
                        player -> player.oSpinningEffectIntensity,
                        (player, v) -> player.oSpinningEffectIntensity = v)

                .property("jump riding scale", Float.class, SecurityLevel.MINIMAL,
                        LocalPlayer::getJumpRidingScale)

                // startedUsingItem / usingItemHand / handsBusy
                .property("hands busy", Boolean.class, SecurityLevel.MINIMAL,
                        LocalPlayer::isHandsBusy)

                // autoJumpEnabled
                .property("auto jump enabled", Boolean.class, SecurityLevel.MINIMAL,
                        LocalPlayer::isAutoJumpEnabled)

                // wasFallFlying / waterVision
                .property("fall flying", Boolean.class,
                        LocalPlayer::isFallFlying)
                .property("water vision", Float.class, SecurityLevel.MINIMAL,
                        LocalPlayer::getWaterVision)

                // showDeathScreen / doLimitedCrafting
                .property("show death screen", Boolean.class, SecurityLevel.STANDARD,
                        LocalPlayer::shouldShowDeathScreen,
                        LocalPlayer::setShowDeathScreen)
                .property("limited crafting", Boolean.class, SecurityLevel.STANDARD,
                        LocalPlayer::getDoLimitedCrafting,
                        LocalPlayer::setDoLimitedCrafting)

                .register();

    }

    // TODO: probably remove this.
    @SuppressWarnings("unchecked")
    public static <T, H> T getPrivate(String methodName, H instance) {
        try {
            Class<H> clazz = (Class<H>) instance.getClass();
            Method declaredMethod = clazz.getDeclaredMethod(methodName);
            declaredMethod.setAccessible(true);
            return (T) declaredMethod.invoke(instance);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
