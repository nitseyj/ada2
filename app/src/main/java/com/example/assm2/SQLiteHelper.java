package com.example.assm2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "assm2.db";
    private static final int DATABASE_VERSION = 1;

    // === USERS TABLE (LOGIN/REGISTER) ===
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_PHONE = "phone";
    private static final String COL_EMAIL = "email";
    private static final String COL_ADDRESS = "address";
    private static final String COL_PIN = "pin";
    private static final String COL_ROLE = "role";

    // === LISTINGS TABLE (SEPARATE SECTION FOR SELLER LISTINGS) ===
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

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // === USERS TABLE CREATION ===
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_ID + " TEXT PRIMARY KEY, " +
                COL_NAME + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_ADDRESS + " TEXT, " +
                COL_PIN + " TEXT, " +
                COL_ROLE + " TEXT)";
        db.execSQL(createUsersTable);
        Log.d("DB_CREATE", "User table created.");

        // === LISTINGS TABLE CREATION ===
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
        Log.d("DB_CREATE", "Listings table created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTINGS);
        onCreate(db);
    }

    // === USERS SECTION ===

    public boolean insertUser(String id, String name, String phone, String email, String address,
                              String pin, String role) {
        if (checkUserExists(id)) {
            Log.d("DB_INSERT", "User already exists: ID=" + id);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, id);
        values.put(COL_NAME, name);
        values.put(COL_PHONE, phone);
        values.put(COL_EMAIL, email);
        values.put(COL_ADDRESS, address);
        values.put(COL_PIN, pin);
        values.put(COL_ROLE, role.toLowerCase());

        long result = db.insert(TABLE_USERS, null, values);
        Log.d("DB_INSERT", "Inserted user: ID=" + id + ", role=" + role);
        return result != -1;
    }

    public boolean validateLogin(String id, String pin, String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_ID + "=? AND " + COL_PIN + "=? AND " + COL_ROLE + "=?",
                new String[]{id, pin, role.toLowerCase()},
                null, null, null);

        boolean valid = (cursor.getCount() > 0);
        Log.d("DB_LOGIN", "Login attempt: ID=" + id + ", role=" + role + " → " + (valid ? "SUCCESS" : "FAIL"));
        cursor.close();
        return valid;
    }

    public boolean checkUserExists(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID},
                COL_ID + "=?", new String[]{id},
                null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
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

    public Cursor getUserById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null,
                COL_ID + "=?", new String[]{id},
                null, null, null);
    }

    // === LISTINGS SECTION (SEPARATE TABLE FOR SELLER LISTINGS) ===

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

        long result = db.insert(TABLE_LISTINGS, null, values);
        Log.d("DB_INSERT", "Inserted listing: ID=" + id + ", Title=" + title);
        return result != -1;

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

}
