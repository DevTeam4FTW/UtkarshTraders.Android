package com.example.utkarshtraders;

import android.os.Parcel;
import android.os.Parcelable;

public class Items implements  Parcelable {

    private String ItemCategory;
    private String hsnNo;
    private String itemName;
    private Long itemPrice;
    private Long taxRate;

    public Items() {

    }

    public Items(String itemcategory, String hsnno, String itemname, Long itemprice, Long taxrate) {
        ItemCategory = itemcategory;
        hsnNo = hsnno;
        itemName = itemname;
        itemPrice = itemprice;
        taxRate = taxrate;
    }

    protected Items(Parcel in) {
        ItemCategory = in.readString();
        hsnNo = in.readString();
        itemName = in.readString();
        itemPrice = in.readLong();
        taxRate = in.readLong();
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

    public String getItemCategory() {
        return ItemCategory;
    }

    public void setItemCategory(String itemCategory) {
        ItemCategory = itemCategory;
    }

    public String getHsnNo() {
        return hsnNo;
    }

    public void setHsnNo(String hsnNo) {
        this.hsnNo = hsnNo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Long itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Long getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Long taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ItemCategory);
        parcel.writeString(hsnNo);
        parcel.writeString(itemName);
        parcel.writeLong(itemPrice);
        parcel.writeLong(taxRate);
    }
}
