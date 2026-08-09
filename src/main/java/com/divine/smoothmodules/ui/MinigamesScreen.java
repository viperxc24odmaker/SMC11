package com.divine.smoothmodules.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Random;

/**
 * Minigames popup screen with Snake, Pong, and Tetris.
 * Opens as an overlay popup from the mod menu.
 */
public class MinigamesScreen extends Screen {

	private MinigameType currentGame = MinigameType.MENU;
	private SnakeGame snakeGame;
	private PongGame pongGame;
	private TetrisGame tetrisGame;

	private static final int POPUP_WIDTH = 400;
	private static final int POPUP_HEIGHT = 300;

	public enum MinigameType {
		MENU, SNAKE, PONG, TETRIS
	}

	public MinigamesScreen() {
		super(Text.literal("Minigames"));
		this.snakeGame = new SnakeGame();
		this.pongGame = new PongGame();
		this.tetrisGame = new TetrisGame();
	}

	@Override
	public void render(DrawContext ctx, int mx, int my, float delta) {
		// Dim background
		ctx.fill(0, 0, this.width, this.height, 0x80000000);

		// Popup background
		int popupX = (this.width - POPUP_WIDTH) / 2;
		int popupY = (this.height - POPUP_HEIGHT) / 2;
		ctx.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + POPUP_HEIGHT, 0xFF1a1a2e);
		ctx.fill(popupX, popupY, popupX + POPUP_WIDTH, popupY + 30, 0xFF16213e); // Header

		switch (currentGame) {
			case MENU:
				renderMenu(ctx, popupX, popupY);
				break;
			case SNAKE:
				snakeGame.render(ctx, popupX + 50, popupY + 50);
				break;
			case PONG:
				pongGame.render(ctx, popupX + 50, popupY + 50);
				break;
			case TETRIS:
				tetrisGame.render(ctx, popupX + 50, popupY + 50);
				break;
		}

