package android.utils;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import androidx.annotation.NonNull;

public class Country {
    public String name, name_en,code;
    private static ArrayList<Country> countries = new ArrayList<>();

    public Country(String code, String name, String name_en) {
        this.code = code;
        this.name = name;
        this.name_en = name_en;
    }

    @Override
    public String toString() {
        return "Country{" +
                "code='" + code + '\'' +
                "name_en='" + name_en + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static ArrayList<Country> getAll(){
        return new ArrayList<>(countries);
    }

    public static void load(@NonNull Context ctx) throws IOException, JSONException {
        countries = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(ctx.getResources().getAssets().open("region_global.json")));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null)
            sb.append(line);
        br.close();
        JSONArray ja = new JSONArray(sb.toString());
        for (int i = 0; i < ja.length(); i++) {
            JSONObject jo = ja.getJSONObject(i);
            String name = jo.getString("name");
            String name_en = jo.getString("name_en");
            String code = jo.getString("code");

            countries.add(new Country(code,name,name_en));
        }
    }

    public static void destroy() {
        countries.clear();
    }
}
