package com.example.compkncalculator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class compknMainPagerAdapter extends FragmentStateAdapter {
	public compknMainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
		super(fragmentActivity);
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		if (position == 0) {
			return new compknCalculatorFragment();
		} else {
			return new compknConverterFragment();
		}
	}

	@Override
	public int getItemCount() {
		return 2;
	}
}