package com.sebo.wynnmarketserver;

import com.sebo.wynnmarketserver.objects.AllItemArray;
import com.sebo.wynnmarketserver.objects.ItemArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class WynnmarketserverApplication {

	public static void main(String[] args) {
		System.out.println(Runtime.version().toString());
		System.setProperty("java.awt.headless", "false");
		if(AllItemArray.updateLocal().equalsIgnoreCase("Error reading file")){
			System.out.println("No stored items, updating from Wynncraft");
			System.out.println(AllItemArray.updateAll());
		}
		else{
			AllItemArray.updateLocal();
			System.out.println("HashMap for all items updated with "+AllItemArray.allItems.size()+" items");
		}

		System.out.println(ItemArray.updateLocal());
		SpringApplication.run(WynnmarketserverApplication.class, args);
	}

}
