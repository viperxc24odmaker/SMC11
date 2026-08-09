package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;

public class AutoClickerModule extends Module {

	private int clicksPerSecond = 8;
	private int tickCounter = 0;
	private int ticksPerClick;

	public AutoClickerModule() {
		super("AutoClicker", "Automatically clicks at set CPS", ModuleCategory.MISC);
		updateTicksPerClick();
	}

	private void updateTicksPerClick() {
		// 20 ticks per second in Minecraft
		ticksPerClick = Math.max(1, 20 / clicksPerSecond);
	}

	@Override
	protected void onEnable() {
		tickCounter = 0;
	}

	@Override
	public void onTick() {
		if (mc.player == null || mc.options == null) {
			return;
		}

		tickCounter++;
		if (tickCounter >= ticksPerClick) {
			// Simulate attack key press via options.attackKey
			// This handles both entity and block attacks automatically
			mc.options.attackKey.setPressed(true);
			tickCounter = 0;
		}
	}

	@Override
	protected void onDisable() {
		// Make sure we release the attack key when disabled
		if (mc.options != null) {
			mc.options.attackKey.setPressed(false);
		}
	}

	public int getClicksPerSecond() {
		return clicksPerSecond;
	}

	public void setClicksPerSecond(int cps) {
		this.clicksPerSecond = Math.max(1, Math.min(20, cps)); // 1-20 CPS range
		updateTicksPerClick();
	}
}
