package com.painelcmd;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class PainelScreen extends Screen {

    private static final int COR_BORDA = 0xFF9B30FF;   // roxo
    private static final int COR_FUNDO = 0xFF000000;   // preto
    private static final int COR_TEXTO = 0xFFDDDDDD;
    private static final int COR_ECO = 0xFFC58AFF;
    private static final int COR_TITULO = 0xFFFFFFFF;
    private static final int LINHA_ALTURA = 10;

    private static String ultimoComando = "";

    private EditBox entrada;
    private int px, py, pw, ph;

    public PainelScreen() {
        super(Component.literal("Painel de comandos"));
    }

    @Override
    protected void init() {
        pw = Math.min(240, this.width - 20);
        ph = this.height - 12;
        px = this.width - pw - 6;
        py = 6;

        entrada = new EditBox(this.font, px + 8, py + ph - 20, pw - 16, 12, Component.literal("Comando"));
        entrada.setBordered(false);
        entrada.setMaxLength(256);
        entrada.setHint(Component.literal("digite: ajuda"));
        this.addRenderableWidget(entrada);
        this.setInitialFocus(entrada);

        if (Comandos.LOG.isEmpty()) {
            Comandos.log("Painel de comandos. Digite: ajuda");
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; // não pausa o jogo no singleplayer
    }

    // Sem @Override de propósito: se o nome mudar numa versão futura, só volta o escurecimento padrão.
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        // vazio: não escurece/borra o mundo atrás do painel
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (PainelCmdClient.OPEN_KEY.matches(event)) {
            this.onClose();
            return true;
        }
        int k = event.key();
        if (k == GLFW.GLFW_KEY_ENTER || k == GLFW.GLFW_KEY_KP_ENTER) {
            enviar();
            return true;
        }
        if (k == GLFW.GLFW_KEY_UP) {
            entrada.setValue(ultimoComando);
            return true;
        }
        return super.keyPressed(event);
    }

    private void enviar() {
        String texto = entrada.getValue().trim();
        entrada.setValue("");
        if (texto.isEmpty()) return;
        if (texto.startsWith("/")) texto = texto.substring(1);
        ultimoComando = texto;
        Comandos.executar(texto);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        // moldura roxa + fundo preto
        g.fill(px - 2, py - 2, px + pw + 2, py + ph + 2, COR_BORDA);
        g.fill(px, py, px + pw, py + ph, COR_FUNDO);

        g.text(this.font, Component.literal("Painel de comandos"), px + 8, py + 7, COR_TITULO);
        String nv = "Visão noturna: " + (NightVision.isEnabled() ? "ligada" : "desligada");
        g.text(this.font, Component.literal(nv), px + 8, py + 18, COR_BORDA);
        g.fill(px + 6, py + 30, px + pw - 6, py + 31, COR_BORDA);

        // área do log
        int logTopo = py + 36;
        int logBase = py + ph - 32;
        int maxLinhas = Math.max(1, (logBase - logTopo) / LINHA_ALTURA);
        int larguraMax = pw - 16;

        List<String[]> linhas = new ArrayList<>();
        for (String entry : Comandos.LOG) {
            String cor = entry.startsWith("> ") ? "eco" : "txt";
            for (String l : quebrar(entry, larguraMax)) {
                linhas.add(new String[] {l, cor});
            }
        }
        int inicio = Math.max(0, linhas.size() - maxLinhas);
        int y = logTopo;
        for (int i = inicio; i < linhas.size(); i++) {
            String[] l = linhas.get(i);
            g.text(this.font, Component.literal(l[0]), px + 8, y, l[1].equals("eco") ? COR_ECO : COR_TEXTO);
            y += LINHA_ALTURA;
        }

        // caixa do campo de texto
        g.fill(px + 5, py + ph - 25, px + pw - 5, py + ph - 5, COR_BORDA);
        g.fill(px + 6, py + ph - 24, px + pw - 6, py + ph - 6, COR_FUNDO);

        super.extractRenderState(g, mouseX, mouseY, partialTick); // desenha o campo de texto
    }

    private List<String> quebrar(String s, int larguraMax) {
        List<String> out = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        for (String palavra : s.split(" ")) {
            String teste = atual.length() == 0 ? palavra : atual + " " + palavra;
            if (this.font.width(teste) <= larguraMax) {
                atual = new StringBuilder(teste);
            } else {
                if (atual.length() > 0) out.add(atual.toString());
                atual = new StringBuilder(palavra);
            }
        }
        if (atual.length() > 0) out.add(atual.toString());
        return out;
    }
}
