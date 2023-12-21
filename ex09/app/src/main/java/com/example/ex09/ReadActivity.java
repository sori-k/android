package com.example.ex09;

import static com.example.ex09.RemoteService.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReadActivity extends AppCompatActivity {

    //db 연결
    Retrofit retrofit=new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    RemoteService service=retrofit.create(RemoteService.class);
    UserVO vo = new UserVO();
    TextView txtuid, address, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        txtuid = findViewById(R.id.uid);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");

        System.out.println("........................" + uid);

        getSupportActionBar().setTitle("사용자 정보 | " + uid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Call<UserVO> call = service.read(uid);
        call.enqueue(new Callback<UserVO>() {
            @Override
            public void onResponse(Call<UserVO> call, Response<UserVO> response) {
                vo = response.body();
                //System.out.println(vo.toString());
                txtuid.setText(vo.getUname() + "(" + vo.getUid() + ")");
                address.setText(vo.getAddress1() + " " + vo.getAddress2());
                phone.setText(vo.getPhone());
            }

            @Override
            public void onFailure(Call<UserVO> call, Throwable t) {

            }
        });

    } //onCreate

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
} //Activity