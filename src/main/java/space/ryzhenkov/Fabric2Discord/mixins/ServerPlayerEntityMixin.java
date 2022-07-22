package space.ryzhenkov.Fabric2Discord.mixins;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.ryzhenkov.Fabric2Discord.config.F2DConfig;
import space.ryzhenkov.Fabric2Discord.utils.MessageUtils;

import java.util.HashMap;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {


    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("HEAD"))
    private void onDeath(DamageSource source, CallbackInfo ci) throws CommandSyntaxException {
        if (!F2DConfig.messages.playerDeath.INSTANCE.getEnabled()) return;
        if (F2DConfig.general.ids.INSTANCE.getLogChannel() == null) return;

        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("death_message", this.getDamageTracker().getDeathMessage().getString());
        replacements.put("death_by", source.name);

        MessageUtils.INSTANCE.sendEmbedMessage(F2DConfig.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(F2DConfig.messages.playerDeath.INSTANCE, null, this.getCommandSource().getPlayer(), replacements)
                        .build().asRequest()
        );
    }

    @Inject(method = "worldChanged", at = @At("TAIL"))
    private void worldChanged(ServerWorld origin, CallbackInfo ci) throws CommandSyntaxException {
        if (!F2DConfig.messages.playerDimension.INSTANCE.getEnabled()) return;
        if (F2DConfig.general.ids.INSTANCE.getLogChannel() == null) return;

        HashMap<String, String> replacements = new HashMap<>();
        replacements.put("world_origin", origin.getRegistryKey().getValue().toString());
        replacements.put("world_origin_id", origin.getRegistryKey().getValue().getPath());

        MessageUtils.INSTANCE.sendEmbedMessage(F2DConfig.general.ids.INSTANCE.getLogChannel(),
                MessageUtils.INSTANCE.getConfigMessage(F2DConfig.messages.playerDimension.INSTANCE, null, this.getCommandSource().getPlayer(), replacements)
                        .build().asRequest()
        );
    }
}