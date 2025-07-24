package de.safti.skriptclient.commons.standalone.types.common;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import de.safti.skriptclient.api.SkriptRegistry;

import java.util.UUID;

public class MojangTypes {

    static {

        SkriptRegistry.registerType(GameProfile.class, "gameprofile", "game[ ]profile")
                .property("uuid", UUID.class,
                        GameProfile::getId)
                .property("name", String.class,
                        GameProfile::getName)
                .property("properties", PropertyMap.class,
                        GameProfile::getProperties)
                .register();

    }

}
