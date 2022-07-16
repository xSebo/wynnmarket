package com.sebo.wynnmarketserver.web.restControllers;

import com.sebo.wynnmarketserver.objects.Item;
import com.sebo.wynnmarketserver.objects.ItemArray;
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
    @PostMapping("/items")
    public void receiveItems(HttpEntity<String> httpEntity) {
        System.out.println("Page received");
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

    @GetMapping("/items")
    public String testPage() {
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
