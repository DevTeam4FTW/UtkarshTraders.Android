package com.example.utkarshtraders;

public class Customers {

    private String city;
    private String clientAddress;
    private String clientArea;
    private String clientName;
    private String clientPhoneNo;
    private String custType;
    private String fssaino;
    private String gstno;
    private String pincode;
    private String state;

    public Customers(){

    }

    public Customers(String City,String ClientAddress,String ClientArea,String ClientName,String ClientPhoneNo,String CustType,String Fssaino,String Gstno,String Pincode,String State){
                city=City;
                clientAddress=ClientAddress;
                clientArea=ClientArea;
                clientName=ClientName;
                clientPhoneNo=ClientPhoneNo;
                custType = CustType;
                fssaino = Fssaino;
                gstno = Gstno;
                pincode = Pincode;
                state = State;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientArea() {
        return clientArea;
    }

    public void setClientArea(String clientArea) {
        this.clientArea = clientArea;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhoneNo() {
        return clientPhoneNo;
    }

    public void setClientPhoneNo(String clientPhoneNo) {
        this.clientPhoneNo = clientPhoneNo;
    }

    public String getCustType() {
        return custType;
    }

    public void setCustType(String custType) {
        this.custType = custType;
    }

    public String getFssaino() {
        return fssaino;
    }

    public void setFssaino(String fssaino) {
        this.fssaino = fssaino;
    }

    public String getGstno() {
        return gstno;
    }

    public void setGstno(String gstno) {
        this.gstno = gstno;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

