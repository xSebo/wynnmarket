package com.sebo.wynnmarketserver.web.restControllers;

import com.sebo.wynnmarketserver.objects.AllItemArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.io.*;
@RestController
public class ItemDBUpdater {

    @GetMapping("/updateItems")
    public String fetchItems() {
        return AllItemArray.updateAll();
    }
}
