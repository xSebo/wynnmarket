package com.sebo.wynnmarketserver;

import com.sebo.wynnmarketserver.objects.AllItemArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.IOException;

@SpringBootApplication
public class WynnmarketserverApplication {

	public static void main(String[] args) {
		JSONParser parser = new JSONParser();
		try {
			JSONArray a = (JSONArray) parser.parse(new FileReader("allItems.json"));
			for(Object o:a){
				String[] categoryType = {(String) ((JSONObject)o).get("category"),(String) ((JSONObject)o).get("type")};
				AllItemArray.addItem((String) ((JSONObject)o).get("name"), categoryType);
			}
			System.out.println(AllItemArray.allItems.size());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		SpringApplication.run(WynnmarketserverApplication.class, args);
	}

}
