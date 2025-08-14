package com.example.compkncalculator;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class compknConverterFragment extends Fragment {
	private RecyclerView compknRecyclerCategories;
	private EditText compknInputValue;
	private Spinner compknSpinnerFrom;
	private Spinner compknSpinnerTo;
	private TextView compknResultText;
	private RecyclerView compknRecyclerResults;
	private final List<String> compknUnits = new ArrayList<>();
	private final List<String> compknResults = new ArrayList<>();

	private compknConverterEngine.compknCategory compknSelectedCategory = compknConverterEngine.compknCategory.LENGTH;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_compkn_converter, container, false);
		compknRecyclerCategories = root.findViewById(R.id.compkn_recycler_categories);
		compknInputValue = root.findViewById(R.id.compkn_input_value);
		compknSpinnerFrom = root.findViewById(R.id.compkn_spinner_from);
		compknSpinnerTo = root.findViewById(R.id.compkn_spinner_to);
		compknResultText = root.findViewById(R.id.compkn_text_result);

		compknRecyclerResults = new RecyclerView(requireContext());
		((ViewGroup) root).addView(compknRecyclerResults);
		compknRecyclerResults.setLayoutManager(new LinearLayoutManager(getContext()));
		compknRecyclerResults.setAdapter(new compknResultsAdapter());

		compknSetupCategories();
		compknSetupConverters();
		compknBindListeners();
		return root;
	}

	private void compknSetupCategories() {
		compknRecyclerCategories.setLayoutManager(new GridLayoutManager(getContext(), 3));
		List<String> labels = Arrays.asList(
			getString(R.string.compkn_category_currency),
			getString(R.string.compkn_category_length),
			getString(R.string.compkn_category_mass),
			getString(R.string.compkn_category_area),
			getString(R.string.compkn_category_time),
			getString(R.string.compkn_category_finance),
			getString(R.string.compkn_category_data),
			getString(R.string.compkn_category_date),
			getString(R.string.compkn_category_discount),
			getString(R.string.compkn_category_volume),
			getString(R.string.compkn_category_numeral),
			getString(R.string.compkn_category_speed),
			getString(R.string.compkn_category_temperature),
			getString(R.string.compkn_category_bmi),
			getString(R.string.compkn_category_gst)
		);
		compknRecyclerCategories.setAdapter(new compknCategoryAdapter(labels, position -> {
			switch (position) {
				case 0: compknSelectedCategory = compknConverterEngine.compknCategory.CURRENCY; break;
				case 1: compknSelectedCategory = compknConverterEngine.compknCategory.LENGTH; break;
				case 2: compknSelectedCategory = compknConverterEngine.compknCategory.MASS; break;
				case 3: compknSelectedCategory = compknConverterEngine.compknCategory.AREA; break;
				case 4: compknSelectedCategory = compknConverterEngine.compknCategory.TIME; break;
				case 5: compknSelectedCategory = compknConverterEngine.compknCategory.FINANCE; break;
				case 6: compknSelectedCategory = compknConverterEngine.compknCategory.DATA; break;
				case 7: compknSelectedCategory = compknConverterEngine.compknCategory.DATE; break;
				case 8: compknSelectedCategory = compknConverterEngine.compknCategory.DISCOUNT; break;
				case 9: compknSelectedCategory = compknConverterEngine.compknCategory.VOLUME; break;
				case 10: compknSelectedCategory = compknConverterEngine.compknCategory.NUMERAL; break;
				case 11: compknSelectedCategory = compknConverterEngine.compknCategory.SPEED; break;
				case 12: compknSelectedCategory = compknConverterEngine.compknCategory.TEMPERATURE; break;
				case 13: compknSelectedCategory = compknConverterEngine.compknCategory.BMI; break;
				case 14: compknSelectedCategory = compknConverterEngine.compknCategory.GST; break;
			}
			compknSetupConverters();
			compknRecalc();
		}));
	}

	private void compknSetupConverters() {
		compknUnits.clear();
		compknUnits.addAll(compknConverterEngine.compknGetUnitsForCategory(compknSelectedCategory));
		ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, compknUnits);
		compknSpinnerFrom.setAdapter(adapter);
		compknSpinnerTo.setAdapter(adapter);
		if (compknUnits.size() > 1) compknSpinnerTo.setSelection(1);
	}

	private void compknBindListeners() {
		compknInputValue.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) { compknRecalc(); }
			@Override public void afterTextChanged(Editable s) {}
		});
		compknSpinnerFrom.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { compknRecalc(); }
			@Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
		});
		compknSpinnerTo.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { compknRecalc(); }
			@Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
		});
	}

	private void compknRecalc() {
		String text = compknInputValue.getText().toString().trim();
		if (text.isEmpty()) { compknResultText.setText(getString(R.string.compkn_result)); compknResults.clear(); compknRecyclerResults.getAdapter().notifyDataSetChanged(); return; }
		try {
			double value = Double.parseDouble(text);
			String from = (String) compknSpinnerFrom.getSelectedItem();
			String to = (String) compknSpinnerTo.getSelectedItem();
			double out = compknConverterEngine.compknConvert(compknSelectedCategory, from, to, value);
			compknResultText.setText(String.valueOf(out));

			compknResults.clear();
			for (String unit : compknUnits) {
				double v = compknConverterEngine.compknConvert(compknSelectedCategory, from, unit, value);
				compknResults.add(unit + ": " + v);
			}
			compknRecyclerResults.getAdapter().notifyDataSetChanged();
		} catch (Exception ex) {
			compknResultText.setText("Error");
		}
	}

	private class compknResultsAdapter extends RecyclerView.Adapter<compknResultsAdapter.RVH> {
		class RVH extends RecyclerView.ViewHolder { TextView t; RVH(View v) { super(v); t = (TextView) v; } }
		@NonNull @Override public RVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			TextView t = new TextView(parent.getContext());
			t.setTextColor(getResources().getColor(R.color.compkn_color_on_background));
			t.setTextSize(16);
			t.setPadding(0, 8, 0, 8);
			return new RVH(t);
		}
		@Override public void onBindViewHolder(@NonNull RVH holder, int position) { holder.t.setText(compknResults.get(position)); }
		@Override public int getItemCount() { return compknResults.size(); }
	}

	private static class compknCategoryAdapter extends RecyclerView.Adapter<compknCategoryAdapter.VH> {
		interface OnItemClick { void onClick(int position); }
		private final List<String> items; private final OnItemClick onClick;
		compknCategoryAdapter(List<String> items, OnItemClick onClick) { this.items = items; this.onClick = onClick; }
		static class VH extends RecyclerView.ViewHolder {
			TextView label; VH(View v) { super(v); label = v.findViewById(R.id.compkn_label); }
		}
		@NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_compkn_category, parent, false);
			return new VH(v);
		}
		@Override public void onBindViewHolder(@NonNull VH holder, int position) {
			holder.label.setText(items.get(position));
			holder.itemView.setOnClickListener(v -> onClick.onClick(holder.getAdapterPosition()));
		}
		@Override public int getItemCount() { return items.size(); }
	}
}