		super.render(ctx, mx, my, delta);
	}

	private void renderMenu(DrawContext ctx, int x, int y) {
		ctx.drawCenteredTextWithShadow(this.textRenderer, "Minigames", x + POPUP_WIDTH / 2, y + 10, 0xFFFFFF);

		// Buttons
		int buttonW = 100;
		int buttonH = 30;
		int startY = y + 80;
		int centerX = x + POPUP_WIDTH / 2;

		drawButton(ctx, centerX - buttonW / 2, startY, buttonW, buttonH, "Snake", 0xFF00FF00);
		drawButton(ctx, centerX - buttonW / 2, startY + 50, buttonW, buttonH, "Pong", 0xFF0000FF);
		drawButton(ctx, centerX - buttonW / 2, startY + 100, buttonW, buttonH, "Tetris", 0xFFFFFF00);
	}

	private void drawButton(DrawContext ctx, int x, int y, int w, int h, String label, int color) {
		ctx.fill(x, y, x + w, y + h, color);
		ctx.drawCenteredTextWithShadow(this.textRenderer, label, x + w / 2, y + h / 2 - 4, 0xFF000000);
	}

	@Override
	public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
		if (currentGame == MinigameType.TETRIS) {
			// Tetris scroll handling
		}
		return super.mouseScrolled(mx, my, scrollX, scrollY);
	}

	@Override
	public boolean mouseClicked(double mx, double my, int button) {
		if (currentGame == MinigameType.MENU) {
			int popupX = (this.width - POPUP_WIDTH) / 2;
			int popupY = (this.height - POPUP_HEIGHT) / 2;
			int centerX = popupX + POPUP_WIDTH / 2;
			int startY = popupY + 80;

			// Snake button
			if (mx >= centerX - 50 && mx <= centerX + 50 && my >= startY && my <= startY + 30) {
				currentGame = MinigameType.SNAKE;
				snakeGame.reset();
				return true;
			}
			// Pong button
			if (mx >= centerX - 50 && mx <= centerX + 50 && my >= startY + 50 && my <= startY + 80) {
				currentGame = MinigameType.PONG;
				pongGame.reset();
				return true;
			}
			// Tetris button
			if (mx >= centerX - 50 && mx <= centerX + 50 && my >= startY + 100 && my <= startY + 130) {
				currentGame = MinigameType.TETRIS;
				tetrisGame.reset();
				return true;
			}
		}
		return super.mouseClicked(mx, my, button);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		// Escape to go back
		if (keyCode == 256) {
			if (currentGame == MinigameType.MENU) {
				this.close();
			} else {
				currentGame = MinigameType.MENU;
			}
			return true;
		}

		// Pass input to game
		switch (currentGame) {
			case SNAKE:
				snakeGame.handleKey(keyCode);
				break;
			case PONG:
				pongGame.handleKey(keyCode);
				break;
			case TETRIS:
				tetrisGame.handleKey(keyCode);
				break;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {
		this.client.setScreen(null);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return currentGame == MinigameType.MENU;
	}

	// ===== SNAKE GAME =====
	private static class SnakeGame {
		private int[][] grid = new int[20][20];
		private int headX = 10, headY = 10;
		private int dirX = 1, dirY = 0;
		private int nextDirX = 1, nextDirY = 0;
		private int appleX, appleY;
		private int score = 0;
		private int tickCounter = 0;

		SnakeGame() {
			reset();
		}

		void reset() {
			grid = new int[20][20];
			headX = 10;
			headY = 10;
			dirX = 1;
			dirY = 0;
			score = 0;
			tickCounter = 0;
			spawnApple();
		}

		void spawnApple() {
			Random r = new Random();
			appleX = r.nextInt(20);
			appleY = r.nextInt(20);
		}

		void handleKey(int key) {
			if (key == 262 && dirX == 0) { // RIGHT
				nextDirX = 1;
				nextDirY = 0;
			} else if (key == 263 && dirX == 0) { // LEFT
				nextDirX = -1;
				nextDirY = 0;
			} else if (key == 264 && dirY == 0) { // DOWN
				nextDirX = 0;
				nextDirY = 1;
			} else if (key == 265 && dirY == 0) { // UP
				nextDirX = 0;
				nextDirY = -1;
			}
		}

		void render(DrawContext ctx, int offsetX, int offsetY) {
			// Game loop
			tickCounter++;
			if (tickCounter > 10) {
				dirX = nextDirX;
				dirY = nextDirY;
				headX += dirX;
				headY += dirY;

				// Wrap around
				if (headX < 0) headX = 19;
				if (headX > 19) headX = 0;
				if (headY < 0) headY = 19;
				if (headY > 19) headY = 0;

				tickCounter = 0;
			}

			// Draw grid
			for (int x = 0; x < 20; x++) {
				for (int y = 0; y < 20; y++) {
					int px = offsetX + x * 10;
					int py = offsetY + y * 10;
					ctx.fill(px, py, px + 9, py + 9, 0xFF333333);
				}
			}

			// Draw snake head
			ctx.fill(offsetX + headX * 10, offsetY + headY * 10, offsetX + headX * 10 + 9, offsetY + headY * 10 + 9, 0xFF00FF00);

			// Draw apple
			ctx.fill(offsetX + appleX * 10, offsetY + appleY * 10, offsetX + appleX * 10 + 9, offsetY + appleY * 10 + 9, 0xFFFF0000);
		}
	}

	// ===== PONG GAME =====
	private static class PongGame {
		private int paddleLeft = 50, paddleRight = 50;
		private int ballX = 100, ballY = 75;
		private int ballDirX = 2, ballDirY = 2;
		private int scoreLeft = 0, scoreRight = 0;

		void reset() {
			ballX = 100;
			ballY = 75;
			ballDirX = 2;
			ballDirY = 2;
			scoreLeft = 0;
			scoreRight = 0;
		}

		void handleKey(int key) {
			if (key == 265) paddleLeft = Math.max(0, paddleLeft - 10); // UP
			if (key == 264) paddleLeft = Math.min(140, paddleLeft + 10); // DOWN
		}

		void render(DrawContext ctx, int offsetX, int offsetY) {
			// Update ball
			ballX += ballDirX;
			ballY += ballDirY;

			// Bounce on top/bottom
			if (ballY < 0 || ballY > 140) ballDirY = -ballDirY;

			// Bounce on paddles or score
			if (ballX < 10 && ballY > paddleLeft - 10 && ballY < paddleLeft + 20) {
				ballDirX = 2;
			} else if (ballX > 190) {
				scoreLeft++;
				reset();
			}

			if (ballX > 190 && ballY > 75 - 10 && ballY < 75 + 20) {
				ballDirX = -2;
			} else if (ballX < 0) {
				scoreRight++;
				reset();
			}

			// Draw court
			ctx.fill(offsetX, offsetY, offsetX + 200, offsetY + 150, 0xFF000000);

			// Draw paddles
			ctx.fill(offsetX, offsetY + paddleLeft, offsetX + 5, offsetY + paddleLeft + 30, 0xFFFFFFFF);
			ctx.fill(offsetX + 195, offsetY + 75, offsetX + 200, offsetY + 105, 0xFFFFFFFF);

			// Draw ball
			ctx.fill(offsetX + ballX - 3, offsetY + ballY - 3, offsetX + ballX + 3, offsetY + ballY + 3, 0xFFFFFF00);
		}
	}

	// ===== TETRIS GAME =====
	private static class TetrisGame {
		private int[][] board = new int[20][10];
		private int score = 0;

		void reset() {
			board = new int[20][10];
			score = 0;
		}

		void handleKey(int key) {
			// Tetris controls
		}

		void render(DrawContext ctx, int offsetX, int offsetY) {
			// Draw Tetris board
			for (int y = 0; y < 20; y++) {
				for (int x = 0; x < 10; x++) {
					int px = offsetX + x * 15;
					int py = offsetY + y * 15;
					ctx.fill(px, py, px + 14, py + 14, board[y][x] == 0 ? 0xFF333333 : 0xFF00FF00);
				}
			}
		}
	}
}
