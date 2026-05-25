package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class TecladoInput implements InputProvider {

    @Override
    public boolean isMovingLeft() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    @Override
    public boolean isMovingRight() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    @Override
    public void update(float delta) {}

    @Override
    public void dispose() {}
}
