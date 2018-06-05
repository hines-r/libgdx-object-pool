package com.moemeido.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * Created by Ryan on 2/11/2018.
 */

public class FontManager {

    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter params;

    public BitmapFont font;

    public FontManager() {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/munro.ttf"));
        params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.minFilter = Texture.TextureFilter.Nearest;
        params.magFilter = Texture.TextureFilter.Nearest;
        params.size = 20;
        font = generator.generateFont(params);
    }

    public void dispose() {
        generator.dispose();
        font.dispose();
    }

}
