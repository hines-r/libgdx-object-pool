package com.moemeido.game.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.moemeido.game.Application;
import com.moemeido.game.utils.B2DVars;
import com.moemeido.game.utils.FontManager;

import static com.moemeido.game.utils.B2DVars.PPM;

/**
 * Created by Ryan on 2/10/2018.
 */

public class Coin implements Poolable{

    private Application app;
    private World world;
    private Stage stage;
    private float x;
    private float y;
    private float radius;
    private boolean readyToDestroy;
    private boolean hitTarget;

    private Body body;
    private FontManager fontManager;
    private Label labelValue;

    public Coin(Application app, World world, Stage stage, float x, float y) {
        this.app = app;
        this.world = world;
        this.stage = stage;
        this.x = x;
        this.y = y;

        radius = 8;
        int value = 1;

        fontManager = new FontManager();
        Label.LabelStyle style = new Label.LabelStyle(fontManager.font, Color.WHITE);
        labelValue = new Label("+" + String.valueOf(value), style);

        defineCoin();
    }

    private void defineCoin() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true;
        bdef.position.set(x / PPM, y / PPM);
        body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius / 2 / PPM); // sets the radius

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.COIN_BIT;
        fdef.filter.maskBits = B2DVars.TARGET_BIT; // Can only come in contact with a target, not other coins too
        body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    public void update() {
        if (hitTarget) {
            displayValue(50f, 25f);
            readyToDestroy = true;
        }

        if (body.getPosition().y < 0) {
            readyToDestroy = true;
        }
    }

    /**
     * Displays the coins value right before its destruction. Produces a label with a random position
     * that is near the coin at the time of destruction.
     * @param xRand - random value along the current value of the x-axis during destruction
     * @param yRand - random value along the current value of the y-axis during destruction
     */
    private void displayValue(float xRand, float yRand) {
        float randomX = MathUtils.random(body.getPosition().x * PPM - xRand, body.getPosition().x * PPM + xRand);
        float randomY = MathUtils.random(body.getPosition().y * PPM - yRand, body.getPosition().y * PPM + yRand);

        labelValue.setPosition(randomX, randomY);
        labelValue.addAction(Actions.addAction(Actions.parallel(Actions.alpha(1),Actions.fadeOut(1f), Actions.moveBy(0, 75f, 1f))));
        stage.addActor(labelValue);
    }

    public void hitTarget() {
        if (body.getLinearVelocity().y < 0) {
            hitTarget = true;
        }
    }

    public void jumpToTarget(Target target, float jumpHeight) {
        float gravity = -9.81f;

        float displacementX = target.getPosition().x - body.getPosition().x;
        float displacementY = target.getPosition().y - body.getPosition().y;

        jumpHeight += MathUtils.random(0f, 1f);

        Vector2 initialVelocity = new Vector2(
                displacementX / ((float)(Math.sqrt((-2f * jumpHeight) / gravity) + Math.sqrt((2f * (displacementY - jumpHeight)) / gravity))),
                (float) (Math.sqrt(-2f * gravity * jumpHeight)));

        // BE SURE TO APPLY A LINEAR IMPULSE!
        // apply force is used to apply force gradually over time
        // apply impulse changes a bodies velocity immediately!
        body.applyLinearImpulse(initialVelocity.x, initialVelocity.cpy().y, body.getPosition().x, body.getPosition().y,true);
    }

    public void dispose() {
        world.dispose();
        stage.dispose();
        fontManager.dispose();
    }

    @Override
    public void reset() {
        readyToDestroy = false;
        hitTarget = false;
        body.setTransform(-50 / PPM, -50 / PPM, 0);
        body.setAwake(false);
    }

    public Body getBody() {
        return body;
    }

    public boolean isReadyToDestroy() {
        return readyToDestroy;
    }

}
