package com.example.pingout;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converter {

    @TypeConverter
    public static ArrayList<Messages> stringToSomeObjectList(String value) {
        Type listType = new TypeToken<ArrayList<Messages>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String someObjectListToString(ArrayList<Messages> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

}
