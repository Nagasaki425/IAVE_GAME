package br.mackenzie;

public class GerenciadorInput implements InputProvider {

    private final TecladoInput    teclado;
    private final SkateboardInput skate;

    public GerenciadorInput() {
        teclado = new TecladoInput();
        skate   = new SkateboardInput();
    }

    @Override
    public boolean isMovingLeft() {
        return teclado.isMovingLeft() || skate.isMovingLeft();
    }

    @Override
    public boolean isMovingRight() {
        return teclado.isMovingRight() || skate.isMovingRight();
    }

    @Override
    public void update(float delta) {
        teclado.update(delta);
        skate.update(delta);
    }

    @Override
    public void dispose() {
        teclado.dispose();
        skate.dispose();
    }
}
