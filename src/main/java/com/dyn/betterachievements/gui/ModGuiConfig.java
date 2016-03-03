package com.dyn.betterachievements.gui;

import com.dyn.betterachievements.handler.ConfigHandler;
import com.dyn.betterachievements.reference.Reference;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraft.client.gui.GuiScreen;

public class ModGuiConfig extends GuiConfig
{
    public ModGuiConfig(GuiScreen guiScreen)
    {
        super(guiScreen,
                ConfigHandler.getConfigElements(),
                Reference.ID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
    }
}
