package com.example.ex06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    LinearLayout drawerView;
    TabLayout tab;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        drawerView = findViewById(R.id.drawerView);
        pager = findViewById(R.id.pager);
        tab = findViewById(R.id.tab);

        getSupportActionBar().setTitle("네이버검색");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //이미지지정
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_24);

        //tab 생성
        tab.addTab(tab.newTab().setText("영화"));
        tab.getTabAt(0).setIcon(R.drawable.baseline_movie_24);

        tab.addTab(tab.newTab().setText("쇼핑"));
        tab.getTabAt(1).setIcon(R.drawable.baseline_add_shopping_cart_24);

        tab.addTab(tab.newTab().setText("도서"));
        tab.getTabAt(2).setIcon(R.drawable.baseline_menu_book_24);

        //pager에 fragment 연결
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        //pager가 바뀔때 tab 변경
        pager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

        //tab과 pager 연결
        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { //tab이 바뀔때 pager 변경
                pager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    //옵션 메뉴 열고 닫기
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(drawerLayout.isDrawerOpen(drawerView)){
                drawerLayout.close();
            }else{
                drawerLayout.openDrawer(drawerView);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //어댑터 생성 (Fragment 3개 출력위한)
    class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new MovieFragment();
                case 1:
                    return new ShopFragment();
                case 2:
                    return new BookFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}