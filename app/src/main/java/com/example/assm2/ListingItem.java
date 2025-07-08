package com.example.assm2;

public class ListingItem {
    public String title, price, quantity, seller, description, phone, address;
    public boolean available;

    public ListingItem(String title, String price, String quantity, String seller,
                       boolean available, String description, String phone, String address) {
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.seller = seller;
        this.available = available;
        this.description = description;
        this.phone = phone;
        this.address = address;
    }
}
