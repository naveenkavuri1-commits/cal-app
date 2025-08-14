package com.example.compkncalculator;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class compknMainActivity extends AppCompatActivity {
	private ViewPager2 compknViewPager;
	private TabLayout compknTabLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compkn_main);

		compknViewPager = findViewById(R.id.compkn_view_pager);
		compknTabLayout = findViewById(R.id.compkn_tab_layout);

		compknViewPager.setAdapter(new compknMainPagerAdapter(this));

		new TabLayoutMediator(compknTabLayout, compknViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
			@Override
			public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
				if (position == 0) {
					tab.setText(R.string.compkn_tab_calculator);
				} else {
					tab.setText(R.string.compkn_tab_converter);
				}
			}
		}).attach();
	}
}