package com.example.ex06;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ShopFragment extends Fragment {
    String query = "스타일러";
    ArrayList<HashMap<String, Object>> array = new ArrayList<>();
    ShopAdapter adapter = new ShopAdapter();
    int page = 1;
    int total = 0;
    int start = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        
        //thread 실행
        new ShopThread().execute();

        RecyclerView list = view.findViewById(R.id.list);
        list.setAdapter(adapter);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL); //몇개씩 출력할지, vertical(세로방향)
        list.setLayoutManager(manager);

        //검색버튼 눌렀을때
        view.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                //새로운 검색 위해 array 비우기
                array = new ArrayList<>();

                EditText edtQuery = view.findViewById(R.id.query);
                query = edtQuery.getText().toString();
                new ShopThread().execute();
            }
        });

        view.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page >= 10){
                    Toast.makeText(getActivity(), "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    page += 1;
                    start = (page-1) * 10 + 1;
                    new ShopThread().execute(); //thread 실행
                }
            }
        });
        return view;
    }

    class ShopThread extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String url = "https://openapi.naver.com/v1/search/shop.json";

            String result = NaverAPI.con(url, query, start);
            return result;
        }

        @Override
        protected void onPostExecute(String s) { //위의 result를 s로 보냄
            super.onPostExecute(s);
            shopParser(s);
            //System.out.println("데이터갯수: " + array.size());
            adapter.notifyDataSetChanged();
        }
    }

    public void shopParser(String result) {
        try{
            //JSONObject objTotal = new JSONObject(result).getJSONObject("total");
            //total = Integer.parseInt(objTotal.toString());

            JSONArray jArray = new JSONObject(result).getJSONArray("items");
            for(int i = 0; i < jArray.length(); i++){
                JSONObject obj = jArray.getJSONObject(i);
                HashMap<String, Object> map = new HashMap<>();
                map.put("title", obj.getString("title"));
                map.put("link", obj.getString("link"));
                map.put("price", obj.getInt("lprice"));
                map.put("image", obj.getString("image"));
                array.add(map); //array에 파싱한 결과가 들어감
            }
        }catch (Exception e){
            System.out.println("파싱오류: " + e.toString());
        }
    }

    //쇼핑 어댑터 생성
    class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {
        @NonNull
        @Override
        public ShopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_shop, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShopAdapter.ViewHolder holder, int position) { // data 읽어들이기
            HashMap<String, Object> map = array.get(position);
            holder.title.setText(Html.fromHtml(map.get("title").toString()));
            int intPrice = Integer.parseInt(map.get("price").toString());
            DecimalFormat df = new DecimalFormat("#,###원");
            holder.price.setText(df.format(intPrice));

            //상품 이미지 가져오기
            String strImage = map.get("image").toString();
            if(strImage.equals("")){
                holder.image.setImageResource(R.drawable.baseline_add_shopping_cart_24);
            }else{
                Picasso.with(getActivity()).load(strImage).into(holder.image);
            }

            holder.item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("link", map.get("link").toString());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder { //id 읽기
            TextView title, price;
            ImageView image;
            CardView item;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                price = itemView.findViewById(R.id.price);
                image = itemView.findViewById(R.id.image);
                item = itemView.findViewById(R.id.item);
            }
        }
    }
}