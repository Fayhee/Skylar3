package com.example.skylar.hedera;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.skylar.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.skylar.hedera.ui.SectionsPagerAdapter;

public class MainActivity2 extends AppCompatActivity {
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        sectionsPagerAdapter = new SectionsPagerAdapter(this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        new TabLayoutMediator(tabs, viewPager,
            (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText(R.string.tab_title_1);
                        break;
                    case 1:
                        tab.setText(R.string.tab_title_2);
                        break;
                    case 2:
                        tab.setText(R.string.tab_title_3);
                        break;
                    default:
                        tab.setText("unhandled");
                }
            }
        ).attach();
    }
}
