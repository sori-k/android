package com.example.ex05;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlogFragment extends Fragment {
    String query = "이효리";
    int page = 1;
    boolean is_end = false;

    List<HashMap<String, Object>> array = new ArrayList<>();
    ListView list;
    BlogAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blog, container, false);

        new BlogThread().execute(); //동시실행

        list = view.findViewById(R.id.list);
        adapter = new BlogAdapter();
        list.setAdapter(adapter);

        EditText edtQuery = view.findViewById(R.id.query);

        //검색버튼 눌렀을때
        view.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = edtQuery.getText().toString();
                array = new ArrayList<>();
                new BlogThread().execute();
            }
        });

        //더보기 버튼 눌렀을때
        view.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(is_end){
                    Toast.makeText(getActivity(), "마지막 페이지입니다.", Toast.LENGTH_SHORT).show();
                }else{
                    page += 1;
                    new BlogThread().execute();
                }
            }
        });
        return view;
    }

    //Blog 쓰레드 생성
    class BlogThread extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) { //main 쓰레드 뒤에서 작동 (동시에 실행)
            String url = "https://dapi.kakao.com/v2/search/web?query=" + query + "&page=" + page;
            String result = KakaoAPI.connect(url); //결과가 string으로 리턴
            System.out.println("........................" + result);

            return result;
        }

        @Override
        protected void onPostExecute(String s) { //결과 result를 s로 받음
            super.onPostExecute(s);
            blogParser(s);

            //System.out.println("데이터갯수:" + array.size());
            adapter.notifyDataSetChanged();
        }
    }

    public void blogParser(String result) { //result에 위의 blogParser에서 받은 s가 들어옴
        try{
            JSONObject meta = new JSONObject(result).getJSONObject("meta");
            is_end = meta.getBoolean("is_end");

            JSONArray jArray = new JSONObject(result).getJSONArray("documents");
            for(int i = 0; i < jArray.length(); i++){
                JSONObject obj = jArray.getJSONObject(i);
                HashMap<String, Object> map = new HashMap<>();
                map.put("title", obj.getString("title")); //처음 "내가만든이름", 두번째 name:"문서에 있는 이름"
                map.put("url", obj.getString("url"));
                map.put("contents", obj.getString("contents"));

                array.add(map);
            }
        }catch (Exception e){
            System.out.println("파서오류:" + e.toString());
        }
    }

    //어댑터 생성
    class BlogAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view = getLayoutInflater().inflate(R.layout.item_blog, parent, false);

            //내용넣기
            HashMap<String, Object> map = array.get(position);

            TextView title = view.findViewById(R.id.title);
            String strTitle = map.get("title").toString(); //html tag 지우기
            title.setText(Html.fromHtml(strTitle));

            TextView contents = view.findViewById(R.id.contents);
            String strContents = map.get("contents").toString();
            contents.setText(Html.fromHtml(strContents));

            //위의 블로그 내용을 누르면 블로그로 이동
            String url = map.get("url").toString();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });
            return view;
        }
    }
}