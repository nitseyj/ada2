package com.example.assm2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "assm2.db";
    private static final int DATABASE_VERSION = 3;

    // === USERS TABLE ===
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phone";
    private static final String COL_EMAIL = "email";
    private static final String COL_ADDRESS = "address";
    private static final String COL_PIN = "pin";
    private static final String COL_ROLE = "role";
    private static final String COL_PROFILE_PIC = "profile_pic";

    // === LISTINGS TABLE ===
    private static final String TABLE_LISTINGS = "listings";
    private static final String COL_LISTING_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_DESC = "description";
    private static final String COL_CATEGORY = "category";
    private static final String COL_PRICE = "price";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_SELLER_ID = "seller_id";
    private static final String COL_SELLER_NAME = "seller_name";
    private static final String COL_SELLER_PHONE = "seller_phone";
    private static final String COL_SELLER_ADDRESS = "seller_address";

    // === PURCHASE HISTORY TABLE ===
    private static final String TABLE_PURCHASES = "purchase_history";
    private static final String COL_PURCHASE_ID = "purchase_id";
    private static final String COL_BUYER_ID = "buyer_id";
    private static final String COL_ITEM_TITLE = "item_title";
    private static final String COL_ITEM_PRICE = "item_price";
    private static final String COL_ITEM_DESC = "item_desc";
    private static final String COL_SELLER = "seller";
    private static final String COL_DATE = "date";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " TEXT PRIMARY KEY, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_ADDRESS + " TEXT, " +
                COL_PIN + " TEXT, " +
                COL_ROLE + " TEXT, " +
                COL_PROFILE_PIC + " BLOB)";
        db.execSQL(createUsersTable);

        String createListingsTable = "CREATE TABLE " + TABLE_LISTINGS + " (" +
                COL_LISTING_ID + " TEXT PRIMARY KEY, " +
                COL_TITLE + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_CATEGORY + " TEXT, " +
                COL_PRICE + " REAL, " +
                COL_QUANTITY + " INTEGER, " +
                COL_SELLER_ID + " TEXT, " +
                COL_SELLER_NAME + " TEXT, " +
                COL_SELLER_PHONE + " TEXT, " +
                COL_SELLER_ADDRESS + " TEXT)";
        db.execSQL(createListingsTable);

        String createPurchasesTable = "CREATE TABLE " + TABLE_PURCHASES + " (" +
                COL_PURCHASE_ID + " TEXT PRIMARY KEY, " +
                COL_BUYER_ID + " TEXT, " +
                COL_ITEM_TITLE + " TEXT, " +
                COL_ITEM_PRICE + " TEXT, " +
                COL_ITEM_DESC + " TEXT, " +
                COL_SELLER + " TEXT, " +
                COL_DATE + " TEXT)";
        db.execSQL(createPurchasesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PURCHASES);
        onCreate(db);
    }

    // === USERS ===
    public boolean insertUser(String id, String name, String phone, String email, String address,
                              String pin, String role) {
        if (checkUserExists(id)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);
        values.put(COL_EMAIL, email);
        values.put(COL_ADDRESS, address);
        values.put(COL_PIN, pin);
        values.put(COL_ROLE, role.toLowerCase());

        return db.insert(TABLE_USERS, null, values) != -1;
    }

    public boolean validateLogin(String id, String pin, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_ID + "=? AND " + COL_PIN + "=? AND " + COL_ROLE + "=?",
                new String[]{id, pin, role.toLowerCase()},
                null, null, null);

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    public boolean checkUserExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID},
                COL_ID + "=?", new String[]{id},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getUserById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null,
                COL_ID + "=?", new String[]{id},
                null, null, null);
    }

    public boolean updateProfilePicture(String userId, byte[] imageBytes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROFILE_PIC, imageBytes);

        int rows = db.update(TABLE_USERS, values, COL_ID + "=?", new String[]{userId});
        return rows > 0;
    }

    public byte[] getProfilePicture(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_PROFILE_PIC},
                COL_ID + "=?", new String[]{userId}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            byte[] imageBytes = cursor.getBlob(cursor.getColumnIndexOrThrow(COL_PROFILE_PIC));
            cursor.close();
            return imageBytes;
        }

        return null;
    }

    // ✅ === NEW METHOD: Get sellers with address and profile picture ===
    public Cursor getSellersWithAddress() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_USERS,
                new String[]{COL_NAME, COL_PHONE, COL_ADDRESS, COL_PROFILE_PIC},
                COL_ROLE + "=? AND " + COL_ADDRESS + " IS NOT NULL AND " + COL_ADDRESS + " != ''",
                new String[]{"seller"},
                null, null,
                COL_NAME + " ASC"
        );
    }

    // === LISTINGS ===
    public boolean insertListing(String id, String title, String desc, String category, double price,
                                 int quantity, String sellerId, String sellerName,
                                 String sellerPhone, String sellerAddress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LISTING_ID, id);
        values.put(COL_TITLE, title);
        values.put(COL_DESC, desc);
        values.put(COL_CATEGORY, category);
        values.put(COL_PRICE, price);
        values.put(COL_QUANTITY, quantity);
        values.put(COL_SELLER_ID, sellerId);
        values.put(COL_SELLER_NAME, sellerName);
        values.put(COL_SELLER_PHONE, sellerPhone);
        values.put(COL_SELLER_ADDRESS, sellerAddress);

        return db.insert(TABLE_LISTINGS, null, values) != -1;
    }

    public Cursor getListingsBySellerId(String sellerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_LISTINGS, null,
                COL_SELLER_ID + "=?",
                new String[]{sellerId},
                null, null, COL_TITLE + " ASC");
    }

    public Cursor getAllListings() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_LISTINGS, null, null, null, null, null, COL_TITLE + " ASC");
    }

    public boolean deleteListingById(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_LISTINGS, COL_TITLE + "=?", new String[]{title}) > 0;
    }

    public boolean purchaseListing(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_LISTINGS, new String[]{COL_QUANTITY},
                COL_TITLE + "=?", new String[]{title}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int qty = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
            if (qty > 0) {
                ContentValues values = new ContentValues();
                values.put(COL_QUANTITY, qty - 1);
                int rows = db.update(TABLE_LISTINGS, values, COL_TITLE + "=?", new String[]{title});
                cursor.close();
                return rows > 0;
            }
            cursor.close();
        }
        return false;
    }

    // === PURCHASE HISTORY ===
    public boolean insertPurchase(String buyerId, String title, String price,
                                  String desc, String seller, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PURCHASE_ID, "PUR" + System.currentTimeMillis());
        values.put(COL_BUYER_ID, buyerId);
        values.put(COL_ITEM_TITLE, title);
        values.put(COL_ITEM_PRICE, price);
        values.put(COL_ITEM_DESC, desc);
        values.put(COL_SELLER, seller);
        values.put(COL_DATE, date);

        return db.insert(TABLE_PURCHASES, null, values) != -1;
    }

    public Cursor getPurchaseHistory(String buyerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PURCHASES, null,
                COL_BUYER_ID + "=?", new String[]{buyerId},
                null, null, COL_DATE + " DESC");
    }

    public void logAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        Log.d("DB_DUMP", "---- All Users in DB ----");

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE));
                Log.d("DB_DUMP", "ID: " + id + ", Name: " + name + ", Role: " + role);
            } while (cursor.moveToNext());
        } else {
            Log.d("DB_DUMP", "No users found.");
        }

        cursor.close();
    }
}
