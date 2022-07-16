package com.sebo.wynnmarket.events;

import com.sebo.wynnmarket.HttpItemHandler;
import com.sebo.wynnmarket.WynnMarket;
import com.sebo.wynnmarket.auctionobjects.Item;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;


@Mod.EventBusSubscriber(modid = WynnMarket.MODID)
public class ModEvents {

    private static GuiScreen GUI = null;
    private static String lastFirst = "";


    @SubscribeEvent
    public static void updateWorld(TickEvent.PlayerTickEvent event) {
        if (GUI != null) {
            if (GUI instanceof GuiContainer) {
                try {
                    GuiContainer container = ((GuiContainer) GUI);
                    NonNullList<ItemStack> slots = container.inventorySlots.getInventory();
                    boolean isAh = false;
                    String itemName = slots.get(8).getDisplayName().toLowerCase();
                    if (itemName.contains("filter frontpage")) {
                        isAh = true;
                    }
                    if (!lastFirst.equalsIgnoreCase(slots.get(0).getDisplayName())) {
                        ArrayList<Item> items = new ArrayList<Item>();
                        lastFirst = slots.get(0).getDisplayName();
                        int counter = 0;
                        if (isAh) {
                            for (int i = 0; i < slots.size(); i++) {
                                if (i == 52) {
                                    break; //End of AH slots
                                }
                                if (!(counter == 7 || counter == 8)) { // Skips auction option and blank slots
                                    ItemStack temp = slots.get(i);
                                    try {
                                        String tempName = temp.getDisplayName().toLowerCase().substring(0, temp.getDisplayName().length() - 1);
                                    } catch (Exception e) {
                                    }
                                    if(StringUtils.stripControlCodes(temp.getDisplayName()).equalsIgnoreCase("")){
                                        return;
                                    }
                                    items.add(new Item(temp));
                                }
                                if (counter == 8) {
                                    counter = 0;
                                } else {
                                    counter++;
                                }
                            }
                            HttpItemHandler sendItems = new HttpItemHandler(items);
                            System.out.println("Sending items to server");
                            sendItems.run();

                        }
                    }
                }catch (Exception e){
                    //e.printStackTrace();
                }
                GUI = Minecraft.getMinecraft().currentScreen;
            }
        }
    }


    @SubscribeEvent
    public static void openChest(GuiOpenEvent event) {
        GUI = event.getGui();
    }
}
