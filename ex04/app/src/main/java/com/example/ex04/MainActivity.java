package com.example.ex04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    AddressHelper helper;
    SQLiteDatabase db;
    JusoAdapter adapter;
    String sql = "select _id,name,phone,juso,photo from address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("주소목록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helper=new AddressHelper(this);
        db=helper.getReadableDatabase();

        //데이터 생성
        Cursor cursor=db.rawQuery(sql, null);

        //어댑터 생성
        adapter=new JusoAdapter(this, cursor);

        //ListView에 어댑터 연결
        ListView list=findViewById(R.id.list);
        list.setAdapter(adapter);

        findViewById(R.id.btnInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, InsertActivity.class);
                startActivity(intent);
            }
        });
    } //onCreate

    class JusoAdapter extends CursorAdapter{ //cursorAdapter를 상속받아 새로운 주소어댑터 생성

        public JusoAdapter(Context context, Cursor c) {

            super(context, c);
        }
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view=getLayoutInflater().inflate(R.layout.item_address, parent, false);
            return view;
        }
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int id=cursor.getInt(0);
            TextView name=view.findViewById(R.id.name);
            name.setText(cursor.getString(1));
            TextView phone=view.findViewById(R.id.phone);
            phone.setText(cursor.getString(2));
            TextView juso=view.findViewById(R.id.juso);
            juso.setText(cursor.getString(3));
            //사진출력
            ImageView photo=view.findViewById(R.id.photo);
            String strPhoto=cursor.getString(4);
            if(strPhoto.equals("")){
                photo.setImageResource(R.drawable.baseline_person_24);
            }else{
                photo.setImageBitmap(BitmapFactory.decodeFile(strPhoto));
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this, UpdateActivity.class);
                    intent.putExtra("id", id); //UpdateActivity로 이동시 현재 id를 담아서
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        Cursor cursor=db.rawQuery(sql, null);
        adapter.changeCursor(cursor);
        super.onRestart();
    }

    //메뉴 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == R.id.book){
            Intent intent = new Intent(this, BookActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
} //Activity