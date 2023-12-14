package com.example.ex04;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AddressHelper extends SQLiteOpenHelper {
    public AddressHelper(@Nullable Context context) {
        super(context, "address.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table address(_id integer primary key autoincrement, name text, phone text, juso text, photo text)");
        db.execSQL("insert into address values(null, '홍길동', '010-1234-4321', '서울 마곡동', '')");
        db.execSQL("insert into address values(null, '이순신', '010-1223-5690', '인천 남동구', '')");
        db.execSQL("insert into address values(null, '김우주', '010-5230-0092', '강원도 강릉시', '')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
