package com.sebo.wynnmarketserver.web.restControllers;

import com.sebo.wynnmarketserver.objects.AllItemArray;
import com.sebo.wynnmarketserver.objects.AuctionItem;
import com.sebo.wynnmarketserver.objects.ItemArray;
import com.sebo.wynnmarketserver.utils.AutoClicker;
import com.sebo.wynnmarketserver.utils.MultithreadVariables;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class ItemReceiver {
    int secondPage = 0;
    Thread clickerThread = new Thread(new AutoClicker());
    @PostMapping("/items")
    public void receiveItems(HttpEntity<String> httpEntity) {
        if(secondPage == 1){
            ItemArray.clear();
            clickerThread.start();
            System.out.println("Working...");
        }
        secondPage++;
        JSONObject oT = null;
        try {
            oT = new JSONArray(httpEntity.getBody()).getJSONObject(0);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String bannedJson = "{\"stats\":{},\"price\":\"0\",\"name\":\"\",\"rarity\":\"null\"}";
        //System.out.println(oT.toString() + "\n" + bannedJson);
        if(oT.toString().equalsIgnoreCase(bannedJson)){
            MultithreadVariables.clickMouse.getAndSet(false);
            System.out.println("Final page received, updating items");
            writeItems();
            ItemArray.updateLocal();
            secondPage = 0;
            return;
        }
        JSONArray o = null;
        try {
            o = new JSONArray(httpEntity.getBody());
            for (int i = 0; i < o.length(); i++) {
                AuctionItem item = new AuctionItem(o.getJSONObject(i));
                ItemArray.add(item);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public String writeItems() {
        try {
            ItemArray.writeItems();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(ItemArray.getItems().size() + " items written");
        //ItemArray.clear();
        return "File written";
    }

    @GetMapping("/items/{name}/{stat}/{category}/{type}/{avgPct}")
    @ResponseBody
    public ResponseEntity<List<AuctionItem>> sortBy(@PathVariable String name, @PathVariable String stat, @PathVariable String category, @PathVariable String type, @PathVariable String avgPct) {
        ArrayList<String> stats = new ArrayList<>(Arrays.asList(URLDecoder.decode(stat).split(",")));
        return ResponseEntity.ok(ItemArray.sortBy(name, stats, category, type, Boolean.valueOf(avgPct)));
    }
}
