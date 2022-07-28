package com.sebo.wynnmarketserver.objects;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class ItemArray {
    private static ArrayList<AuctionItem> auctionItems = new ArrayList<>();

    public static void writeItems() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("items.json", "UTF-8");
        writer.print("");
        writer.println("[");
        for (int i = 0; i < auctionItems.size(); i++) {
            String bannedJson = "{\"name\":\"\",\"rarity\":\"null\",\"price\":\"0\",\"category\":\"Unknown\",\"type\":\"Unknown\",\"stats\":{}}";
            if (auctionItems.get(i).genRawJson().equals(bannedJson)) {
                continue;
            }
            writer.println(new Gson().toJson(auctionItems.get(i)));
            if (i != auctionItems.size() - 1) {
                writer.println(",");
            }
        }
        writer.println("]");
        writer.close();
    }

    public static ArrayList<AuctionItem> getItems() {
        return auctionItems;
    }

    public static void clear() {
        auctionItems.clear();
        try {
            PrintWriter writer = new PrintWriter("items.json", "UTF-8");
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void add(AuctionItem i) {
        auctionItems.add(i);
    }

    public static String updateLocal() {
        try {
            String wholeFile = new String(Files.readAllBytes(Paths.get("items.json")), StandardCharsets.UTF_8);
            JSONArray a = new JSONArray(wholeFile);
            for (int i = 0; i < a.length(); i++) {
                AuctionItem item = new AuctionItem(a.getJSONObject(i));
                if(item.getStats().size() != 0){
                    ItemArray.add(item);
                }
            }
            return "HashMap for auction items updated with " + ItemArray.auctionItems.size() + " items";
        } catch (IOException e) {
            return "No pre-existing items found";
        } catch (JSONException e) {
            e.printStackTrace();
            return "JSONException";
        }
    }

    private static ArrayList<AuctionItem> sortByStats(ArrayList<AuctionItem> items,ArrayList<String> stat){
        Map<AuctionItem,ArrayList<Double>> allIndeces = new HashMap<>();
        for(String statI:stat){
            Collections.sort(items, new Comparator<>() {
                @Override
                public int compare(AuctionItem o1, AuctionItem o2) {
                    double stat1 = 0;
                    double stat2 = 0;

                    try {
                        stat1 = o1.getStat(statI).get(1);
                    } catch (NullPointerException e) {

                    }
                    try {
                        stat2 = o2.getStat(statI).get(1);
                    } catch (NullPointerException e) {

                    }
                    if (statI.equalsIgnoreCase("spellCostPct1") ||
                            statI.equalsIgnoreCase("spellCostPct2") ||
                            statI.equalsIgnoreCase("spellCostPct3") ||
                            statI.equalsIgnoreCase("spellCostPct4") ||
                            statI.equalsIgnoreCase("spellCostRaw1") ||
                            statI.equalsIgnoreCase("spellCostRaw2") ||
                            statI.equalsIgnoreCase("spellCostRaw3") ||
                            statI.equalsIgnoreCase("spellCostRaw4")) {
                        return Double.compare(stat1, stat2);
                    }
                    return Double.compare(stat2, stat1);
                }
            });
            for(int j = 0; j<items.size(); j++){
                Double doubleJ = Double.valueOf(j);
                try{
                    allIndeces.get(items.get(j)).add(doubleJ);
                }catch(NullPointerException e){
                    allIndeces.put(items.get(j),new ArrayList<>(Arrays.asList(doubleJ)));
                }
            }
        }
        allIndeces.forEach((k,v) -> {
            ArrayList<Double> mean = new ArrayList<>(Arrays.asList(v.stream().mapToDouble(a -> a).average().getAsDouble()));
            allIndeces.get(k).clear();
            allIndeces.get(k).addAll(mean);
        });

        List<Map.Entry<AuctionItem, ArrayList<Double>>> list = new ArrayList<>(allIndeces.entrySet());
        Collections.sort(list, Comparator.comparingDouble(o -> o.getValue().get(0)));
        ArrayList<AuctionItem> sorted = new ArrayList<>();
        list.forEach(o -> sorted.add(o.getKey()));

        System.out.println("Sorted " + items.size() + " items by " + stat);
        return sorted;
    }

    private static ArrayList<AuctionItem> sortByAvgPct(ArrayList<AuctionItem> items){
        Collections.sort(items, Comparator.comparingDouble(o -> o.getAvgStatPct()));

        System.out.println("Sorted " + items.size() + " items by average stat percentage");
        return items;
    }

    public static ArrayList<AuctionItem> sortBy(String name, ArrayList<String> stat, String category, String type, boolean avgPct) {
        ArrayList<AuctionItem> sorted = new ArrayList<>();
        if (category.equalsIgnoreCase("armour")) {
            category = "armor";
        }
        boolean nullCat = false;
        boolean nullType = false;
        boolean nullName = false;
        boolean nullStat = false;
        try{
            nullStat = stat.get(0).equalsIgnoreCase("null");
        }catch(NullPointerException e){
        }
        if (category.equalsIgnoreCase("null")) {
            nullCat = true;
        }
        if (type.equalsIgnoreCase("null")) {
            nullType = true;
        }
        if (name.equalsIgnoreCase("null")) {
            nullName = true;
        }

        for (AuctionItem i : auctionItems) {
            if (nullCat) {
                category = i.getCategory();
            }
            if (nullType) {
                type = i.getType();
            }
            if (nullName) {
                name = i.getName();
            }
            if (i.getRarity().equals("null") || i.getRarity().equals("Crafted")
                    || i.getName().contains("Unidentified")) {
                continue;
            }
            if ((i.getCategory().equalsIgnoreCase(category) && i.getType().equalsIgnoreCase(type) && i.getName().equalsIgnoreCase(name))) {
                if(stat.size() != 0 && !nullStat){
                    List<String> containedStats = new ArrayList<>();
                    for(String s : stat){
                        if(i.getStats().containsKey(s)){
                            containedStats.add(s);
                        }
                    }
                    if(containedStats.size() == stat.size()){
                        sorted.add(i);
                    }
                }else{
                    sorted.add(i);
                }
            }
        }
        try {
            if (stat.size() != 0 && !nullStat) {
                sortByStats(sorted, stat);
                if(avgPct){
                    sortByAvgPct(sorted);
                }
            }else{
                sortByAvgPct(sorted);
            }
        } catch (Exception e) {
            System.out.println("Error sorting by " + stat);
            e.printStackTrace();
        }
        return sorted;
    }

    public static String asJson() {
        return genJson(auctionItems);
    }

    public static String asJson(ArrayList<AuctionItem> tempAuctionItems) {
        return genJson(tempAuctionItems);
    }

    private static String genJson(ArrayList<AuctionItem> tempAuctionItems) {
        int threads = 200;
        Gson gson = new Gson();
        Executor executor = Executors.newFixedThreadPool(threads);
        CompletionService<String> completionService =
                new ExecutorCompletionService<>(executor);
        List<String> jsonList = new ArrayList<>();
        int totalSize = tempAuctionItems.size();
        for (int i = 0; i < totalSize; ) {
            for (int j = 0; j < threads; j++, i++) {
                int finalI = i;
                completionService.submit(() -> gson.toJson(tempAuctionItems.get(finalI)));
            }
            int received = 0;
            boolean errors = false;
            while (received < threads && !errors) {
                Future<String> resultFuture = null;
                try {
                    resultFuture = completionService.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    String result = resultFuture.get();
                    received++;
                    jsonList.add(result);
                } catch (IndexOutOfBoundsException | InterruptedException | ExecutionException e) {
                    break;
                }
            }
        }

        System.out.println(jsonList.size() + " items converted to JSON");

        String json = null;
        json = new JSONArray(jsonList).toString();
        return json;
    }


}
