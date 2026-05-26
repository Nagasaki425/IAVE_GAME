Markdown
# ♻️ SWEEPER SYSTEM (LibGDX)

Este é um jogo digital 2D de uma cidade poluída desenvolvido em **Java** utilizando o framework **LibGDX**. O projeto foi construído como parte do currículo acadêmico da **Universidade Presbiteriana Mackenzie**, aplicando conceitos avançados de Programação Orientada a Objetos (POO), gerência de estados, detecção de colisões e integração customizada de periféricos (mecanismo de input baseado em um skate adaptado).

---

## 🚀 Funcionalidades do Jogo

* **Mecânica Core:** Controle um caminhão de coleta de lixo desviando de placas e cones (obstáculos) e coletando "lixos" (coletavéis) para pontuar.
* **Power-ups:**
    * ❤️ **Coração/Drop:** Recupera um ponto de vida (máximo de 3).
    * 🛡️ **Escudo (Shield):** Concede invencibilidade temporária com efeito visual pulsante.
* **Parallax Scrolling:** Fundo infinito simulando o movimento do caminhão pelas ruas sujas.
* **Sistema de Estados (FSM):** Telas de Menu Principal, Gameplay (Fase 1, 2 e 3) com sistema de Pausa integrado, e Tela Final (com estatísticas de tempo e pontuação).
* **Suporte Dual-Input:** Modos de controle intercambiáveis entre teclado convencional e um controle de skate físico customizado.

---

## 🎨 Arquitetura e Engenharia de Software

O projeto segue à risca os padrões de desenvolvimento exigidos em Engenharia de Software e POO:

* **Polimorfismo e Herança:** Uso da classe abstrata `GameObject` para encapsular o comportamento de renderização, atualização de física e cálculo de caixas de colisão (`Rectangle` bounds) para todos os elementos em tela (`PlayerShip`, `coletaveis`, `Obstaculo`, `PowerUp`, `Shield`).
* **Inversão de Dependência:** O controle da nave depende da interface `InputProvider`. Isso desacopla a física do jogador da origem física do comando (seja teclado ou controle de skate).
* **Gerenciamento de Memória:** Implementação rigorosa do método `dispose()` em todas as camadas para evitar *memory leaks* de texturas, fontes e áudios nativos da biblioteca gráfica.

---

## 🕹️ Modos de Controle (Skateboard Input)

O jogo possui uma classe chamada `SkateboardInput` que permite mapear os movimentos com base em um skate físico adaptado com eixos de controle digitais.

* **`MODO_SIMULACAO = true` (Padrão):** O jogo roda simulando o skate através do teclado tradicional (**A** para Esquerda, **D** para Direita).
* **`MODO_SIMULACAO = false`:** O framework passa a varrer as portas USB via `Controllers` do LibGDX para detectar o hardware do skate, mapeando a zona morta (`ZONA_MORTA = 0.3f`) e os eixos horizontais nativos.
* **Modo Debug:** Inclui um registrador (`MODO_DEBUG = true`) em tempo real para capturar os índices exatos de eixos e botões acionados pelo periférico no console.

---

## 📁 Estrutura de Arquivos Principais

```text
br.mackenzie/
│
├── Main.java              # Classe central que estende com.badlogic.gdx.Game (gerencia o ciclo de vida)
├── Fase1.java             # Coração do gameplay (Mecânica de spawn, colisões, HUD, estados e Pausa)
├── InputProvider.java     # Interface base de abstração para os comandos de movimentação
├── GerenciadorInput.java  # Provedor padrão que decide a estratégia ativa de leitura de controles
└── SkateboardInput.java   # Implementação avançada para suporte a hardware físico de Skate e Teclado (A/D)
🛠️ Tecnologias Utilizadas
Linguagem: Java

Framework: LibGDX (v1.12.1 ou superior)

Gerenciador de Dependências: Gradle

Biblioteca de Áudio/Gráficos: APIs nativas do LibGDX (SpriteBatch, FitViewport, Sound, Music)

🎮 Como Jogar (Controles de Teclado)
Menu e Telas
ESPAÇO: Inicia o gameplay nas telas de Start ou reinicia nas telas de Game Over / Vitória.

ESC: Pausa o jogo durante a partida ou retorna/sai dependendo do contexto.

Durante o Gameplay
A / D (ou Setas): Movimentam a nave para a esquerda e para a direita.

ESC: Abre o menu de pausa estilizado em tela.

Use as setas UP / DOWN para navegar entre Continuar, Voltar ao Menu e Sair.

Pressione ENTER ou use o Clique do Mouse diretamente sobre os botões texturizados para selecionar a opção desejada.

⚙️ Pré-requisitos para Execução
Ter o JDK 11 (ou superior) instalado na máquina.

Certificar-se de ter os seguintes recursos de áudio e imagem na pasta assets do seu projeto Java/Gradle:

Imagens/Texturas: player.png, player_left.png, player_right.png, background_near.png, coletavel1.png, coletavel2.png, coletavel3.png, obstaculo1.png, obstaculo2.png, drop.png, shield.png, start_game.png, game_over.png, heart.png, win.png, pause.png.

Áudios: explosao.mp3 (efeitos), music.mp3 (trilha de fundo).

Fontes: pixel_font.fnt (e seu respectivo arquivo de textura).
