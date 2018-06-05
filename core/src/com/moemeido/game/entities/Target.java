package com.moemeido.game.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.moemeido.game.utils.B2DVars;

import static com.moemeido.game.utils.B2DVars.PPM;

/**
 * Created by Ryan on 2/11/2018.
 */

public class Target {

    private World world;
    private float x;
    private float y;
    private float size;

    private Body body;

    public Target(World world, float x, float y, float size) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.size = size;

        defineTarget();
    }

    private void defineTarget() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x / PPM, y / PPM);
        bdef.fixedRotation = true;
        body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(size / 2 / PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = B2DVars.TARGET_BIT;
        fdef.filter.maskBits = B2DVars.COIN_BIT;
        body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    public Vector2 getPosition() {
        return new Vector2(body.getPosition().x, body.getPosition().y);
    }

}
