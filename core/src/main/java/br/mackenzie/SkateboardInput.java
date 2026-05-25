package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;

public class SkateboardInput implements InputProvider {

    public static boolean MODO_SIMULACAO = true;

    public static int    EIXO_HORIZONTAL = 0;
    public static float  ZONA_MORTA      = 0.3f;
    public static int    BOTAO_ESQUERDA  = 14;
    public static int    BOTAO_DIREITA   = 15;

    public static boolean MODO_DEBUG = false;

    private static final int   MAX_EIXOS       = 8;
    private static final int   MAX_BOTOES      = 20;
    private static final float DEBUG_INTERVALO = 0.5f;

    private Controller controle;
    private float debugTimer = 0f;

    public SkateboardInput() {
        if (MODO_SIMULACAO) {
            Gdx.app.log("Skate", "MODO SIMULACAO ativo — A = esquerda, D = direita");
        } else {
            buscarControle();
        }
    }

    private void buscarControle() {
        if (Controllers.getControllers().size > 0) {
            controle = Controllers.getControllers().first();
            Gdx.app.log("Skate", "Controle encontrado: \"" + controle.getName() + "\"");
        } else {
            Gdx.app.log("Skate", "Nenhum controle conectado. Usando apenas teclado.");
        }
    }

    @Override
    public boolean isMovingLeft() {
        if (MODO_SIMULACAO) {
            return Gdx.input.isKeyPressed(Input.Keys.A);
        }
        if (controle == null) return false;
        float eixo = controle.getAxis(EIXO_HORIZONTAL);
        return eixo < -ZONA_MORTA || controle.getButton(BOTAO_ESQUERDA);
    }

    @Override
    public boolean isMovingRight() {
        if (MODO_SIMULACAO) {
            return Gdx.input.isKeyPressed(Input.Keys.D);
        }
        if (controle == null) return false;
        float eixo = controle.getAxis(EIXO_HORIZONTAL);
        return eixo > ZONA_MORTA || controle.getButton(BOTAO_DIREITA);
    }

    @Override
    public void update(float delta) {
        if (MODO_SIMULACAO) return;

        if (controle == null) {
            buscarControle();
            return;
        }

        if (!MODO_DEBUG) return;

        debugTimer += delta;
        if (debugTimer < DEBUG_INTERVALO) return;
        debugTimer = 0f;

        StringBuilder sb = new StringBuilder("[SKATE DEBUG] ");

        for (int i = 0; i < MAX_EIXOS; i++) {
            float v = controle.getAxis(i);
            if (Math.abs(v) > 0.15f) {
                sb.append("Eixo").append(i).append("=").append(String.format("%.2f", v)).append("  ");
            }
        }

        boolean algumBotao = false;
        for (int i = 0; i < MAX_BOTOES; i++) {
            if (controle.getButton(i)) {
                sb.append("Btn").append(i).append("  ");
                algumBotao = true;
            }
        }

        if (algumBotao || sb.length() > 16) {
            Gdx.app.log("Skate", sb.toString());
        }
    }

    public boolean isConectado() {
        return MODO_SIMULACAO || controle != null;
    }

    @Override
    public void dispose() {}
}
