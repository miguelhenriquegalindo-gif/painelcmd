package com.painelcmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Comandos do painel. Rodam todos no cliente, então funcionam com cheats desligados.
 * Pra criar um comando novo, é só adicionar um CMDS.put(...) no bloco static.
 */
public final class Comandos {

    public static final List<String> LOG = new ArrayList<>();
    private static final Map<String, Consumer<String[]>> CMDS = new LinkedHashMap<>();

    static {
        Consumer<String[]> nv = args -> {
            boolean novo;
            if (args.length > 0 && (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("ligar"))) {
                novo = true;
            } else if (args.length > 0 && (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("desligar"))) {
                novo = false;
            } else {
                novo = !NightVision.isEnabled();
            }
            NightVision.set(novo);
            log("Visão noturna " + (novo ? "LIGADA" : "desligada") + " (sem partículas).");
        };
        CMDS.put("nv", nv);
        CMDS.put("visao", nv);
        CMDS.put("visaonoturna", nv);

        CMDS.put("limpar", args -> LOG.clear());

        CMDS.put("ajuda", args -> {
            log("Comandos:");
            log("nv [on|off] - visão noturna sem partículas");
            log("limpar - limpa o painel");
            log("ajuda - mostra esta lista");
            log("Esc ou a tecla do painel fecha.");
        });
    }

    private Comandos() {}

    public static void log(String s) {
        LOG.add(s);
        while (LOG.size() > 100) LOG.remove(0);
    }

    public static void executar(String linha) {
        log("> " + linha);
        String[] partes = linha.trim().split("\\s+");
        String nome = partes[0].toLowerCase(Locale.ROOT);
        String[] args = Arrays.copyOfRange(partes, 1, partes.length);
        Consumer<String[]> cmd = CMDS.get(nome);
        if (cmd == null) {
            log("Comando desconhecido. Digite: ajuda");
        } else {
            cmd.accept(args);
        }
    }
}
