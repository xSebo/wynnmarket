package com.sebo.wynnmarket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.sebo.wynnmarket.auctionobjects.Item;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HttpItemHandler extends Thread{
    private ArrayList<Item> localItems;

    public HttpItemHandler(ArrayList<Item> items){
        this.localItems = items;

    }
    public void run(){
        String finalJson = "[";
        for(Item i:localItems) {
            String jsonMap = "{\"name\":\""+i.getName()+
                            "\",\"price\":\""+i.getPrice()+
                            "\",\"stats\":" +
                    new Gson().toJson(i.getStats()) + "}"; // {"statName":value}
            finalJson+=jsonMap+",";
        }
        finalJson+="]";
        URL url = null;
        try {
            url = new URL("http://localhost:8080/items");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = finalJson.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
            }catch (IOException e){
                e.printStackTrace();
            }
            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Success");
            } else {
                System.out.println("Failed: " + responseCode);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
