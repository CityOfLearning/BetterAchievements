package com.dyn.betterachievements.reference;

public class Reference
{
    // User friendly version of our mods name.
    public static final String NAME = "Better Achievements";

    // Internal mod name used for reference purposes and resource gathering.
    public static final String ID = "BetterAchievements";
    public static final String RESOURCE_ID = ID.toLowerCase();

    // Main version information that will be displayed in mod listing and for other purposes.
    public static final String V_MAJOR = "@MAJOR@";
    public static final String V_MINOR = "@MINOR@";
    public static final String V_REVIS = "@REVIS@";
    public static final String VERSION_FULL = V_MAJOR + "." + V_MINOR + "." + V_REVIS;

    // proxy info
    public static final String SERVER_PROXY = "com.dyn.betterachievements.proxy.CommonProxy";
    public static final String CLIENT_PROXY = "com.dyn.betterachievements.proxy.ClientProxy";
    public static final String MOD_GUI_FACTORY = "com.dyn.betterachievements.gui.ModGuiFactory";
}
