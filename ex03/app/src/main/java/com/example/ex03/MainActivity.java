package com.example.ex03;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    AddressHelper helper;
    SQLiteDatabase db;
    String sql = "select _id, name, phone, juso, photo from address ";
    JusoAdapter jusoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("주소관리");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //db 생성
        helper = new AddressHelper(this);

        //db open
        db = helper.getReadableDatabase();

        //결과 넣기
        Cursor cursor = db.rawQuery(sql, null);

//        while (cursor.moveToNext()){
//            System.out.println(cursor.getString(1));
//        }

        //어댑터 생성
        jusoAdapter = new JusoAdapter(this, cursor);

        //ListView에 어댑터 연결
        ListView list = findViewById(R.id.list);
        list.setAdapter(jusoAdapter);

        //주소등록 버튼 눌렀을때
        findViewById(R.id.btnInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이동
                Intent intent = new Intent(MainActivity.this, InsertActivity.class);
                startActivity(intent);
            }
        });

    } //onCreate

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        sql = "select _id, name, phone, juso, photo from address ";

        if(item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == R.id.name){
            sql += " order by name";
        }else if(item.getItemId() == R.id.phone){
            sql += " order by phone";
        }else if(item.getItemId() == R.id.juso){
            sql += " order by juso";
        }else if(item.getItemId() == R.id.photo){
            sql += " order by photo";
        }
        onRestart();
        return super.onOptionsItemSelected(item);
    }

    //어댑터
    class JusoAdapter extends CursorAdapter{
        public JusoAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //view를 만들어서 리턴
            View view = getLayoutInflater().inflate(R.layout.item_address, parent, false);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            int id = cursor.getInt(0);

            TextView name = view.findViewById(R.id.name);
            name.setText(cursor.getString(1));

            TextView phone = view.findViewById(R.id.phone);
            phone.setText(cursor.getString(2));

            TextView juso = view.findViewById(R.id.juso);
            juso.setText(cursor.getString(3));


            //새로 사진 등록
            CircleImageView photo = view.findViewById(R.id.photo);
            String strPhoto = cursor.getString(4);
            if(strPhoto.equals("")){
                photo.setImageResource(R.drawable.baseline_face_24);
            }else{
                photo.setImageURI(Uri.parse(strPhoto));
            }
            
            //삭제버튼 눌렀을때
            view.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(MainActivity.this) //알림창 띄우기
                            .setTitle("질의")
                            .setMessage(id + "번 주소를 삭제할까요?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //삭제작업
                                    String sql = "delete from address where _id=" + id;
                                    db.execSQL(sql);
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
                    Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Cursor cursor = db.rawQuery(sql, null);
        jusoAdapter.changeCursor(cursor); //sql 내용이 바뀔때마다  cursor에 넣어서 리프레쉬
    }

    //메뉴 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { //써밋될때
                sql = "select _id, name, phone, juso, photo from address ";
                sql += "where juso like '%" + query + "%'";
                sql += " or name like '%" + query + "%'";
                sql += " or phone like '%" + query + "%'";
                //sql += " or photo like '%" + query + "%'";
                onRestart();                
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { //쿼리가 바뀔때마다
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
} //Activity