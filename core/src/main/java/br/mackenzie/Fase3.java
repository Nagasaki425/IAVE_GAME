package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Fase3 extends ScreenAdapter {

    private final Main jogo;

    private enum GameState {
        WAITING,
        PLAYING,
        PAUSED,
        GAME_OVER,
        WIN
    }

    private static final int MAX_LIVES = 2;
    private static final int SCORE_VITORIA = 30;
    private static final float VELOCIDADE_FASE3 = 4.5f;

    public static final float LEFT_WALL = 1.5f;
    public static final float RIGHT_WALL = 6.5f;

    private GameState gameState = GameState.WAITING;

    private SpriteBatch spriteBatch;
    private FitViewport viewport;
    private OrthographicCamera hudCamera;

    private Texture playerTex, playerLeftTex, playerRightTex;
    private Texture bgNearTex;

    private Texture coletavel1Tex, coletavel2Tex, coletavel3Tex;
    private Texture obstaculo1Tex, obstaculo2Tex;

    private Texture powerTex;
    private Texture shieldTex;
    private Texture startGameTex, gameOverTex, heartTex, winTex;
    private Texture pauseTex;

    private PlayerShip player;
    private GerenciadorInput gerenciadorInput;

    private Array<coletaveis> coletaveisList;
    private Array<Obstaculo> obstaculos;
    private Array<PowerUp> activePowerUps;
    private Array<Shield> activeShields;

    private float spawnTimer;
    private int score = 0;
    private int lives = 1;
    private float bgNearY = 0f;

    private Sound dropSound;
    private Music music;
    private BitmapFont font;
    private BitmapFont pauseFont;

    private int screenW, screenH;
    private float tempoJogo = 0f;
    private int pauseOption = 0; // 0 = Play, 1 = Menu, 2 = Sair

    public Fase3(Main jogo) {
        this.jogo = jogo;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);

        screenW = Gdx.graphics.getWidth();
        screenH = Gdx.graphics.getHeight();

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, screenW, screenH);

        playerTex = new Texture("player.png");
        playerLeftTex = new Texture("player_left.png");
        playerRightTex = new Texture("player_right.png");

        bgNearTex = new Texture("background_fase3.png");

        coletavel1Tex = new Texture("coletavel1.png");
        coletavel2Tex = new Texture("coletavel2.png");
        coletavel3Tex = new Texture("coletavel3.png");

        obstaculo1Tex = new Texture("obstaculo1.png");
        obstaculo2Tex = new Texture("obstaculo2.png");

        powerTex = new Texture("drop.png");
        shieldTex = new Texture("shield.png");

        startGameTex = new Texture("start_game.png");
        gameOverTex = new Texture("game_over.png");
        heartTex = new Texture("heart.png");
        winTex = new Texture("win.png");
        pauseTex = new Texture("pause.png");

        font = new BitmapFont(Gdx.files.internal("pixel_font.fnt"));
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        pauseFont = new BitmapFont();
        pauseFont.getData().setScale(3.0f);
        pauseFont.setColor(Color.WHITE);

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));

        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.play();

        gerenciadorInput = new GerenciadorInput();

        initGame();
    }

    private void initGame() {
        player = new PlayerShip(playerTex, playerLeftTex, playerRightTex, 3.5f, 0.2f, gerenciadorInput);

        coletaveisList = new Array<>();
        obstaculos = new Array<>();
        activePowerUps = new Array<>();
        activeShields = new Array<>();

        spawnTimer = 0;
        score = 0;
        lives = MAX_LIVES;
        bgNearY = 0f;
        tempoJogo = 0f;
        pauseOption = 0;
    }

    @Override
    public void render(float delta) {
        switch (gameState) {
            case WAITING:
                if (pressedAny(Input.Keys.SPACE, Input.Keys.ENTER, Input.Keys.NUMPAD_ENTER)) {
                    gameState = GameState.PLAYING;
                }
                break;

            case PLAYING:
                if (pressedAny(Input.Keys.ESCAPE, Input.Keys.P)) {
                    pauseGame();
                    break;
                }

                tempoJogo += delta;
                gerenciadorInput.update(delta);
                updateParallax(delta);
                player.update(delta);
                spawnObjects(delta);
                updateGameObjects(delta);

                if (score >= SCORE_VITORIA) {
                    gameState = GameState.WIN;
                }
                break;

            case PAUSED:
                updatePauseInput();
                break;

            case GAME_OVER:
                if (pressedAny(Input.Keys.SPACE, Input.Keys.ENTER, Input.Keys.NUMPAD_ENTER)) {
                    irParaTelaFinal();
                    return;
                }
                break;

            case WIN:
                if (pressedAny(Input.Keys.SPACE, Input.Keys.ENTER, Input.Keys.NUMPAD_ENTER, Input.Keys.ESCAPE)) {
                    irParaTelaFinal();
                    return;
                }
                break;
        }

        drawGame();
    }

    private boolean pressedAny(int... keys) {
        for (int key : keys) {
            if (Gdx.input.isKeyJustPressed(key)) {
                return true;
            }
        }

        return false;
    }

    private void drawGame() {
        ScreenUtils.clear(Color.BLACK);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        spriteBatch.begin();

        spriteBatch.draw(bgNearTex, 0, bgNearY, 8, 5);
        spriteBatch.draw(bgNearTex, 0, bgNearY + 5f, 8, 5);

        if (gameState != GameState.WAITING) {
            for (coletaveis c : coletaveisList) {
                c.draw(spriteBatch);
            }

            for (Obstaculo o : obstaculos) {
                o.draw(spriteBatch);
            }

            for (PowerUp p : activePowerUps) {
                p.draw(spriteBatch);
            }

            for (Shield s : activeShields) {
                s.draw(spriteBatch);
            }

            player.draw(spriteBatch);
        }

        spriteBatch.end();

        hudCamera.update();
        spriteBatch.setProjectionMatrix(hudCamera.combined);

        spriteBatch.begin();
        drawHUD();
        spriteBatch.end();
    }

    private void pauseGame() {
        gameState = GameState.PAUSED;
        pauseOption = 0;

        if (music != null) {
            music.pause();
        }
    }

    private void resumeGame() {
        gameState = GameState.PLAYING;

        if (music != null) {
            music.play();
        }
    }

    private void updatePauseInput() {
        if (pressedAny(Input.Keys.ESCAPE, Input.Keys.P)) {
            resumeGame();
            return;
        }

        if (pressedAny(Input.Keys.UP, Input.Keys.W, Input.Keys.LEFT, Input.Keys.A)) {
            movePauseOptionUp();
            return;
        }

        if (pressedAny(Input.Keys.DOWN, Input.Keys.S, Input.Keys.RIGHT, Input.Keys.D)) {
            movePauseOptionDown();
            return;
        }

        if (pressedAny(Input.Keys.ENTER, Input.Keys.NUMPAD_ENTER, Input.Keys.SPACE)) {
            executePauseOption();
            return;
        }

        if (Gdx.input.justTouched()) {
            handlePauseMouseClick();
        }
    }

    private void movePauseOptionUp() {
        pauseOption--;

        if (pauseOption < 0) {
            pauseOption = 2;
        }
    }

    private void movePauseOptionDown() {
        pauseOption++;

        if (pauseOption > 2) {
            pauseOption = 0;
        }
    }

    private void handlePauseMouseClick() {
        float mouseX = Gdx.input.getX();
        float mouseY = screenH - Gdx.input.getY();

        float pauseAspectRatio = (float) pauseTex.getWidth() / pauseTex.getHeight();

        float imgH = screenH * 0.60f;
        float imgW = imgH * pauseAspectRatio;

        float maxW = screenW * 0.90f;
        if (imgW > maxW) {
            imgW = maxW;
            imgH = imgW / pauseAspectRatio;
        }

        float imgX = (screenW - imgW) / 2f;
        float imgY = (screenH - imgH) / 2f;

        float buttonX = imgX + imgW * 0.18f;
        float buttonW = imgW * 0.64f;
        float buttonH = imgH * 0.12f;

        float playY = imgY + imgH * 0.485f;
        float menuY = imgY + imgH * 0.345f;
        float sairY = imgY + imgH * 0.205f;

        if (isMouseInside(mouseX, mouseY, buttonX, playY, buttonW, buttonH)) {
            pauseOption = 0;
            executePauseOption();
            return;
        }

        if (isMouseInside(mouseX, mouseY, buttonX, menuY, buttonW, buttonH)) {
            pauseOption = 1;
            executePauseOption();
            return;
        }

        if (isMouseInside(mouseX, mouseY, buttonX, sairY, buttonW, buttonH)) {
            pauseOption = 2;
            executePauseOption();
        }
    }

    private boolean isMouseInside(float mouseX, float mouseY, float x, float y, float w, float h) {
        return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
    }

    private void executePauseOption() {
        switch (pauseOption) {
            case 0:
                resumeGame();
                break;

            case 1:
                if (music != null) music.stop();
                Gdx.input.setInputProcessor(null);
                jogo.setScreen(new MenuPrincipal(jogo));
                break;

            case 2:
                Gdx.app.exit();
                break;
        }
    }
    private void irParaTelaFinal() {
        if (music != null) music.stop();
        Gdx.input.setInputProcessor(null);
        jogo.setScreen(new TelaFinal(jogo, score, SCORE_VITORIA, tempoJogo));
    }

    private void updateParallax(float delta) {
        bgNearY -= VELOCIDADE_FASE3 * delta;

        if (bgNearY <= -5f) {
            bgNearY += 5f;
        }
    }

    private void drawHUD() {
        float imgW = screenW * 0.60f;
        float imgH = imgW * 0.375f;
        float imgX = (screenW - imgW) / 2f;
        float imgY = (screenH - imgH) / 2f;

        switch (gameState) {
            case WAITING:
                spriteBatch.draw(startGameTex, imgX, imgY, imgW, imgH);
                break;

            case PLAYING:
                drawScore();
                drawLiveHearts();
                break;

            case PAUSED:
                drawScore();
                drawLiveHearts();
                drawPauseMenu();
                break;

            case GAME_OVER:
                drawScore();
                drawLiveHearts();
                spriteBatch.draw(gameOverTex, imgX, imgY, imgW, imgH);
                break;

            case WIN:
                drawScore();
                drawLiveHearts();
                spriteBatch.draw(winTex, imgX, imgY, imgW, imgH);
                break;
        }
    }

    private void drawPauseMenu() {
        float pauseAspectRatio = (float) pauseTex.getWidth() / pauseTex.getHeight();

        float imgH = screenH * 0.60f;
        float imgW = imgH * pauseAspectRatio;

        float maxW = screenW * 0.90f;
        if (imgW > maxW) {
            imgW = maxW;
            imgH = imgW / pauseAspectRatio;
        }

        float imgX = (screenW - imgW) / 2f;
        float imgY = (screenH - imgH) / 2f;

        spriteBatch.draw(pauseTex, imgX, imgY, imgW, imgH);

        float arrowX = imgX + imgW * 0.10f;
        float arrowY;

        if (pauseOption == 0) {
            arrowY = imgY + imgH * 0.52f;
        } else if (pauseOption == 1) {
            arrowY = imgY + imgH * 0.38f;
        } else {
            arrowY = imgY + imgH * 0.24f;
        }

        pauseFont.getData().setScale(3.0f);
        pauseFont.setColor(Color.WHITE);
        pauseFont.draw(spriteBatch, ">", arrowX, arrowY);
    }

    private void drawScore() {
        font.setColor(Color.RED);
        font.getData().setScale(1.0f);
        font.draw(spriteBatch, "Fase 3 - Score: " + score, 20, screenH - 20);
    }

    private void drawLiveHearts() {
        float heartSize = 60f;
        float margin = 10f;
        float startX = screenW - (lives * (heartSize + margin)) - 20;
        float y = screenH - heartSize - 10;

        for (int i = 0; i < lives; i++) {
            spriteBatch.draw(heartTex, startX + i * (heartSize + margin), y, heartSize, heartSize);
        }
    }

    private void spawnObjects(float delta) {
        spawnTimer += delta;

        if (spawnTimer < 0.7f) {
            return;
        }

        spawnTimer = 0;

        float x = MathUtils.random(LEFT_WALL, RIGHT_WALL - 1.0f);
        float roll = MathUtils.random();

        if (roll < 0.55f) {
            spawnColetavel(x);
        } else {
            spawnObstaculo(x);
        }

        if (MathUtils.randomBoolean(0.08f)) {
            float px = MathUtils.random(LEFT_WALL, RIGHT_WALL - 0.8f);
            activePowerUps.add(new Fase3PowerUp(powerTex, px, 5f));
        }

        if (MathUtils.randomBoolean(0.012f)) {
            float sx = MathUtils.random(LEFT_WALL, RIGHT_WALL - 0.8f);
            activeShields.add(new Fase3Shield(shieldTex, sx, 5f));
        }
    }

    private void spawnColetavel(float x) {
        float roll = MathUtils.random();

        if (roll < 0.5f) {
            coletaveisList.add(new coletaveis(coletavel1Tex, x, 5f, 0.8f, 0, -VELOCIDADE_FASE3));
        } else if (roll < 0.8f) {
            coletaveisList.add(new coletaveis(coletavel2Tex, x, 5f, 0.5f, 0, -VELOCIDADE_FASE3));
        } else {
            coletaveisList.add(new coletaveis(coletavel3Tex, x, 5f, 0.65f, 0, -VELOCIDADE_FASE3));
        }
    }

    private void spawnObstaculo(float x) {
        if (MathUtils.randomBoolean()) {
            obstaculos.add(new Obstaculo(obstaculo1Tex, x, 5f, 0.8f, 0, -VELOCIDADE_FASE3));
        } else {
            obstaculos.add(new Obstaculo(obstaculo2Tex, x, 5f, 0.9f, 0, -VELOCIDADE_FASE3));
        }
    }

    private void updateGameObjects(float delta) {
        Rectangle playerBounds = player.getBounds();

        for (int i = coletaveisList.size - 1; i >= 0; i--) {
            coletaveis c = coletaveisList.get(i);
            c.update(delta);

            Rectangle cb = c.getBounds();

            if (cb.overlaps(playerBounds)) {
                score++;
                dropSound.play();
                coletaveisList.removeIndex(i);
                continue;
            }

            if (cb.y < -1f) {
                coletaveisList.removeIndex(i);
            }
        }

        for (int i = obstaculos.size - 1; i >= 0; i--) {
            Obstaculo o = obstaculos.get(i);
            o.update(delta);

            Rectangle ob = o.getBounds();

            if (ob.overlaps(playerBounds)) {
                obstaculos.removeIndex(i);

                if (!player.isInvincible()) {
                    lives--;
                    player.hit();

                    if (lives <= 0) {
                        gameState = GameState.GAME_OVER;
                    }
                }

                continue;
            }

            if (ob.y < -1f) {
                obstaculos.removeIndex(i);
            }
        }

        for (int i = activePowerUps.size - 1; i >= 0; i--) {
            PowerUp p = activePowerUps.get(i);
            p.update(delta);

            Rectangle pb = p.getBounds();

            if (pb.overlaps(playerBounds)) {
                dropSound.play();
                lives = Math.min(lives + 1, MAX_LIVES);
                activePowerUps.removeIndex(i);
            } else if (pb.y < -1f) {
                activePowerUps.removeIndex(i);
            }
        }

        for (int i = activeShields.size - 1; i >= 0; i--) {
            Shield s = activeShields.get(i);
            s.update(delta);

            Rectangle sb = s.getBounds();

            if (sb.overlaps(playerBounds)) {
                dropSound.play();
                player.activateShield();
                activeShields.removeIndex(i);
            } else if (sb.y < -1f) {
                activeShields.removeIndex(i);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);

        screenW = width;
        screenH = height;

        hudCamera.setToOrtho(false, width, height);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();

        if (playerTex != null) playerTex.dispose();
        if (playerLeftTex != null) playerLeftTex.dispose();
        if (playerRightTex != null) playerRightTex.dispose();

        if (bgNearTex != null) bgNearTex.dispose();

        if (coletavel1Tex != null) coletavel1Tex.dispose();
        if (coletavel2Tex != null) coletavel2Tex.dispose();
        if (coletavel3Tex != null) coletavel3Tex.dispose();

        if (obstaculo1Tex != null) obstaculo1Tex.dispose();
        if (obstaculo2Tex != null) obstaculo2Tex.dispose();

        if (powerTex != null) powerTex.dispose();
        if (shieldTex != null) shieldTex.dispose();

        if (startGameTex != null) startGameTex.dispose();
        if (gameOverTex != null) gameOverTex.dispose();
        if (heartTex != null) heartTex.dispose();
        if (winTex != null) winTex.dispose();
        if (pauseTex != null) pauseTex.dispose();

        if (dropSound != null) dropSound.dispose();
        if (music != null) music.dispose();
        if (font != null) font.dispose();
        if (pauseFont != null) pauseFont.dispose();
        if (gerenciadorInput != null) gerenciadorInput.dispose();
    }

    private static class Fase3PowerUp extends PowerUp {
        public Fase3PowerUp(Texture texture, float x, float y) {
            super(texture, x, y);
        }

        @Override
        public void update(float delta) {
            sprite.translateY(-VELOCIDADE_FASE3 * delta);
        }
    }

    private static class Fase3Shield extends Shield {
        public Fase3Shield(Texture texture, float x, float y) {
            super(texture, x, y);
        }

        @Override
        public void update(float delta) {
            sprite.translateY(-VELOCIDADE_FASE3 * delta);
        }
    }
}