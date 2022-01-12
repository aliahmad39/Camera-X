package com.ali.pf_trainee_cameraapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Singleton {
    // Static variable reference of single_instance
    // of type Singleton
    private static Singleton single_instance = new Singleton();

    // Declaring a variable of type String
    public ArrayList<Uri> list = new ArrayList<Uri>();
    public ArrayList<String> savedList = new ArrayList<>();
    // Constructor
    // Here we will be creating private constructor
    // restricted to this class itself

    // Static method
    // Static method to create instance of Singleton class
    public static Singleton getInstance()
    {
        if (single_instance == null)
            single_instance = new Singleton();

        return single_instance;
    }

    public boolean isSavedListAdded = false;



}

