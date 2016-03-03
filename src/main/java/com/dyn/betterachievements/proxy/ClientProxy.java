package com.dyn.betterachievements.proxy;

import com.dyn.betterachievements.handler.ConfigHandler;
import com.dyn.betterachievements.handler.GuiOpenHandler;
import com.dyn.betterachievements.handler.SaveHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerHandlers()
    {
        MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
        MinecraftForge.EVENT_BUS.register(new SaveHandler());
    }

    @Override
    public void initConfig(File file)
    {
        ConfigHandler.init(file);
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
    }
}
