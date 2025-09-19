package com.example.designs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Designs.db";
    private static final int DATABASE_VERSION = 5;
    private static final String TAG = "DatabaseHelper";

    // User Table
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_LOCATION = "location";

    //  Measurements Table
    public static final String TABLE_MEASUREMENTS = "measurements";
    public static final String COLUMN_MEASUREMENTS_ID = "id";
    public static final String COLUMN_MEASUREMENTS_USER_EMAIL = "user_email";
    public static final String COLUMN_MEASUREMENTS_IMAGE_RESOURCE = "image_resource";
    public static final String COLUMN_MEASUREMENTS_BUST = "bust";
    public static final String COLUMN_MEASUREMENTS_WAIST = "waist";
    public static final String COLUMN_MEASUREMENTS_HIPS = "hips";
    public static final String COLUMN_MEASUREMENTS_HEIGHT = "height";
    public static final String COLUMN_MEASUREMENTS_SHOULDER = "shoulder";
    public static final String COLUMN_MEASUREMENTS_ARM_LENGTH = "arm_length";
    public static final String COLUMN_MEASUREMENTS_NOTES = "notes";
    public static final String COLUMN_MEASUREMENTS_MATERIAL = "material";
    public static final String COLUMN_MEASUREMENTS_TIMESTAMP = "timestamp";

    //  Custom Designs Table
    public static final String TABLE_CUSTOM_DESIGNS = "custom_designs";
    public static final String COLUMN_CUSTOM_ID = "id";
    public static final String COLUMN_CUSTOM_USER_EMAIL = "user_email";
    public static final String COLUMN_CUSTOM_IMAGE_URI = "image_uri";
    public static final String COLUMN_CUSTOM_NAME = "design_name";
    public static final String COLUMN_CUSTOM_DESCRIPTION = "description";
    public static final String COLUMN_CUSTOM_MATERIAL = "material";
    public static final String COLUMN_CUSTOM_ESTIMATED_COST = "estimated_cost";
    public static final String COLUMN_CUSTOM_TIMESTAMP = "timestamp";

    //  Orders Table
    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_USER_EMAIL = "user_email";
    public static final String COLUMN_ORDER_DESIGN_ID = "design_id";
    public static final String COLUMN_ORDER_DESIGN_NAME = "design_name";
    public static final String COLUMN_ORDER_IMAGE_URI = "design_image_uri";
    public static final String COLUMN_ORDER_QUANTITY = "quantity";
    public static final String COLUMN_ORDER_STREET = "street";
    public static final String COLUMN_ORDER_CITY = "city";
    public static final String COLUMN_ORDER_POSTAL_CODE = "postal_code";
    public static final String COLUMN_ORDER_PAYMENT = "payment_method";
    public static final String COLUMN_ORDER_UNIT_PRICE = "unit_price";
    public static final String COLUMN_ORDER_SUBTOTAL = "subtotal";
    public static final String COLUMN_ORDER_SHIPPING = "shipping_fee";
    public static final String COLUMN_ORDER_TOTAL = "total_cost";
    public static final String COLUMN_ORDER_STATUS = "status";
    public static final String COLUMN_ORDER_TIMESTAMP = "timestamp";

    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_USER_NAME + " TEXT NOT NULL," +
                    COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL," +
                    COLUMN_USER_PASSWORD + " TEXT NOT NULL," +
                    COLUMN_USER_PHONE + " TEXT," +
                    COLUMN_USER_LOCATION + " TEXT)";

    private static final String SQL_CREATE_MEASUREMENTS =
            "CREATE TABLE " + TABLE_MEASUREMENTS + " (" +
                    COLUMN_MEASUREMENTS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MEASUREMENTS_USER_EMAIL + " TEXT NOT NULL, " +
                    COLUMN_MEASUREMENTS_IMAGE_RESOURCE + " INTEGER, " +
                    COLUMN_MEASUREMENTS_BUST + " TEXT, " +
                    COLUMN_MEASUREMENTS_WAIST + " TEXT, " +
                    COLUMN_MEASUREMENTS_HIPS + " TEXT, " +
                    COLUMN_MEASUREMENTS_HEIGHT + " TEXT, " +
                    COLUMN_MEASUREMENTS_SHOULDER + " TEXT, " +
                    COLUMN_MEASUREMENTS_ARM_LENGTH + " TEXT, " +
                    COLUMN_MEASUREMENTS_NOTES + " TEXT, " +
                    COLUMN_MEASUREMENTS_MATERIAL + " TEXT, " +
                    COLUMN_MEASUREMENTS_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (" + COLUMN_MEASUREMENTS_USER_EMAIL + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USER_EMAIL + ") ON DELETE CASCADE)";

    private static final String SQL_CREATE_CUSTOM_DESIGNS =
            "CREATE TABLE " + TABLE_CUSTOM_DESIGNS + " (" +
                    COLUMN_CUSTOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CUSTOM_USER_EMAIL + " TEXT NOT NULL, " +
                    COLUMN_CUSTOM_IMAGE_URI + " TEXT, " +
                    COLUMN_CUSTOM_NAME + " TEXT, " +
                    COLUMN_CUSTOM_DESCRIPTION + " TEXT, " +
                    COLUMN_CUSTOM_MATERIAL + " TEXT, " +
                    COLUMN_CUSTOM_ESTIMATED_COST + " TEXT, " +
                    COLUMN_CUSTOM_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (" + COLUMN_CUSTOM_USER_EMAIL + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USER_EMAIL + ") ON DELETE CASCADE)";

    private static final String SQL_CREATE_ORDERS =
            "CREATE TABLE " + TABLE_ORDERS + " (" +
                    COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ORDER_USER_EMAIL + " TEXT NOT NULL, " +
                    COLUMN_ORDER_DESIGN_ID + " INTEGER, " +
                    COLUMN_ORDER_DESIGN_NAME + " TEXT, " +
                    COLUMN_ORDER_IMAGE_URI + " TEXT, " +
                    COLUMN_ORDER_QUANTITY + " INTEGER, " +
                    COLUMN_ORDER_STREET + " TEXT, " +
                    COLUMN_ORDER_CITY + " TEXT, " +
                    COLUMN_ORDER_POSTAL_CODE + " TEXT, " +
                    COLUMN_ORDER_PAYMENT + " TEXT, " +
                    COLUMN_ORDER_UNIT_PRICE + " TEXT, " +
                    COLUMN_ORDER_SUBTOTAL + " TEXT, " +
                    COLUMN_ORDER_SHIPPING + " TEXT, " +
                    COLUMN_ORDER_TOTAL + " TEXT, " +
                    COLUMN_ORDER_STATUS + " TEXT DEFAULT 'Pending', " +
                    COLUMN_ORDER_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (" + COLUMN_ORDER_USER_EMAIL + ") REFERENCES " +
                    TABLE_USERS + "(" + COLUMN_USER_EMAIL + ") ON DELETE CASCADE)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
        Log.d(TAG, "Foreign key constraints enabled.");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS);
        db.execSQL(SQL_CREATE_MEASUREMENTS);
        db.execSQL(SQL_CREATE_CUSTOM_DESIGNS);
        db.execSQL(SQL_CREATE_ORDERS);
        Log.d(TAG, "onCreate: All tables created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: Upgrading database from version " + oldVersion + " to " + newVersion);
        try {
            if (oldVersion < 3) {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_USER_PHONE + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_USER_LOCATION + " TEXT");
                Log.d(TAG, "onUpgrade: Added phone and location columns to users table (version < 3).");
            }
            if (oldVersion < 4) {
                db.execSQL(SQL_CREATE_CUSTOM_DESIGNS);
                db.execSQL(SQL_CREATE_ORDERS);
                Log.d(TAG, "onUpgrade: Created custom_designs and orders tables (version < 4).");
            }
            if (oldVersion < 5) {
                Log.d(TAG, "onUpgrade: Handling version < 5. If this involves new FKs on existing data, " +
                        "a more complex migration (drop/recreate or data transfer) is usually needed.");
            }
        } catch (Exception e) {
            Log.e(TAG, "onUpgrade: Error during database upgrade from " + oldVersion + " to " + newVersion, e);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "onDowngrade: Downgrading database from version " + oldVersion + " to " + newVersion + ". This operation will cause data loss for all tables.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOM_DESIGNS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASUREMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);

        long result = -1;
        try {
            result = db.insert(TABLE_USERS, null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting user: " + e.getMessage());
            Log.e(TAG, "User registration failed for email: " + email, e);
        }
        return result != -1;
    }

    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public boolean validateLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?",
                new String[]{email, password},
                null, null, null);

        boolean valid = (cursor.getCount() > 0);
        cursor.close();
        return valid;
    }


    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                null,
                COLUMN_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
    }

    public boolean updateUserProfile(String email, String phone, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_LOCATION, location);

        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_EMAIL + " = ?", new String[]{email});

        return rowsAffected > 0;
    }

    public boolean updateProfilePhoneAndLocation(String email, String phone, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_LOCATION, location);

        int rows = db.update(TABLE_USERS, values, COLUMN_USER_EMAIL + " = ?", new String[]{email});

        return rows > 0;
    }

    public Cursor getUserDetails(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS, null, COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PASSWORD, newPassword);

        int result = db.update(TABLE_USERS, values, COLUMN_USER_EMAIL + " = ?", new String[]{email});
        return result > 0;
    }

    public boolean insertMeasurements(String userEmail, int selectedImageRes,
                                      String bust, String waist, String hips,
                                      String height, String shoulder, String armLength,
                                      String notes, String material) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEASUREMENTS_USER_EMAIL, userEmail);
        values.put(COLUMN_MEASUREMENTS_IMAGE_RESOURCE, selectedImageRes);
        values.put(COLUMN_MEASUREMENTS_BUST, bust);
        values.put(COLUMN_MEASUREMENTS_WAIST, waist);
        values.put(COLUMN_MEASUREMENTS_HIPS, hips);
        values.put(COLUMN_MEASUREMENTS_HEIGHT, height);
        values.put(COLUMN_MEASUREMENTS_SHOULDER, shoulder);
        values.put(COLUMN_MEASUREMENTS_ARM_LENGTH, armLength);
        values.put(COLUMN_MEASUREMENTS_NOTES, notes);
        values.put(COLUMN_MEASUREMENTS_MATERIAL, material);

        long result = -1;
        try {
            result = db.insert(TABLE_MEASUREMENTS, null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting measurements: " + e.getMessage());
            Log.e(TAG, "Measurements insertion failed for user: " + userEmail, e);
        }
        return result != -1;
    }


    public Cursor getMeasurementsForUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_MEASUREMENTS,
                null,
                COLUMN_MEASUREMENTS_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
    }

    public long insertCustomDesign(String email, String imageUri,
                                   String name, String description,
                                   String material, String estimatedCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOM_USER_EMAIL, email);
        values.put(COLUMN_CUSTOM_IMAGE_URI, imageUri);
        values.put(COLUMN_CUSTOM_NAME, name);
        values.put(COLUMN_CUSTOM_DESCRIPTION, description);
        values.put(COLUMN_CUSTOM_MATERIAL, material);
        values.put(COLUMN_CUSTOM_ESTIMATED_COST, estimatedCost);

        long result = -1;
        try {
            result = db.insert(TABLE_CUSTOM_DESIGNS, null, values);
            if (result != -1) {
                Log.d(TAG, "Custom design inserted successfully with ID: " + result);
            } else {
                Log.e(TAG, "Failed to insert custom design into database.");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting custom design: " + e.getMessage());
            Log.e(TAG, "Custom design insertion failed for user: " + email, e);
        }
        return result;
    }


    public Cursor getCustomDesignsForUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_CUSTOM_DESIGNS,
                null,
                COLUMN_CUSTOM_USER_EMAIL + " = ?",
                new String[]{email},
                null, null, null);
    }

    public boolean insertOrder(String userEmail,
                               int designId,
                               String designName,
                               String imageUri,
                               int quantity,
                               String street,
                               String city,
                               String postalCode,
                               String paymentMethod,
                               String unitPrice,
                               String subtotal,
                               String shippingFee,
                               String totalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_USER_EMAIL, userEmail);
        values.put(COLUMN_ORDER_DESIGN_ID, designId);
        values.put(COLUMN_ORDER_DESIGN_NAME, designName);
        values.put(COLUMN_ORDER_IMAGE_URI, imageUri);
        values.put(COLUMN_ORDER_QUANTITY, quantity);
        values.put(COLUMN_ORDER_STREET, street);
        values.put(COLUMN_ORDER_CITY, city);
        values.put(COLUMN_ORDER_POSTAL_CODE, postalCode);
        values.put(COLUMN_ORDER_PAYMENT, paymentMethod);
        values.put(COLUMN_ORDER_UNIT_PRICE, unitPrice);
        values.put(COLUMN_ORDER_SUBTOTAL, subtotal);
        values.put(COLUMN_ORDER_SHIPPING, shippingFee);
        values.put(COLUMN_ORDER_TOTAL, totalCost);

        long result = -1;
        try {
            result = db.insert(TABLE_ORDERS, null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting order: " + e.getMessage());
            Log.e(TAG, "Order insertion failed for user: " + userEmail, e);
        }
        return result != -1;
    }
}