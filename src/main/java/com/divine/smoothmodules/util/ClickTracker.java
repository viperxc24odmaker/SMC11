package com.divine.smoothmodules.util;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Tracks left/right mouse clicks by polling GLFW every frame (rising-edge
 * detection). This gives accurate CPS without needing a Mouse mixin.
 * Poll {@link #update()} once per rendered frame.
 */
public final class ClickTracker {

    private static final Deque<Long> LEFT = new ArrayDeque<>();
    private static final Deque<Long> RIGHT = new ArrayDeque<>();

    private static boolean leftDown = false;
    private static boolean rightDown = false;

    private ClickTracker() {}

    public static void update() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getWindow() == null) return;
        long handle = mc.getWindow().getHandle();

        boolean lNow = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rNow = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;

        long now = System.currentTimeMillis();
        if (lNow && !leftDown) LEFT.addLast(now);
        if (rNow && !rightDown) RIGHT.addLast(now);
        leftDown = lNow;
        rightDown = rNow;

        prune(LEFT, now);
        prune(RIGHT, now);
    }

    private static void prune(Deque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > 1000L) {
            q.pollFirst();
        }
    }

    public static int leftCps() {
        prune(LEFT, System.currentTimeMillis());
        return LEFT.size();
    }

    public static int rightCps() {
        prune(RIGHT, System.currentTimeMillis());
        return RIGHT.size();
    }
}
