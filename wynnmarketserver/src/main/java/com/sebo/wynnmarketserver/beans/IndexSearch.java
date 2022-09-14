package com.sebo.wynnmarketserver.beans;

import com.sebo.wynnmarketserver.objects.ItemArray;
import com.sebo.wynnmarketserver.objects.InfoMaps;
import org.primefaces.PrimeFaces;
import org.primefaces.component.dataview.DataView;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.springframework.stereotype.Component;

import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.List;

@Component("indexSearch")
@RequestScoped
public class IndexSearch {

    public String getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
    }

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    private String selectedType;
    private String selectedCategory;

    private String itemName;

    public List<String> getStatNameList() {
        List<String> statList = new ArrayList<>(InfoMaps.statMap.keySet());
        statList.sort(String::compareToIgnoreCase);
        return statList;
    }

    public String getStatId(String statName){
        return InfoMaps.statMap.get(statName);
    }

    public ArrayList<String> getStats() {
        return stats;
    }

    public void setStats(ArrayList<String> stats) {
        this.stats = stats;
    }

    private ArrayList<String> stats;

    private List<String> categories = InfoMaps.getOrderedCategories();

    public List<String> getCategories() {
        return categories;
    }

    private List<String> types = InfoMaps.categoryMap.get(categories.get(0));

    public void setTypes(ValueChangeEvent event) {
        String category = (String) event.getNewValue();
        types = new ArrayList<>(InfoMaps.categoryMap.get(category));

        SelectOneMenu selectOneMenu = (SelectOneMenu) FacesContext.getCurrentInstance().getViewRoot().findComponent("search")
                .getChildren().get(0).getChildren().get(0).findComponent("types");
        PrimeFaces.current().ajax().update(selectOneMenu);
    }

    public List<String> getTypes() {
        return types;
    }

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
        ItemArray.sortBy(itemName, statSubmission, selectedCategory, selectedType, avgPct, avgStatPct);

        DataView dataView = (DataView) FacesContext.getCurrentInstance().getViewRoot().findComponent("form").findComponent("allItems");
        PrimeFaces.current().ajax().update(dataView);
    }
}