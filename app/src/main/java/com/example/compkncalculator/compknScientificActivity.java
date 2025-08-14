package com.example.compkncalculator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class compknScientificActivity extends AppCompatActivity {
	private TextView compknDisplayExpression;
	private TextView compknDisplayResult;
	private ToggleButton compknSecondToggle;
	private final StringBuilder compknExpressionBuilder = new StringBuilder();
	private boolean compknIsSecond = false;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compkn_scientific);

		compknDisplayExpression = findViewById(R.id.compkn_display_expression);
		compknDisplayResult = findViewById(R.id.compkn_display_result);
		compknSecondToggle = findViewById(R.id.compkn_btn_second);

		compknSecondToggle.setOnCheckedChangeListener((btn, isChecked) -> {
			compknIsSecond = isChecked;
			compknUpdateSecondLabels();
		});

		findViewById(R.id.compkn_btn_back).setOnClickListener(v -> finish());

		int[] numberIds = new int[]{R.id.compkn_btn_0,R.id.compkn_btn_1,R.id.compkn_btn_2,R.id.compkn_btn_3,R.id.compkn_btn_4,R.id.compkn_btn_5,R.id.compkn_btn_6,R.id.compkn_btn_7,R.id.compkn_btn_8,R.id.compkn_btn_9};
		for (int id : numberIds) {
			View v = findViewById(id);
			if (v != null) v.setOnClickListener(view -> compknAppend(((Button) view).getText().toString(), false));
		}

		findViewById(R.id.compkn_btn_plus).setOnClickListener(v -> compknAppend("+", true));
		findViewById(R.id.compkn_btn_minus).setOnClickListener(v -> compknAppend("-", true));
		findViewById(R.id.compkn_btn_mul).setOnClickListener(v -> compknAppend("*", true));
		findViewById(R.id.compkn_btn_div).setOnClickListener(v -> compknAppend("/", true));
		findViewById(R.id.compkn_btn_decimal).setOnClickListener(v -> compknAppend(".", false));
		findViewById(R.id.compkn_btn_percent).setOnClickListener(v -> compknAppend("%", true));
		findViewById(R.id.compkn_btn_lparen).setOnClickListener(v -> compknAppend("(", false));
		findViewById(R.id.compkn_btn_rparen).setOnClickListener(v -> compknAppend(")", false));
		findViewById(R.id.compkn_btn_ac).setOnClickListener(v -> compknClear());
		findViewById(R.id.compkn_btn_del).setOnClickListener(v -> compknBackspace());
		findViewById(R.id.compkn_btn_equals).setOnClickListener(v -> compknEvaluate());
		findViewById(R.id.compkn_btn_recip).setOnClickListener(v -> compknAppend("1/(", false));

		findViewById(R.id.compkn_btn_sin).setOnClickListener(v -> compknAppend(compknIsSecond ? "asin(" : "sin(", false));
		findViewById(R.id.compkn_btn_cos).setOnClickListener(v -> compknAppend(compknIsSecond ? "acos(" : "cos(", false));
		findViewById(R.id.compkn_btn_tan).setOnClickListener(v -> compknAppend(compknIsSecond ? "atan(" : "tan(", false));
		findViewById(R.id.compkn_btn_ln).setOnClickListener(v -> compknAppend(compknIsSecond ? "exp(" : "ln(", false));
		findViewById(R.id.compkn_btn_log).setOnClickListener(v -> compknAppend(compknIsSecond ? "10^" : "log(", false));
		findViewById(R.id.compkn_btn_pi).setOnClickListener(v -> compknAppend("pi", false));
		findViewById(R.id.compkn_btn_e).setOnClickListener(v -> compknAppend("e", false));
		findViewById(R.id.compkn_btn_sqrt).setOnClickListener(v -> compknAppend("sqrt(", false));
		findViewById(R.id.compkn_btn_pow).setOnClickListener(v -> compknAppend("^", true));
		findViewById(R.id.compkn_btn_fact).setOnClickListener(v -> compknAppend("!", true));
	}

	private void compknUpdateSecondLabels() {
		Button ln = findViewById(R.id.compkn_btn_ln);
		Button log = findViewById(R.id.compkn_btn_log);
		Button sin = findViewById(R.id.compkn_btn_sin);
		Button cos = findViewById(R.id.compkn_btn_cos);
		Button tan = findViewById(R.id.compkn_btn_tan);
		if (compknIsSecond) {
			ln.setText("eˣ");
			log.setText("10ˣ");
			sin.setText("sin⁻¹");
			cos.setText("cos⁻¹");
			tan.setText("tan⁻¹");
		} else {
			ln.setText("ln");
			log.setText("log");
			sin.setText("sin");
			cos.setText("cos");
			tan.setText("tan");
		}
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