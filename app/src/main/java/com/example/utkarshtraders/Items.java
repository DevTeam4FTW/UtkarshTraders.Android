package com.example.utkarshtraders;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Items implements  Parcelable {

    private String category;
    private ArrayList<HashMap<String,String>> catgprice;
    private String cname;
    private String hsnNo;
    private String ics;
    private String name;
    private String price;
    private String taxRate;

    public Items() {

    }

    public Items(String Category, ArrayList<HashMap<String,String>>  Catgprice, String Cname, String HsnNo, String Ics,String Name,String Price,String TaxRate) {
        category = Category;
        catgprice = Catgprice;
        cname = Cname;
        hsnNo = HsnNo;
        ics = Ics;
        name = Name;
        name = Name;
        price = Price;
        taxRate = TaxRate;
    }

    protected Items(Parcel in) {
        category = in.readString();
        catgprice = (ArrayList<HashMap<String,String>>) in.readValue(Array.class.getClassLoader());
        cname = in.readString();
        hsnNo = in.readString();
        ics = in.readString();
        name = in.readString();
        price = in.readString();
        taxRate = in.readString();
    }

    public static final Parcelable.Creator<Items> CREATOR = new Parcelable.Creator<Items>() {
        @Override
        public Items createFromParcel(Parcel in) {
            return new Items(in);
        }

        @Override
        public Items[] newArray(int size) {
            return new Items[size];
        }
    };


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<HashMap<String,String>> getCatgprice() {
        return catgprice;
    }

    public void setCatgprice(ArrayList<HashMap<String,String>> catgprice) {
        this.catgprice = catgprice;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getHsnNo() {
        return hsnNo;
    }

    public void setHsnNo(String hsnNo) {
        this.hsnNo = hsnNo;
    }

    public String getIcs() {
        return ics;
    }

    public void setIcs(String ics) {
        this.ics = ics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(category);
        parcel.writeValue(catgprice);
        parcel.writeString(cname);
        parcel.writeString(hsnNo);
        parcel.writeString(ics);
        parcel.writeString(name);
        parcel.writeString(price);
        parcel.writeString(taxRate);
    }
}
