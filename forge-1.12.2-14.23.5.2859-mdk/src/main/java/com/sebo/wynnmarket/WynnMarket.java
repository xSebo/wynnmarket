package com.sebo.wynnmarket;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = WynnMarket.MODID, name = WynnMarket.NAME, version = WynnMarket.VERSION)
public class WynnMarket {
    public static final String MODID = "wynnmarket";
    public static final String NAME = "WynnMarket";
    public static final String VERSION = "1.0";

    public WynnMarket() {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
