package com.dyn.betterachievements.api.components.achievement;

public interface ICustomIconRenderer
{
    /**
     * Custom icon rendering of the {@link net.minecraft.stats.Achievement} on the {@link com.dyn.betterachievements.gui.GuiBetterAchievements}
     *
     * @param xPos  left of the achievement icon
     * @param yPos  top of the achievement icon
     */
    void renderIcon(int xPos, int yPos);
}
