package com.moemeido.game.utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.moemeido.game.entities.Coin;

/**
 * Created by Ryan on 2/11/2018.
 */

public class MyContactListener implements ContactListener{

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null) return;
        if (fa.getUserData() == null || fb.getUserData() == null) return;

        if (fa.getUserData() instanceof Coin) {
            Coin coin = (Coin) fa.getUserData();
            coin.hitTarget();
        }

        if (fb.getUserData() instanceof Coin) {
            Coin coin = (Coin) fb.getUserData();
            coin.hitTarget();
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null) return;
        if (fa.getUserData() == null || fb.getUserData() == null) return;

        if (fa.getUserData() instanceof Coin) {
            Coin coin = (Coin) fa.getUserData();
            coin.hitTarget();
        }

        if (fb.getUserData() instanceof Coin) {
            Coin coin = (Coin) fb.getUserData();
            coin.hitTarget();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
