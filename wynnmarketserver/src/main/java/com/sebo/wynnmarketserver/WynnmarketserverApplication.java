package com.sebo.wynnmarketserver;

import com.sebo.wynnmarketserver.objects.AllItemArray;
import com.sebo.wynnmarketserver.objects.ItemArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
public class WynnmarketserverApplication {
	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");
		AllItemArray.updateLocal();
		if(AllItemArray.allItems.size() == 0){
			System.out.println("No stored items, updating from Wynncraft");
			System.out.println(AllItemArray.updateAll());
		}
		else{
			System.out.println("HashMap for all items updated with "+AllItemArray.allItems.size()+" items");
		}

		System.out.println(ItemArray.updateLocal());
		SpringApplication.run(WynnmarketserverApplication.class, args);
	}

}
