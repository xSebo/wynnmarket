package com.sebo.wynnmarketserver.beans;

import com.sebo.wynnmarketserver.objects.ItemArray;
import com.sebo.wynnmarketserver.objects.StatMap;
import org.primefaces.PrimeFaces;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.dataview.DataView;
import org.springframework.stereotype.Component;

import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

@Component("indexSearch")
@RequestScoped
public class IndexSearch {

    private String itemName;

    public List<String> getStatNameList() {
        List<String> statList = new ArrayList<>(StatMap.map.keySet());
        statList.sort(String::compareToIgnoreCase);
        return statList;
    }

    public String getStatId(String statName){
        return StatMap.map.get(statName);
    }

    public ArrayList<String> getStats() {
        return stats;
    }

    public void setStats(ArrayList<String> stats) {
        this.stats = stats;
    }

    private ArrayList<String> stats;

    private boolean avgPct;

    public boolean isAvgStatPct() {
        return avgStatPct;
    }

    public void setAvgStatPct(boolean avgStatPct) {
        this.avgStatPct = avgStatPct;
    }

    private boolean avgStatPct;

    public boolean isAvgPct() {
        return avgPct;
    }

    public void setAvgPct(boolean avgPct) {
        this.avgPct = avgPct;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void search(){
        ArrayList<String> statSubmission = new ArrayList<>();
        try {
            for (String stat : stats) {
                statSubmission.add(getStatId(stat));
            }
        }catch (NullPointerException e){
            statSubmission.add("null");
        }
        if(statSubmission.size() == 0){
            statSubmission.add("null");
        }

        ItemArray.sortBy(itemName, statSubmission, "null","null", avgPct, avgStatPct);

        DataView dataView = (DataView) FacesContext.getCurrentInstance().getViewRoot().findComponent("form").findComponent("allItems");
        PrimeFaces.current().ajax().update(dataView);
    }
}