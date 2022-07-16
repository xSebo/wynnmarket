package com.sebo.wynnmarketserver.web.restControllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashSet;

@RestController
public class ItemDBUpdater {

    @GetMapping("/getItems")
    public String fetchItems() throws IOException, JSONException {
        String dbUrl = "https://api.wynncraft.com/public_api.php?action=itemDB&search=";
//        String alphabet = "aeiouy";
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        PrintWriter writer = new PrintWriter("allItems.json", "UTF-8");
        writer.print("");
        writer.println("[");
        HashSet<String> hashSet= new HashSet<>();
        for(int i = 0; i < alphabet.length(); i++) {

            String urlStr = dbUrl + alphabet.charAt(i);
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                JSONArray json = new JSONObject(response.toString()).getJSONArray("items");
                for(int j = 0; j < json.length(); j++) {
                    hashSet.add(json.getJSONObject(j).toString());
                    if(i == alphabet.length()-1 && j == json.length()-1) {
                    } else {
                    }
                }

            } else {
                System.out.println("Failed : HTTP error code : " + responseCode);
            }
        }
        String[] jOA = hashSet.toArray(new String[hashSet.size()]);
        for(int i = 0; i < hashSet.size(); i++) {
            writer.println(jOA[i].toString());
            if(i != hashSet.size()-1) {
                writer.println(",");
            }
        }
        writer.println("]");
        writer.close();
        System.out.println("Done");
        return "DB Updated";
    }
}
