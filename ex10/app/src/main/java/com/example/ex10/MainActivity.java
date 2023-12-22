package com.example.ex10;

import static com.example.ex10.RemoteService.BASE_URL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    int page = 1;
    List<ShopVO> array = new ArrayList<>();
    ShopAdapter adapter = new ShopAdapter();
    RecyclerView list;
    int last = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("상품목록");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getList();
        list = findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)); //화면에 2개씩 세로로

        //더보기 버튼 눌렀을때
        findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page == last){
                    Toast.makeText(MainActivity.this, "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    page += 1;
                    getList();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getList() {
        System.out.println(".........................." + page);
        RemoteService service = ShopAPI.call();
        Call<HashMap<String, Object>> call = service.list(page, 10);
        call.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                try {
                    JSONObject object = new JSONObject(response.body());
                    int total = object.getInt("total");
                    last = total/10;
                    if(total/10. != last){ //마지막페이지 구하기
                        last += 1;
                    }
                    
                    JSONArray jArray = object.getJSONArray("list");
                    //System.out.println("결과: " + object.toString());

                    for(int i = 0; i < jArray.length(); i++){
                        JSONObject obj = jArray.getJSONObject(i);
                        ShopVO vo = new ShopVO();

                        vo.setPid(obj.getInt("pid"));
                        vo.setTitle(obj.getString("title"));
                        vo.setImage(obj.getString("image"));
                        vo.setLprice(obj.getInt("lprice"));
                        vo.setRegdate(obj.getString("fmtdate"));
                        System.out.println(vo.toString());
                        array.add(vo);
                    }
                    adapter.notifyDataSetChanged();
                }catch (Exception e) {
                    System.out.println("오류: " + e.toString());
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {

            }
        });
    }

    class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

        @NonNull
        @Override
        public ShopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_shop, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShopAdapter.ViewHolder holder, int position) {
            ShopVO vo = array.get(position);
            holder.title.setText(vo.getPid() + "|" + vo.getTitle());
            DecimalFormat df = new DecimalFormat("#,###원");
            holder.price.setText(df.format(vo.getLprice()));

            //이미지
            if(vo.getImage().equals("")){
                holder.image.setImageResource(R.drawable.baseline_card_giftcard_24);
            }else{
                String file = BASE_URL + "/display?file=" + vo.getImage();
                Picasso.with(MainActivity.this).load(file).into(holder.image);
            }
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                    intent.putExtra("pid", vo.getPid());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CardView item;
            TextView title, price;
            ImageView image;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                item = itemView.findViewById(R.id.item);
                title = itemView.findViewById(R.id.title);
                price = itemView.findViewById(R.id.price);
                image = itemView.findViewById(R.id.image);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        array.clear();
        getList();
    }
}