package com.cleancode.chapter14.step3;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Step.3 新增ArgumentMarshaler
 * 
 * @author Ark Chang
 */
public class Args {

	private String schema; // 格式
	private String[] args; // 檢測參數
	private boolean valid = true; // 執行結果
	private Set<Character> unexpectedArguments = new TreeSet<>(); // 不符合預期的參數
	private Map<Character, ArgumentMarshaler> booleanArgs = new HashMap<>(); // 是否存在的處理結果
	private Map<Character, String> stringArgs = new HashMap<>(); // 已存在的字串處理結果
	private Set<Character> argsFound = new HashSet<>(); // 陣列數
	private int currentArgument;
	private char errorArgument = '\0'; // 錯誤的字串

	enum ErrorCode {
		OK, MISSING_STRING
	}

	private ErrorCode errorCode = ErrorCode.OK;

	public Args(String schema, String[] args) throws ParseException {
		this.schema = schema;
		this.args = args;
		valid = parse();
	}

	/**
	 * @return 出現次數
	 */
	public int cardinality() {
		return argsFound.size();
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

	public String errorMessage() throws Exception {
		if (unexpectedArguments.size() > 0) {
			return unexpectedArgumentMessage();
		} else {
			switch (errorCode) {
			case MISSING_STRING:
				return String.format("Could not find string parameter for -%c.", errorArgument);
			case OK:
				throw new Exception("TILT: Should not get here.");
			}
		}
		return "";
	}

	/**
	 * @return 規定參數是否存在
	 */
	public boolean getBoolean(char arg) {
		return falseIfNull(booleanArgs.get(arg));
	}

	/**
	 * @return 回傳存在的字串
	 */
	public String getString(char arg) {
		return blankIfNull(stringArgs.get(arg));
	}

	public boolean has(char arg) {
		return argsFound.contains(arg);
	}

	public boolean isValid() {
		return valid;
	}

	private boolean parse() throws ParseException {
		if (schema.length() == 0 && args.length == 0) {
			return true;
		}
		parseSchema();
		parseArguments();
		return valid;
	}

	private boolean parseSchema() throws ParseException {
		for (String element : schema.split(",")) {
			if (element.length() > 0) {
				String trimmedElement = element.trim();
				parseSchemaElement(trimmedElement);
			}
		}
		return true;
	}

	private void parseSchemaElement(String element) throws ParseException {
		char elementId = element.charAt(0);
		String elementTail = element.substring(1);
		validateSchemaElementId(elementId);

		if (isBooleanSchemaElement(elementTail)) {
			parseBooleanSchemaElement(elementId);
		} else if (isStringSchemaElement(elementTail)) {
			parseStringSchemaElement(elementId);
		}
	}

	private void validateSchemaElementId(char elementId) throws ParseException {
		if (!Character.isLetter(elementId)) {
			throw new ParseException("Bad character:" + elementId + "in Args format:" + schema, 0);
		}
	}

	private boolean isBooleanSchemaElement(String elementTail) {
		return elementTail.length() == 0;
	}

	private boolean isStringSchemaElement(String elementTail) {
		return elementTail.equals("*");
	}

	private void parseBooleanSchemaElement(char elementId) {
		booleanArgs.put(elementId, new BooleanArgumentMarshaler());
	}

	private void parseStringSchemaElement(char elementId) {
		stringArgs.put(elementId, "");
	}

	private boolean parseArguments() {
		for (currentArgument = 0; currentArgument < args.length; currentArgument++) {
			String arg = args[currentArgument];
			parseArgument(arg);
		}
		return true;
	}

	private void parseArgument(String arg) {
		if (arg.startsWith("-")) {
			parseElements(arg);
		}
	}

	private void parseElements(String arg) {
		for (int i = 1; i < arg.length(); i++) {
			parseElement(arg.charAt(i));
		}
	}

	private void parseElement(char argChar) {
		if (setArgument(argChar)) {
			argsFound.add(argChar);
		} else {
			unexpectedArguments.add(argChar);
			valid = false;
		}
	}

	private boolean setArgument(char argChar) {
		boolean set = true;
		if (isBoolean(argChar)) {
			setBooleanArg(argChar, true);
		} else if (isString(argChar)) {
			setStringArg(argChar, "");
		} else {
			set = false;
		}
		return set;
	}

	private boolean isBoolean(char argChar) {
		return booleanArgs.containsKey(argChar);
	}

	private void setBooleanArg(char argChar, boolean value) {
		booleanArgs.put(argChar, value);
	}

	private boolean isString(char argChar) {
		return stringArgs.containsKey(argChar);
	}

	private void setStringArg(char argChar, String value) {

		try {
			stringArgs.put(argChar, args[currentArgument]);
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
			errorArgument = argChar;
			errorCode = ErrorCode.MISSING_STRING;
		}
		currentArgument++;
	}

	private String unexpectedArgumentMessage() {
		StringBuffer message = new StringBuffer("Argument(s) -");
		for (char c : unexpectedArguments) {
			message.append(c);
		}
		message.append(" unexpected.");
		return message.toString();
	}

	private boolean falseIfNull(Boolean b) {
		return b == null ? false : b;
	}

	private String blankIfNull(String s) {
		return s == null ? "" : s;
	}

	// XXX 兩層Inner Class是好的寫法嗎?
	private class ArgumentMarshaler {
		private boolean booleanValue = false;

		public void setBoolean(boolean value) {
			booleanValue = value;
		}

		public boolean getBoolean() {
			return booleanValue;
		}

		private class BooleanArgumentMarshaler extends ArgumentMarshaler {

		}

		private class StringArgumentMarshaler extends ArgumentMarshaler {

		}

		private class IntegerArgumentMarshaler extends ArgumentMarshaler {

		}
	}
}
