package com.moemeido.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.moemeido.game.Application;
import com.moemeido.game.entities.Coin;
import com.moemeido.game.entities.Target;
import com.moemeido.game.utils.FontManager;
import com.moemeido.game.utils.MyContactListener;

import static com.moemeido.game.utils.B2DVars.PPM;

/**
 * Created by Ryan on 2/10/2018.
 */

public class PlayScreen implements Screen {

    private Application app;

    private World world;
    private Stage stage;
    private Box2DDebugRenderer b2dr;
    private MyContactListener myContactListener;
    private Vector3 touch;

    private OrthographicCamera camera;
    private Viewport viewport;

    private int tapCount;
    private Target target;

    private Preferences prefs; // Allows for persistence of the tap counter
    private FontManager fontManager;
    private Texture backgroundTex;

    private Array<Coin> coins;
    private Pool<Coin> coinPool;

    public PlayScreen(final Application app) {
        this.app = app;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Application.V_WIDTH, Application.V_HEIGHT);

        app.batch.setProjectionMatrix(camera.combined);

        world = new World(new Vector2(0f, -9.81f), false);
        b2dr = new Box2DDebugRenderer();
        myContactListener = new MyContactListener();
        viewport = new FitViewport(Application.V_WIDTH, Application.V_HEIGHT);
        stage = new Stage(viewport);
        prefs = Gdx.app.getPreferences("My Preferences");
        fontManager = new FontManager();
    }

    @Override
    public void show() {
        world.setContactListener(myContactListener);
        touch = new Vector3();
        target = new Target(world, camera.viewportWidth / 2, camera.viewportHeight / 2, 16);
        tapCount = prefs.getInteger("Number of taps", 0);
        backgroundTex = new Texture(Gdx.files.internal("img/background.png"));

        coins = new Array<Coin>();
        coinPool = new Pool<Coin>(0, 200) {
            @Override
            protected Coin newObject() {
                return new Coin(app, world, stage, touch.x / PPM, touch.y / PPM);
            }
        };
    }

    private void handleInput() {
        for(int i = 0; i < 2; i++) {
            touch.x = Gdx.input.getX(i);
            touch.y = Gdx.input.getY(i);

            touch = camera.unproject(touch, viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

            if (Gdx.input.isTouched(i) && Gdx.input.justTouched() && checkGameBounds(touch)) {

                System.out.println("x:" + touch.x + " | y:" + touch.y);

                prefs.putInteger("Number of taps", tapCount++);
                prefs.flush();
                Coin coin = coinPool.obtain();
                coins.add(coin);
                coin.getBody().setTransform(touch.x / PPM, touch.y / PPM, 0);
                coin.jumpToTarget(target, reverseNumber(touch.y, 0, Application.V_HEIGHT));
            }
        }
    }

    private void update(float delta) {
        world.step(1f / Application.APP_FPS, 6, 2);
        stage.act(delta);

        // Updates each coin within the coins array
        // Also detects if coins are flagged for removal and adds them to a separate
        // array to be removed
        for(Coin coin : coins) {
            coin.update();

            if (coin.isReadyToDestroy()) {
                if(coinPool.getFree() < coinPool.max) {
                    coins.removeValue(coin, true);
                    coinPool.free(coin);
                }
                else if(!world.isLocked()){
                    coins.removeValue(coin, true);
                    world.destroyBody(coin.getBody());
                }
            }
        }

        handleInput();
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.begin();

        app.batch.draw(backgroundTex, 0, 0, Application.V_WIDTH, Application.V_HEIGHT);
        fontManager.font.draw(app.batch, "Coin array: " + String.valueOf(coins.size), 10, 25);
        fontManager.font.draw(app.batch, "Coin pool: " + String.valueOf(coinPool.getFree()), 10, 50);
        fontManager.font.draw(app.batch, "Taps: " + String.valueOf(tapCount), 10, 75);

        app.batch.end();

        b2dr.render(world, camera.combined.cpy().scl(PPM));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void pause() {
        prefs.flush(); // When the app is paused or destroyed, retain the values in preferences
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        world.dispose();
        stage.dispose();
        b2dr.dispose();
        fontManager.dispose();
        coinPool.clear();

        if (coins != null) {
            for (Coin coin : coins) {
                coin.dispose();
            }
        }
    }

    // Needed this method because the screen coordinates when using Gdx.input.getY() are way different
    // on an android phone. This is used to get world coordinates from unprojected screen coordinates.
    // The coordinate system appears to be reversed with 0,0 being at the top left instead of bottom right.
    // So when the user taps, this reverses the input. Ex. The user taps near top of screen, usually this would
    // output a big number like 630 on the Y coordinate since this games height is 640 pixels. With this method,
    // it allows you to reverse the coordinate plane so that 630 would be equal to ~10 which can then be put
    // into the estimated jump height of the coin!
    private float reverseNumber(float num, float min, float max) {
        return ((max - min) - num) / PPM / 2;
    }

    // Checks if a touch input is within the game world, not just within the devices available screen
    private Boolean checkGameBounds(Vector3 touch) {
        return touch.x > 0 && touch.x < viewport.getWorldWidth() && touch.y > 0 && touch.y < viewport.getWorldHeight();
    }

}
