package com.moemeido.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.moemeido.game.screens.PlayScreen;

public class Application extends Game {

	public static final String APP_TITLE = "Coin Toss";
	public static final int V_WIDTH = 360;
	public static final int V_HEIGHT = V_WIDTH / 9 * 16;
	public static final int APP_FPS = 60;

	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
	}

}
