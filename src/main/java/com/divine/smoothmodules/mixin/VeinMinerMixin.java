package com.divine.smoothmodules.mixin;

import com.divine.smoothmodules.module.ModuleManager;
import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.modules.VeinMinerModule;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class VeinMinerMixin {

	@Inject(
		method = "attackBlock",
private void smoothmodules$checkVeinMiner(
		BlockPos pos,
		Direction direction,
		CallbackInfoReturnable<Boolean> cir
	) {
		// Only activate if veinminer module is enabled
		Module vm = ModuleManager.getByName("VeinMiner");
		if (vm == null || !vm.isEnabled() || VeinMinerModule.isBreakingVein()) {
			return;
		}

		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.world == null) {
			return;
		}

		BlockState state = mc.world.getBlockState(pos);
		if (VeinMinerModule.isValidBlock(state)) {
			VeinMinerModule.mineVein(pos);
		}
	}		}
	}
}
