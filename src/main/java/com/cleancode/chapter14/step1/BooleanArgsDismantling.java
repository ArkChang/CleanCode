package com.cleancode.chapter14.step1;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BooleanArgsDismantling {

	private String schema; // 格式
	private String[] args; // 檢測參數
	private boolean valid; // 執行結果
	private int numberOfArguments = 0; // 陣列數
	private Set<Character> unexpectedArguments = new TreeSet<>(); // 不符合預期的參數
	private Map<Character, Boolean> booleanArgs = new HashMap<>(); // 處理結果

	public BooleanArgsDismantling(String schema, String[] args) {
		this.schema = schema;
		this.args = args;
		valid = parse();
	}

	/**
	 * @return 出現次數
	 */
	public int cardinality() {
		return numberOfArguments;
	}

	/**
	 * @return 查詢對象
	 */
	public String usage() {
		if (schema.length() > 0)
			return "-[" + schema + "]";
		else
			return "";
	}

	public String errorMessage() {
		if (unexpectedArguments.size() > 0) {
			return unexpectedArgumentMessage();
		} else {
			return "";
		}
	}

	/**
	 * @return 規定參數是否存在
	 */
	public boolean getBoolean(char arg) {
		return booleanArgs.get(arg);
	}

	public boolean isValid() {
		return valid;
	}

	private boolean parse() {
		if (schema.length() == 0 && args.length == 0)
			return true;
		parseSchema();
		parseArguments();
		return unexpectedArguments.size() == 0;
	}

	private boolean parseSchema() {
		for (String element : schema.split(",")) {
			parseSchemaElement(element);
		}
		return true;
	}

	private void parseSchemaElement(String element) {
		if (element.length() == 1) {
			parseBooleanSchemaElement(element);
		}
	}

	private void parseBooleanSchemaElement(String element) {
		char c = element.charAt(0);
		if (Character.isLetter(c)) {
			booleanArgs.put(c, false);
		}
	}

	private boolean parseArguments() {
		for (String arg : args) {
			parseArgument(arg);
		}
		return true;
	}

	private void parseArgument(String arg) {
		if (arg.startsWith("-"))
			parseElements(arg);
	}

	private void parseElements(String arg) {
		for (int i = 1; i < arg.length(); i++)
			parseElement(arg.charAt(i));
	}

	private void parseElement(char argChar) {
		if (isBoolean(argChar)) {
			numberOfArguments++;
			setBooleanArg(argChar, true);
		} else {
			unexpectedArguments.add(argChar);
		}
	}

	private boolean isBoolean(char argChar) {
		return booleanArgs.containsKey(argChar);
	}

	private void setBooleanArg(char argChar, boolean value) {
		booleanArgs.put(argChar, value);
	}

	private String unexpectedArgumentMessage() {
		StringBuffer message = new StringBuffer("Argument(s) -");
		for (char c : unexpectedArguments) {
			message.append(c);
		}
		message.append(" unexpected.");
		return message.toString();
	}

}
