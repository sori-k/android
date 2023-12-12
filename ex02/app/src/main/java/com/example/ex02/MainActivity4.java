package com.example.ex02;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity4 extends AppCompatActivity {
    DBHelper helper; //전역변수 선언
    SQLiteDatabase db;
    Cursor cursor;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        getSupportActionBar().setTitle("SQLite 데이터베이스");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helper = new DBHelper(this);
        db = helper.getReadableDatabase(); //데이터베이스 오픈
        
        //데이터 생성
        cursor = db.rawQuery("select _id, name, price from product", null);

        //어댑터 생성
        adapter = new MyAdapter(this, cursor);

        //ListView에 어댑터 연결
        ListView list = findViewById(R.id.list);
        list.setAdapter(adapter);

        findViewById(R.id.btnInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity4.this, InsertActivity.class);
                startActivity(intent);
            }
        });

    } //onCreate
    
    //어댑터 생성
    public class MyAdapter extends CursorAdapter {

        public MyAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.item, parent, false);
            return view; //데이터의 갯수만큼 item이 나옴
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int id = cursor.getInt(0);

            TextView name = view.findViewById(R.id.name);
            name.setText(cursor.getString(1));

            TextView price = view.findViewById(R.id.price);
            int intPrice = cursor.getInt(2);
            DecimalFormat df = new DecimalFormat("#,###원");
            price.setText(df.format(intPrice));

            Button btn = view.findViewById(R.id.btn);
            btn.setText("삭제");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(MainActivity4.this)
                            .setTitle("질의")
                            .setMessage(id + "번 상품을 삭제할까요?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //삭제하는 sql문
                                    String sql = "delete from product where _id =" + id;
                                    db.execSQL(sql);
                                    
                                    //삭제 후 데이터 다시 읽어들임
                                    //sql = "select _id, name, price from product";
                                    //Cursor cursor1 = db.rawQuery(sql, null);
                                    //adapter.changeCursor(cursor1);
                                    onRestart();
                                }
                            })
                            .setNegativeButton("아니요", null)
                            .show();
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity4.this, UpdateActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        //Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
        String sql = "select _id, name, price from product";
        Cursor cursor1 = db.rawQuery(sql, null);
        adapter.changeCursor(cursor1);
        super.onRestart();
    }
} //Activity