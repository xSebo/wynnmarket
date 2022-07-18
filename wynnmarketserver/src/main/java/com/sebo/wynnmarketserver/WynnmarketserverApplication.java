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
		AllItemArray.updateLocal();
		if(AllItemArray.allItems.size() == 0){
			System.out.println("No stored items, updating from Wynncraft");
			System.out.println(AllItemArray.updateAll());
		}
		else{
			System.out.println("HashMap updated with "+AllItemArray.allItems.size()+" items");
		}
		SpringApplication.run(WynnmarketserverApplication.class, args);
	}

}
