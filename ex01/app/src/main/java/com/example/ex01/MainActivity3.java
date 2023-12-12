package com.example.ex01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity3 extends AppCompatActivity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        getSupportActionBar().setTitle("옵션메뉴연습");
        btn = findViewById(R.id.btn);
    }

    //menu 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sub, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.red){
            btn.setTextColor(Color.RED);
        }else if(item.getItemId() == R.id.blue){
            btn.setTextColor(Color.BLUE);
        }else if(item.getItemId() == R.id.green){
            btn.setTextColor(Color.GREEN);
        }else if(item.getItemId() == R.id.big){
            if(item.isChecked()){ //현재 체크되어있으면(큰 글씨면)
                btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 100);
            }else{
                btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, 200);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int color = btn.getTextColors().getDefaultColor();
        if(color == Color.RED){
            menu.findItem(R.id.red).setChecked(true);
        }else if(color == Color.BLUE){
            menu.findItem(R.id.blue).setChecked(true);
        }else if(color == Color.GREEN){
            menu.findItem(R.id.green).setChecked(true);
        }
        if(btn.getTextSize() == 200){
            menu.findItem(R.id.big).setChecked(true);
        }else{
            menu.findItem(R.id.big).setChecked(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
}