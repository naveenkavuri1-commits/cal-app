package com.example.compkncalculator;

import java.util.*;

public class compknConverterEngine {
	public enum compknCategory { CURRENCY, LENGTH, MASS, AREA, TIME, FINANCE, DATA, DATE, DISCOUNT, VOLUME, NUMERAL, SPEED, TEMPERATURE, BMI, GST }

	public static List<String> compknGetUnitsForCategory(compknCategory cat) {
		switch (cat) {
			case CURRENCY: return Arrays.asList("USD", "EUR", "INR");
			case LENGTH: return Arrays.asList("m", "km", "cm", "mm", "mi", "ft");
			case MASS: return Arrays.asList("kg", "g", "lb", "oz");
			case AREA: return Arrays.asList("m²", "km²", "ft²", "acre");
			case TIME: return Arrays.asList("s", "min", "hr", "day");
			case FINANCE: return Arrays.asList("Principal", "Rate%", "Years");
			case DATA: return Arrays.asList("B", "KB", "MB", "GB");
			case DATE: return Arrays.asList("Days", "Weeks", "Months", "Years");
			case DISCOUNT: return Arrays.asList("Price", "Discount%", "Final");
			case VOLUME: return Arrays.asList("L", "mL", "gal", "cup");
			case NUMERAL: return Arrays.asList("Dec", "Bin", "Hex");
			case SPEED: return Arrays.asList("m/s", "km/h", "mph");
			case TEMPERATURE: return Arrays.asList("C", "F", "K");
			case BMI: return Arrays.asList("kg/m²", "lb/ft²");
			case GST: return Arrays.asList("Price", "GST%", "Total");
			default: return Collections.singletonList("Unit");
		}
	}

	public static double compknConvert(compknCategory cat, String from, String to, double value) {
		switch (cat) {
			case CURRENCY: return value * compknGetCurrencyRate(from, to);
			case LENGTH: return value * compknLinearFactor(from) / compknLinearFactor(to);
			case MASS: return value * compknMassFactor(from) / compknMassFactor(to);
			case AREA: return value * compknAreaFactor(from) / compknAreaFactor(to);
			case TIME: return value * compknTimeFactor(from) / compknTimeFactor(to);
			case DATA: return value * compknDataFactor(from) / compknDataFactor(to);
			case VOLUME: return value * compknVolumeFactor(from) / compknVolumeFactor(to);
			case SPEED: return value * compknSpeedFactor(from) / compknSpeedFactor(to);
			case TEMPERATURE: return compknConvertTemp(from, to, value);
			case NUMERAL: return compknConvertNumeral(from, to, value);
			case FINANCE: return compknSimpleInterest(value, from, to);
			case DISCOUNT: return compknDiscount(value, from, to);
			case DATE: return compknDateUnits(value, from, to);
			case BMI: return compknBmi(value, from, to);
			case GST: return compknGst(value, from, to);
			default: return value;
		}
	}

	private static double compknGetCurrencyRate(String from, String to) {
		Map<String, Double> usd = new HashMap<>();
		usd.put("USD", 1.0);
		usd.put("EUR", 0.92);
		usd.put("INR", 83.0);
		double inUsd;
		if (from.equals("USD")) inUsd = 1.0;
		else if (from.equals("EUR")) inUsd = 1.0 / 0.92;
		else if (from.equals("INR")) inUsd = 1.0 / 83.0;
		else inUsd = 1.0;
		double toRate = usd.getOrDefault(to, 1.0);
		return inUsd * toRate;
	}

	private static double compknLinearFactor(String unit) {
		switch (unit) {
			case "m": return 1.0;
			case "km": return 1000.0;
			case "cm": return 0.01;
			case "mm": return 0.001;
			case "mi": return 1609.344;
			case "ft": return 0.3048;
			default: return 1.0;
		}
	}
	private static double compknMassFactor(String unit) {
		switch (unit) {
			case "kg": return 1.0;
			case "g": return 0.001;
			case "lb": return 0.45359237;
			case "oz": return 0.0283495231;
			default: return 1.0;
		}
	}
	private static double compknAreaFactor(String unit) {
		switch (unit) {
			case "m²": return 1.0;
			case "km²": return 1_000_000.0;
			case "ft²": return 0.09290304;
			case "acre": return 4046.8564224;
			default: return 1.0;
		}
	}
	private static double compknTimeFactor(String unit) {
		switch (unit) {
			case "s": return 1.0;
			case "min": return 60.0;
			case "hr": return 3600.0;
			case "day": return 86400.0;
			default: return 1.0;
		}
	}
	private static double compknDataFactor(String unit) {
		switch (unit) {
			case "B": return 1.0;
			case "KB": return 1024.0;
			case "MB": return 1024.0 * 1024.0;
			case "GB": return 1024.0 * 1024.0 * 1024.0;
			default: return 1.0;
		}
	}
	private static double compknVolumeFactor(String unit) {
		switch (unit) {
			case "L": return 1.0;
			case "mL": return 0.001;
			case "gal": return 3.78541;
			case "cup": return 0.236588;
			default: return 1.0;
		}
	}
	private static double compknSpeedFactor(String unit) {
		switch (unit) {
			case "m/s": return 1.0;
			case "km/h": return 1000.0 / 3600.0;
			case "mph": return 1609.344 / 3600.0;
			default: return 1.0;
		}
	}
	private static double compknConvertTemp(String from, String to, double v) {
		double c;
		switch (from) {
			case "C": c = v; break;
			case "F": c = (v - 32.0) * 5.0/9.0; break;
			case "K": c = v - 273.15; break;
			default: c = v;
		}
		switch (to) {
			case "C": return c;
			case "F": return c * 9.0/5.0 + 32.0;
			case "K": return c + 273.15;
			default: return v;
		}
	}
	private static double compknConvertNumeral(String from, String to, double v) {
		long dec = Math.round(v);
		if (to.equals(from)) return dec;
		if (to.equals("Dec")) return dec;
		if (to.equals("Bin")) return Long.parseLong(Long.toBinaryString(dec));
		if (to.equals("Hex")) {
			// Cannot return hex string as double; approximate by decimal value
			return dec; // UI could be improved to show string; kept numeric for simplicity
		}
		return dec;
	}
	private static double compknSimpleInterest(double val, String from, String to) {
		// Treat: from=Principal, to=Final with fixed rate/years through spinners. This is a placeholder.
		return val; // For a full UI, separate inputs are needed. Kept neutral.
	}
	private static double compknDiscount(double val, String from, String to) {
		return val; // Placeholder without multi-input UI.
	}
	private static double compknDateUnits(double val, String from, String to) {
		double days;
		switch (from) {
			case "Days": days = val; break;
			case "Weeks": days = val * 7.0; break;
			case "Months": days = val * 30.0; break;
			case "Years": days = val * 365.0; break;
			default: days = val;
		}
		switch (to) {
			case "Days": return days;
			case "Weeks": return days / 7.0;
			case "Months": return days / 30.0;
			case "Years": return days / 365.0;
			default: return val;
		}
	}
	private static double compknBmi(double val, String from, String to) {
		return val; // Placeholder; BMI needs weight and height simultaneously; simplified out.
	}
	private static double compknGst(double val, String from, String to) {
		return val; // Placeholder; requires price and percentage in separate inputs.
	}
}