package com.sebo.wynnmarketserver.beans;

import com.sebo.wynnmarketserver.objects.AuctionItem;
import com.sebo.wynnmarketserver.objects.ItemArray;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("dataGridView")
@ViewScoped
public class DataGridView implements Serializable {

    private List<AuctionItem> items;
    private AuctionItem selectedItem;

    @PostConstruct
    public void init() {
        ArrayList<String> tempArray = new ArrayList<>(Arrays.asList("null"));
        items = ItemArray.sortBy("null", tempArray, "null", "null", true, false);
    }

    public List<AuctionItem> getItems() {
        return items;
    }

    public AuctionItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(AuctionItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    public void clearMultiViewState() {
        FacesContext context = FacesContext.getCurrentInstance();
        String viewId = context.getViewRoot().getViewId();
        PrimeFaces.current().multiViewState().clearAll(viewId, true, this::showMessage);
    }

    private void showMessage(String clientId) {
        FacesContext.getCurrentInstance()
                .addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, clientId + " multiview state has been cleared out", null));
    }
}