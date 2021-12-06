package android.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{
    private SQLiteDatabase db;

    public DBOpenHelper(Context context){
        super(context,"db_test",null,1);
        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS places(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "xarea TEXT," +
                "city TEXT," +
                "lat TEXT," +
                "lng TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }

    public boolean addPlace(String xarea,String city,String lat,String lng){
        ContentValues values = new ContentValues();
        values.put("xarea",xarea);
        values.put("city",city);
        values.put("lat",lat);
        values.put("lng",lng);
        return db.insert("places", null, values) > 0;
    }

    @SuppressLint("Range")
    public Place getPlace(String xarea, String city){
        Place place = null;
        Cursor cursor = db.query("places",null,"xarea='"+xarea+"' and city='"+city+"'",null,null,null,null);
        if(cursor.moveToNext()){
            place = new Place();
            place.setXarea(xarea);
            place.setCity(city);
            place.setLat(cursor.getString(cursor.getColumnIndex("lat")));
            place.setLng(cursor.getString(cursor.getColumnIndex("lng")));
        }
        return place;
    }

    @SuppressLint("Range")
    public Place getPlaceByArea(String xarea){
        Place place = null;
        Cursor cursor = db.query("places",null,"xarea='"+xarea+"'",null,null,null,null,"1");
        if(cursor.moveToNext()){
            place = new Place();
            place.setXarea(xarea);
            place.setCity(cursor.getString(cursor.getColumnIndex("city")));
            place.setLat(cursor.getString(cursor.getColumnIndex("lat")));
            place.setLng(cursor.getString(cursor.getColumnIndex("lng")));
        }
        return place;
    }
}
