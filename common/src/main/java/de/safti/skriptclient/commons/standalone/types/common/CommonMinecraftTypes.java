package de.safti.skriptclient.commons.standalone.types.common;

import com.mojang.authlib.GameProfile;
import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.SkriptRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.VecDeltaCodec;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;

import java.util.Set;
import java.util.UUID;

public class CommonMinecraftTypes {

    static {

        SkriptRegistry.registerType(Player.class, "player", "[commons[-| ]]player")
                .property("[personal[-| ]]inventory", InventoryMenu.class,
                        player -> player.inventoryMenu)
                .property("[e]xp[experience] level", Integer.class,
                        player -> player.experienceLevel)
                .property("[total] [e]xp[experience]", Integer.class,
                        player -> player.totalExperience)
                .property("game profile", GameProfile.class,
                        Player::getGameProfile)
                .property("current[ly] [active] inventory[container]", AbstractContainerMenu.class,
                        player -> player.containerMenu)
                .property("block interaction range", Double.class,
                        Player::blockInteractionRange)
                .property("scoreboard", Scoreboard.class,
                        Player::getScoreboard)
                .property("display name", Component.class,
                        Player::getDisplayName)
                .property("[clean|basic] name", String.class,
                        Player::getScoreboardName)
                .property("name component", Component.class,
                        Player::getName)
                .property("ender chest [container]", PlayerEnderChestContainer.class,
                        Player::getEnderChestInventory)
                .property("absorption [amount|[half ]hearts]", Float.class,
                        Player::getAbsorptionAmount)
                .property("get item cooldowns", ItemCooldowns.class,
                        Player::getCooldowns)
                .register();



        //<editor-fold desc="LivingEntity">
        SkriptRegistry.registerType(LivingEntity.class, "livingentity", "[living[ ]]entity")

                // attributes
                .property("attributes", AttributeMap.class,
                        LivingEntity::getAttributes)

                // combat tracker
                .property("combat tracker", CombatTracker.class,
                        LivingEntity::getCombatTracker)

                // active effects
                .pluralProperty("active effects", MobEffectInstance.class,
                        livingEntity -> livingEntity.getActiveEffects().toArray(MobEffectInstance[]::new))

                // swinging, swingingArm, swingTime
                .property("swinging arm", InteractionHand.class, SecurityLevel.STANDARD,
                        entity -> entity.swingingArm,
                        (entity, hand) -> entity.swingingArm = hand)

                // swingTime
                .property("swing time", int.class, SecurityLevel.STANDARD,
                        entity -> entity.swingTime,
                        (entity, val) -> entity.swingTime = val)

                // swinging
                .property("is swinging", boolean.class,
                        entity -> entity.swinging)

                // removeArrowTime, removeStingerTime
                .property("remove arrow time", int.class, SecurityLevel.STANDARD,
                        entity -> entity.removeArrowTime,
                        (entity, val) -> entity.removeArrowTime = val)

                // removeStingerTime
                .property("remove stinger time", int.class, SecurityLevel.STANDARD,
                        entity -> entity.removeStingerTime,
                        (entity, val) -> entity.removeStingerTime = val)

                // attackAnim, oAttackAnim
                .property("attack animation", float.class,
                        entity -> entity.attackAnim)

                // oAttackAnim
                .property("last attack animation", float.class,
                        entity -> entity.oAttackAnim)

                // walkAnimation
                .property("walk animation", WalkAnimationState.class,
                        entity -> entity.walkAnimation)

                // yBodyRot, yHeadRot
                .property("body rotation", float.class, SecurityLevel.ENHANCED,
                        entity -> entity.yBodyRot,
                        (entity, val) -> entity.yBodyRot = val)

                // yHeadRot
                .property("head rotation", float.class, SecurityLevel.ENHANCED,
                        entity -> entity.yHeadRot,
                        (entity, val) -> entity.yHeadRot = val)

                // xxa, yya, zza
                // xxa
                .property("movement x", float.class, SecurityLevel.STANDARD,
                        entity -> entity.xxa,
                        (entity, val) -> entity.xxa = val)

                // yya
                .property("movement y", float.class, SecurityLevel.STANDARD,
                        entity -> entity.yya,
                        (entity, val) -> entity.yya = val)

                // zza
                .property("movement z", float.class, SecurityLevel.STANDARD,
                        entity -> entity.zza,
                        (entity, val) -> entity.zza = val)

                // hurtDuration
                .property("hurt duration", int.class,
                        entity -> entity.hurtDuration)

                // deathTime
                .property("death time", int.class,
                        entity -> entity.deathTime)

                // absorptionAmount
                .property("absorption amount", float.class, SecurityLevel.ENHANCED,
                        LivingEntity::getAbsorptionAmount,
                        LivingEntity::setAbsorptionAmount)

                // useItem
                .property("using item", ItemStack.class,
                        LivingEntity::getUseItem)

                // useItemRemaining
                .property("remaining use [item] ticks", int.class,
                        LivingEntity::getUseItemRemainingTicks)

                // fallFlyTicks
                .property("fall fly ticks", int.class,
                        LivingEntity::getFallFlyingTicks)

                // lastClimbablePos
                .property("last climbable position", BlockPos.class,
                        entity -> entity.getLastClimbablePos().orElse(null))

                // brain
                .property("brain", Brain.class,
                        LivingEntity::getBrain)

                .register();
        //</editor-fold>

        //<editor-fold desc="Entity">
        SkriptRegistry.registerType(Entity.class, "entity", "entity")

                // type
                .property("type", EntityType.class, SecurityLevel.MINIMAL,
                        Entity::getType)

                // id
                .property("id", int.class, SecurityLevel.STANDARD,
                        Entity::getId)

                // blocksBuilding
                .property("blocks building", boolean.class, SecurityLevel.STANDARD,
                        entity -> entity.blocksBuilding)

                // passengers
                .pluralProperty("passengers", Entity.class, SecurityLevel.MINIMAL,
                        entity -> entity.getPassengers().toArray(Entity[]::new))

                // vehicle
                .property("vehicle", Entity.class, SecurityLevel.STANDARD,
                        Entity::getVehicle)

                // level
                .property("(world|level)", Level.class, SecurityLevel.STANDARD,
                        Entity::level)

                // xo, yo, zo
                .property("previous x", double.class, SecurityLevel.STANDARD,
                        entity -> entity.xo)

                .property("previous y", double.class, SecurityLevel.STANDARD,
                        entity -> entity.yo)

                .property("previous z", double.class, SecurityLevel.STANDARD,
                        entity -> entity.zo)

                // position
                .property("position", Vec3.class, SecurityLevel.STANDARD,
                        Entity::position)

                // blockPosition
                .property("block position", BlockPos.class, SecurityLevel.STANDARD,
                        Entity::blockPosition)

                // chunkPosition
                .property("chunk position", ChunkPos.class, SecurityLevel.STANDARD,
                        Entity::chunkPosition)

                // deltaMovement
                .property("delta movement", Vec3.class, SecurityLevel.STANDARD,
                        Entity::getDeltaMovement)

                // yRot, xRot
                .property("yaw", float.class, SecurityLevel.STANDARD,
                        Entity::getYRot)

                .property("pitch", float.class, SecurityLevel.STANDARD,
                        Entity::getXRot)

                // yRotO, xRotO
                .property("previous yaw", float.class, SecurityLevel.STANDARD,
                        entity -> entity.yRotO)

                .property("previous pitch", float.class, SecurityLevel.STANDARD,
                        entity -> entity.xRotO)

                // bb
                .property("bounding box", AABB.class, SecurityLevel.ENHANCED,
                        Entity::getBoundingBox)

                // onGround
                .property("on ground", boolean.class, SecurityLevel.STANDARD,
                        Entity::onGround)

                // horizontalCollision, verticalCollision, verticalCollisionBelow, minorHorizontalCollision
                .property("horizontal collision", boolean.class, SecurityLevel.STANDARD,
                        entity -> entity.horizontalCollision)

                .property("vertical collision", boolean.class, SecurityLevel.STANDARD,
                        entity -> entity.verticalCollision)

                .property("vertical collision below", boolean.class, SecurityLevel.STANDARD,
                        entity -> entity.verticalCollisionBelow)

                .property("minor horizontal collision", boolean.class, SecurityLevel.STANDARD,
                        entity -> entity.minorHorizontalCollision)

                // hurtMarked
                .property("hurt marked", boolean.class, SecurityLevel.STANDARD,
                        entity -> entity.hurtMarked)

                .property("removal reason", Entity.RemovalReason.class, SecurityLevel.STANDARD,
                        Entity::getRemovalReason)

                // walkDistO, walkDist, moveDist, flyDist
                .property("previous walk distance", float.class,
                        entity -> entity.walkDistO)

                .property("walk distance", float.class,
                        entity -> entity.walkDist)

                .property("move distance", float.class,
                        entity -> entity.moveDist)

                .property("fly distance", float.class,
                        entity -> entity.flyDist)

                // fallDistance
                .property("fall distance", float.class, SecurityLevel.MINIMAL,
                        entity -> entity.fallDistance)

                // xOld, yOld, zOld
                .property("x old", double.class, SecurityLevel.MINIMAL,
                        entity -> entity.xOld)

                .property("y old", double.class, SecurityLevel.MINIMAL,
                        entity -> entity.yOld)

                .property("z old", double.class, SecurityLevel.MINIMAL,
                        entity -> entity.zOld)

                // noPhysics
                .property("no physics", boolean.class, SecurityLevel.MINIMAL,
                        entity -> entity.noPhysics)

                // random
                .property("random number (source|provider)", RandomSource.class, SecurityLevel.ENHANCED,
                        Entity::getRandom)

                // remainingFireTicks
                .property("remaining fire ticks", int.class, SecurityLevel.MINIMAL,
                        Entity::getRemainingFireTicks)

                // wasTouchingWater
                .property("is in water", boolean.class, SecurityLevel.MINIMAL,
                        Entity::isInWater)

                // wasEyeInWater
                .property("is touching water", boolean.class, SecurityLevel.MINIMAL,
                        Entity::isUnderWater)

                // invulnerableTime
                .property("invulnerable time", int.class, SecurityLevel.MINIMAL,
                        entity -> entity.invulnerableTime)

                // tickCount
                .property("tick count", Integer.class, SecurityLevel.MINIMAL,
                        entity -> entity.tickCount)

                // entityData
                .property("entity data", SynchedEntityData.class, SecurityLevel.MINIMAL,
                        Entity::getEntityData)

                // packetPositionCodec
                .property("packet position codec", VecDeltaCodec.class, SecurityLevel.ENHANCED,
                        Entity::getPositionCodec)

                // noCulling
                .property("no culling", boolean.class, SecurityLevel.MINIMAL,
                        entity -> entity.noCulling)

                // hasImpulse
                .property("has impulse", boolean.class, SecurityLevel.MINIMAL,
                        entity -> entity.hasImpulse)

                // portalProcess
                .property("portal processor", PortalProcessor.class, SecurityLevel.STANDARD,
                        entity -> entity.portalProcess)

                // portalCooldown
                .property("portal cooldown", int.class, SecurityLevel.STANDARD,
                        Entity::getPortalCooldown)

                // invulnerable
                .property("invulnerable", boolean.class, SecurityLevel.MINIMAL,
                        Entity::isInvulnerable)

                // uuid, stringUUID
                .property("uuid", UUID.class, SecurityLevel.MINIMAL,
                        Entity::getUUID)

                .property("string uuid", String.class, SecurityLevel.MINIMAL,
                        Entity::getStringUUID)

                // hasGlowingTag
                .property("has glowing tag", boolean.class, SecurityLevel.MINIMAL,
                        Entity::isCurrentlyGlowing)

                // tags
                .property("tags", Set.class, SecurityLevel.MINIMAL,
                        Entity::getTags)

                // eyeHeight
                .property("eye height", float.class, SecurityLevel.MINIMAL,
                        Entity::getEyeHeight)

                // isInPowderSnow, wasInPowderSnow
                .property("is in powder snow", boolean.class, SecurityLevel.MINIMAL,
                        entity -> entity.isInPowderSnow)

                .property("was in powder snow", boolean.class, SecurityLevel.MINIMAL,
                        entity -> entity.wasInPowderSnow)

                // wasOnFire
                .property("was on fire", boolean.class, SecurityLevel.MINIMAL,
                        entity -> entity.wasOnFire)

                // mainSupportingBlockPos
                .property("main supporting block position", BlockPos.class, SecurityLevel.MINIMAL,
                        entity -> entity.mainSupportingBlockPos.orElse(null))

                // hasVisualFire
                .property("has visual fire", boolean.class, SecurityLevel.MINIMAL,
                        Entity::isOnFire)

                // inBlockState
                .property("in block state", BlockState.class, SecurityLevel.MINIMAL,
                        Entity::getInBlockState)

                .register();
        //</editor-fold>



    }

}
