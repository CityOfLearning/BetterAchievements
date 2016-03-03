package com.dyn.betterachievements.handler;

import com.dyn.betterachievements.gui.GuiAchievementsOld;
import com.dyn.betterachievements.gui.GuiBetterAchievements;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class GuiOpenHandler
{
    private static Field prevScreen, currentPage;

    static
    {
        try
        {
            prevScreen = ReflectionHelper.findField(GuiAchievements.class, "parentScreen", "field_146562_a");
            prevScreen.setAccessible(true);
            currentPage = ReflectionHelper.findField(GuiAchievements.class, "currentPage");
            currentPage.setAccessible(true);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) //we can probably remove this once we have it set to the key listener
    {
        // Do nothing if I want to open the old GUI
        if (event.gui instanceof GuiAchievementsOld)
            return;

        if (event.gui instanceof GuiAchievements)
        {
            event.setCanceled(true);
            try
            {
                Minecraft.getMinecraft().displayGuiScreen(new GuiBetterAchievements((GuiScreen)prevScreen.get(event.gui), (Integer)currentPage.get(event.gui) + 1));
            } catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
