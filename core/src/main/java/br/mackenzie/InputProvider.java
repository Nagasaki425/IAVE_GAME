package br.mackenzie;

public interface InputProvider {
    boolean isMovingLeft();
    boolean isMovingRight();
    void update(float delta);
    void dispose();
}
