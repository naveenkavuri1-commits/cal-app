package com.example.compkncalculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class compknCalculatorFragment extends Fragment {
	private TextView compknDisplayExpression;
	private TextView compknDisplayResult;
	private final StringBuilder compknExpressionBuilder = new StringBuilder();

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_compkn_calculator, container, false);
		compknDisplayExpression = root.findViewById(R.id.compkn_display_expression);
		compknDisplayResult = root.findViewById(R.id.compkn_display_result);

		// Open scientific screen
		root.findViewById(R.id.compkn_btn_open_scientific).setOnClickListener(v -> {
			startActivity(new Intent(requireContext(), compknScientificActivity.class));
		});

		int[] numberIds = new int[]{R.id.compkn_btn_0,R.id.compkn_btn_1,R.id.compkn_btn_2,R.id.compkn_btn_3,R.id.compkn_btn_4,R.id.compkn_btn_5,R.id.compkn_btn_6,R.id.compkn_btn_7,R.id.compkn_btn_8,R.id.compkn_btn_9};
		for (int id : numberIds) {
			View v = root.findViewById(id);
			if (v != null) v.setOnClickListener(view -> compknAppend(((Button) view).getText().toString(), false));
		}

		root.findViewById(R.id.compkn_btn_plus).setOnClickListener(v -> compknAppend("+", true));
		root.findViewById(R.id.compkn_btn_minus).setOnClickListener(v -> compknAppend("-", true));
		root.findViewById(R.id.compkn_btn_mul).setOnClickListener(v -> compknAppend("*", true));
		root.findViewById(R.id.compkn_btn_div).setOnClickListener(v -> compknAppend("/", true));
		root.findViewById(R.id.compkn_btn_decimal).setOnClickListener(v -> compknAppend(".", false));
		root.findViewById(R.id.compkn_btn_percent).setOnClickListener(v -> compknAppend("%", true));
		root.findViewById(R.id.compkn_btn_ac).setOnClickListener(v -> compknClear());
		root.findViewById(R.id.compkn_btn_back).setOnClickListener(v -> compknBackspace());
		root.findViewById(R.id.compkn_btn_equals).setOnClickListener(v -> compknEvaluate());

		return root;
	}

	private void compknAppend(String s, boolean isOperator) {
		if (isOperator && compknExpressionBuilder.length() > 0) {
			char last = compknExpressionBuilder.charAt(compknExpressionBuilder.length() - 1);
			if (last == '+' || last == '-' || last == '*' || last == '/' || last == '^' ) {
				compknExpressionBuilder.setCharAt(compknExpressionBuilder.length() - 1, s.charAt(0));
				compknDisplayExpression.setText(compknExpressionBuilder.toString());
				return;
			}
		}
		compknExpressionBuilder.append(s);
		compknDisplayExpression.setText(compknExpressionBuilder.toString());
	}

	private void compknClear() {
		compknExpressionBuilder.setLength(0);
		compknDisplayExpression.setText("");
		compknDisplayResult.setText("");
	}

	private void compknBackspace() {
		if (compknExpressionBuilder.length() > 0) {
			compknExpressionBuilder.deleteCharAt(compknExpressionBuilder.length() - 1);
			compknDisplayExpression.setText(compknExpressionBuilder.toString());
		}
	}

	private void compknEvaluate() {
		String expr = compknExpressionBuilder.toString();
		if (TextUtils.isEmpty(expr)) return;
		try {
			double result = compknMathEngine.evaluate(expr);
			String formatted = (Math.floor(result) == result) ? String.format(Locale.US, "%.0f", result) : String.format(Locale.US, "%s", result);
			compknDisplayResult.setText(formatted);
		} catch (Exception e) {
			compknDisplayResult.setText("Error");
		}
	}
}