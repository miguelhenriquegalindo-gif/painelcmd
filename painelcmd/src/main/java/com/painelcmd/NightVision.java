package com.painelcmd;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

/**
 * Visão noturna 100% do lado do cliente: o servidor não sabe de nada, então não precisa de
 * cheats/OP e não mexe em conquistas. Sem partículas e sem ícone.
 */
public final class NightVision {

    private static boolean enabled = false;

    private NightVision() {}

    public static boolean isEnabled() {
        return enabled;
    }

    public static void set(boolean on) {
        enabled = on;
        if (!on) {
            LocalPlayer p = Minecraft.getInstance().player;
            if (p != null) {
                MobEffectInstance cur = p.getEffect(MobEffects.NIGHT_VISION);
                // só remove o efeito infinito (o nosso)
                if (cur != null && cur.isInfiniteDuration()) {
                    p.removeEffect(MobEffects.NIGHT_VISION);
                }
            }
        }
    }

    /** Chamado todo tick; reaplica se morreu, trocou de dimensão, etc. */
    public static void tick(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (!enabled || p == null) return;
        if (p.getEffect(MobEffects.NIGHT_VISION) == null) {
            p.addEffect(new MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    MobEffectInstance.INFINITE_DURATION,
                    0,
                    false,   // ambient
                    false,   // visible  -> sem partículas
                    false)); // showIcon -> sem ícone
        }
    }
}
