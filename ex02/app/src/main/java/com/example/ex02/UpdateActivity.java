package com.example.ex02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UpdateActivity extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase db; //local db
    EditText name, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        
        getSupportActionBar().setTitle("정보수정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);

        Button btn = findViewById(R.id.btnInsert);
        btn.setText("수정");

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        Cursor cursor = db.rawQuery("select _id, name, price from product where _id =" + id, null);

        if(cursor.moveToNext()){
            name.setText(cursor.getString(1));
            price.setText(String.valueOf(cursor.getInt(2)));
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UpdateActivity.this)
                        .setTitle("질의")
                        .setMessage(id + "번 상품 정보를 수정할까요?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //정보 수정저장
                                String strName = name.getText().toString();
                                String strPrice = price.getText().toString();
                                String sql = "update product set name=";
                                sql += "'" + strName + "',";
                                sql += "price=" + strPrice;
                                sql += " where _id =" + id;
                                db.execSQL(sql);
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", null)
                        .show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //뒤로가기 버튼 눌렀을때
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}