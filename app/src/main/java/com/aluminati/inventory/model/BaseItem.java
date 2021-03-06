package com.aluminati.inventory.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
//Update
public abstract class BaseItem implements IMapper {
    protected String docID;
    protected String storeID;
    protected String storeCity;
    protected String storeCountry;
    protected boolean isRestricted;
    protected String title;
    protected String description;
    protected double price;
    protected String imgLink;
    protected List<String> tags;
    protected boolean rental;
    protected String dep;
    private String name;

    public BaseItem() {}

    public BaseItem(String storeID, String storeCity,
                    String storeCountry, String title,
                    String description, double price,
                    String imgLink, List<String> tags, boolean isRestricted, boolean rental) {
        this.storeID = storeID;
        this.storeCity = storeCity;
        this.storeCountry = storeCountry;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imgLink = imgLink;
        this.tags = tags;
        this.isRestricted = isRestricted;
        this.rental = rental;
    }

    public BaseItem(String title, String description, double price, String imgLink){
        this.title = title;
        this.description = description;
        this.price = price;
        this.imgLink = imgLink;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean restricted) {
        isRestricted = restricted;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }


    public String getStoreCity() {
        return storeCity;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public String getStoreCountry() {
        return storeCountry;
    }

    public void setStoreCountry(String storeCountry) {
        this.storeCountry = storeCountry;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean getRental() {
        return rental;
    }

    public void setRental(boolean rental) {
        this.rental = rental;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("title", title);
        map.put("tags", tags);
        map.put("imgLink", imgLink);
        map.put("description", description);
        map.put("storeCity", storeCity);
        map.put("docID", docID);
        map.put("storeID", storeID);
        map.put("price", price);
        map.put("isRestricted", isRestricted);
        map.put("rental", rental);

        return map;
    }
}
