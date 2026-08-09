package com.divine.smoothmodules.modules;

import com.divine.smoothmodules.module.Module;
import com.divine.smoothmodules.module.ModuleCategory;
import net.minecraft.util.math.Vec3d;

public class BulletProofModule extends Module {

	private Vec3d lastPosition;
	private Vec3d knockbackDirection;
	private long knockbackTime;
	private static final long KNOCKBACK_DISPLAY_DURATION = 1000; // ms

	public BulletProofModule() {
		super("BulletProof", "Shows knockback direction and damage resistance", ModuleCategory.MISC);
	}

	@Override
	protected void onEnable() {
		lastPosition = null;
		knockbackDirection = null;
	}

	@Override
	public void onTick() {
		if (mc.player == null) {
			return;
		}

		Vec3d currentPos = mc.player.getEyePos();

		// Detect knockback by comparing positions
		if (lastPosition != null) {
			Vec3d delta = currentPos.subtract(lastPosition);
			double knockback = delta.length();

			// If player moved unexpectedly (knockback), record it
			if (knockback > 0.01) {
				knockbackDirection = delta.normalize();
				knockbackTime = System.currentTimeMillis();
			}
		}

		lastPosition = currentPos;
	}

	public Vec3d getKnockbackDirection() {
		if (knockbackDirection == null) {
			return null;
		}

		// Clear knockback display if it's been too long
		if (System.currentTimeMillis() - knockbackTime > KNOCKBACK_DISPLAY_DURATION) {
			knockbackDirection = null;
			return null;
		}

		return knockbackDirection;
	}

	public boolean isKnockbackActive() {
		return getKnockbackDirection() != null;
	}
}
