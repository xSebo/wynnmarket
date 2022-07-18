package com.sebo.wynnmarketserver.web.restControllers;

import com.sebo.wynnmarketserver.objects.Item;
import com.sebo.wynnmarketserver.objects.ItemArray;
import com.sebo.wynnmarketserver.utils.AutoClicker;
import com.sebo.wynnmarketserver.utils.MultithreadVariables;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

@RestController
public class ItemReceiver {
    int secondPage = 0;
    Thread clickerThread = new Thread(new AutoClicker());
    @PostMapping("/items")
    public void receiveItems(HttpEntity<String> httpEntity) {
        if(secondPage == 1){
            clickerThread.start();
        }
        else{
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
            secondPage = 0;
            /*
            try {
                Thread.sleep(500);
                MultithreadVariables.clickMouse.getAndSet(true);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

             */

        }
        JSONArray o = null;
        try {
            o = new JSONArray(httpEntity.getBody());
            for (int i = 0; i < o.length(); i++) {
                ItemArray.add(new Item(o.getJSONObject(i)));

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
        ItemArray.clear();
        return "File written";
    }
}
