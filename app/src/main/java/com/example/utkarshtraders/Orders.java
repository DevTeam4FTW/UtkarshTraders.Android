package com.example.utkarshtraders;

import android.os.Parcel;
import android.os.Parcelable;

public class Orders implements  Parcelable {

    private String billGenerator;
    private String customerArea;
    private String customerId;
    private String date;
    private String hsnNo;
    private String itemId;
    private String itemName;
    private String itemPrice;
    private String itemQuantity;
    private String orderId;
    private String orderStatus;
    private String priceType;
    private String salesmanId;
    private String taxTotal;
    private String taxableRate;
    private String taxrate;
    private String total;
    private String unit;


    public Orders() {

    }

    public Orders(String billgenerator, String customerarea, String customerid, String datevar,String hsnno, String itemid, String itemname,String itemprice,String itemquantity,String orderid,String orderstatus,String pricetype, String salesmanid,String taxtotal,String taxablerate,String taxratevar,String totalvar,String unitvar) {
        billGenerator = billgenerator;
        customerArea = customerarea;
        customerId = customerid;
        date = datevar;
        hsnNo = hsnno;
        itemId = itemid;
        itemName = itemname;
        itemPrice = itemprice;
        itemQuantity = itemquantity;
        orderId = orderid;
        orderStatus = orderstatus;
        priceType = pricetype;
        salesmanId = salesmanid;
        taxTotal = taxtotal;
        taxableRate = taxablerate;
        taxrate = taxratevar;
        total = totalvar;
        unit = unitvar;
    }

    protected Orders(Parcel in) {
        billGenerator = in.readString();
        customerArea = in.readString();
        customerId = in.readString();
        date = in.readString();
        hsnNo = in.readString();
        itemId = in.readString();
        itemName = in.readString();
        itemPrice = in.readString();
        itemQuantity = in.readString();
        orderId = in.readString();
        orderStatus = in.readString();
        priceType = in.readString();
        salesmanId = in.readString();
        taxTotal = in.readString();
        taxableRate = in.readString();
        taxrate = in.readString();
        total = in.readString();
        unit = in.readString();
    }

    public static final Parcelable.Creator<Orders> CREATOR = new Parcelable.Creator<Orders>() {
        @Override
        public Orders createFromParcel(Parcel in) {
            return new Orders(in);
        }

        @Override
        public Orders[] newArray(int size) {
            return new Orders[size];
        }
    };


    public String getBillGenerator() {
        return billGenerator;
    }

    public void setBillGenerator(String billGenerator) {
        this.billGenerator = billGenerator;
    }

    public String getCustomerArea() {
        return customerArea;
    }

    public void setCustomerArea(String customerArea) {
        this.customerArea = customerArea;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHsnNo() {
        return hsnNo;
    }

    public void setHsnNo(String hsnNo) {
        this.hsnNo = hsnNo;
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(String salesmanId) {
        this.salesmanId = salesmanId;
    }

    public String getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(String taxTotal) {
        this.taxTotal = taxTotal;
    }

    public String getTaxableRate() {
        return taxableRate;
    }

    public void setTaxableRate(String taxableRate) {
        this.taxableRate = taxableRate;
    }

    public String getTaxrate() {
        return taxrate;
    }

    public void setTaxrate(String taxrate) {
        this.taxrate = taxrate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(billGenerator);
        parcel.writeString(customerArea);
        parcel.writeString(customerId);
        parcel.writeString(date);
        parcel.writeString(hsnNo);
        parcel.writeString(itemId);
        parcel.writeString(itemName);
        parcel.writeString(itemPrice);
        parcel.writeString(itemQuantity);
        parcel.writeString(orderId);
        parcel.writeString(orderStatus);
        parcel.writeString(priceType);
        parcel.writeString(salesmanId);
        parcel.writeString(taxTotal);
        parcel.writeString(taxableRate);
        parcel.writeString(taxrate);
        parcel.writeString(total);
        parcel.writeString(unit);
    }
}
