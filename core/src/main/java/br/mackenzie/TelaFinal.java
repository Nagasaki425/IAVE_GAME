package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TelaFinal implements Screen {

    private static final float W = 1280f;
    private static final float H = 720f;

    private final Main jogo;
    private final SpriteBatch batch;
    private final int score;
    private final int scoreMaximo;
    private final float tempoTotal;
    private final String classificacao;
    private final Color cor;

    private Viewport viewport;
    private OrthographicCamera camera;
    private Texture fundo;
    private Texture painel;
    private BitmapFont font;

    public TelaFinal(Main jogo, int score, int scoreMaximo, float tempoTotal) {
        this.jogo = jogo;
        this.batch = jogo.batch;
        this.score = score;
        this.scoreMaximo = scoreMaximo;
        this.tempoTotal = tempoTotal;

        float porcentagem = (scoreMaximo > 0) ? (score * 100f / scoreMaximo) : 0;

        if (porcentagem >= 100) {
            classificacao = "PLATINA";
            cor = new Color(0.6f, 0.9f, 1f, 1f);
        } else if (porcentagem >= 75) {
            classificacao = "OURO";
            cor = Color.GOLD;
        } else if (porcentagem >= 50) {
            classificacao = "PRATA";
            cor = Color.LIGHT_GRAY;
        } else {
            classificacao = "BRONZE";
            cor = new Color(0.8f, 0.5f, 0.2f, 1f);
        }
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(W, H, camera);
        fundo = criarTex(new Color(0.05f, 0.2f, 0.05f, 1f));
        painel = criarTex(new Color(0, 0, 0, 0.7f));
        font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(fundo, 0, 0, W, H);
        batch.setColor(0, 0, 0, 0.75f);
        batch.draw(painel, 120, 80, W - 240, H - 160);
        batch.setColor(1, 1, 1, 1);

        font.getData().setScale(2f);
        font.setColor(cor);
        font.draw(batch, "CIDADE SALVA!", 430, 650);

        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);
        font.draw(batch, "Classificacao: " + classificacao, 200, 500);
        font.draw(batch, "Score: " + score + " / " + scoreMaximo, 200, 450);
        font.draw(batch, "Tempo: " + String.format("%.1f", tempoTotal) + "s", 200, 400);

        font.getData().setScale(1f);
        font.draw(batch, "ENTER ou ESC para voltar", 420, 200);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            jogo.setScreen(new MenuPrincipal(jogo));
        }
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (fundo != null) fundo.dispose();
        if (painel != null) painel.dispose();
        if (font != null) font.dispose();
    }

    private Texture criarTex(Color c) {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(c);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }
}
