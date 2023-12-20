package com.example.ex08;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    FirebaseUser user;
    List<ShopVO> array = new ArrayList<>();
    FirebaseFirestore db; //데이터 읽어오기
    ShopAdapter adapter = new ShopAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser(); //firebase의 user 정보 가져오기

        getSupportActionBar().setTitle("상품목록 :" + user.getEmail());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //상품등록 버튼 눌렀을때
        findViewById(R.id.btnInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, InsertActivity.class);
                startActivity(intent);
            }
        });

        getList();
        RecyclerView list = findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));

    } //onCreate

    public void getList() {
        db.collection("shop")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        //결과는 querySnapshop에 들어가있다.
                        array.clear();

                        for(QueryDocumentSnapshot doc:task.getResult()){ //doc(하나씩 꺼내온 데이터)
                            ShopVO vo = new ShopVO();
                            vo.setId(doc.getId());
                            vo.setTitle(doc.getData().get("title").toString());
                            vo.setPrice(Integer.parseInt(doc.getData().get("price").toString()));
                            vo.setDate(doc.getData().get("date").toString());
                            vo.setEmail(doc.getData().get("email").toString());
                            vo.setImage(doc.getData().get("image").toString());
                            array.add(vo);
                        }
                        //System.out.println("데이터 갯수: " +array.size());
                        adapter.notifyDataSetChanged(); //데이터 바뀌었다고 공지(어댑터에)
                    }
                });
    }

    //뒤로 가기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getList();
    }

    //shop의 어댑터 정의
    class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

        @NonNull
        @Override
        public ShopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_shop, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShopAdapter.ViewHolder holder, int position) {
            ShopVO vo = array.get(position); //array(list)의 position번째를 꺼내와서
            holder.title.setText(vo.getTitle()); //holder에 하나씩 넣기
            holder.email.setText(vo.getEmail());
            DecimalFormat df = new DecimalFormat("#,###원");
            holder.price.setText(df.format(vo.getPrice()));
            holder.date.setText(vo.getDate());

            //네트워크 이미지 불러와서 출력
            Picasso.with(ShopActivity.this).load(vo.getImage()).into(holder.image);
            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShopActivity.this, ReadActivity.class);
                    intent.putExtra("id", vo.getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, price, date, email;
            ImageView image;
            RelativeLayout item;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                price = itemView.findViewById(R.id.price);
                email = itemView.findViewById(R.id.email);
                date = itemView.findViewById(R.id.date);
                image = itemView.findViewById(R.id.image);
                item = itemView.findViewById(R.id.item);
            }
        }
    }

} //Activity