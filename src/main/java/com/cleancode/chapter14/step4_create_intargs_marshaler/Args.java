package com.cleancode.chapter14.step4_create_intargs_marshaler;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Step.4 新增判斷顯示有存在的數字
 * 
 * @author Ark Chang
 */
public class Args {

	private String schema; // 格式
	private String[] args; // 檢測參數
	private boolean valid = true; // 執行結果
	private Set<Character> unexpectedArguments = new TreeSet<>(); // 不符合預期的參數
	private Map<Character, ArgumentMarshaler> booleanArgs = new HashMap<>(); // 參數是否有Boolean
	private Map<Character, ArgumentMarshaler> stringArgs = new HashMap<>(); // 參數是否有字串
	private Map<Character, ArgumentMarshaler> intArgs = new HashMap<>(); // 參數是否有數字
	private Set<Character> argsFound = new HashSet<>(); // 陣列數
	private int currentArgument;
	private char errorArgumentId = '\0'; // 錯誤的字串
	private String errorParameter = "TILT";

	enum ErrorCode {
		OK, MISSING_BOOLEAN, INVALID_BOOLEAN, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER
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
			case MISSING_BOOLEAN:
				return String.format("Could not find boolean parameter for -%c.", errorArgumentId);
			case INVALID_BOOLEAN:
				return String.format("Argument -%c expects an boolean but was '%s'.", errorArgumentId, errorParameter);
			case MISSING_STRING:
				return String.format("Could not find string parameter for -%c.", errorArgumentId);
			case MISSING_INTEGER:
				return String.format("Could not find integer parameter for -%c.", errorArgumentId);
			case INVALID_INTEGER:
				return String.format("Argument -%c expects an integer but was '%s'.", errorArgumentId, errorParameter);
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
		ArgumentMarshaler am = booleanArgs.get(arg);
		return am != null && am.getBoolean();
	}

	/**
	 * @return 回傳存在的字串
	 */
	public String getString(char arg) {
		ArgumentMarshaler am = stringArgs.get(arg);
		return am == null ? "" : am.getString();
	}

	public int getInt(char arg) {
		ArgumentMarshaler am = intArgs.get(arg);
		return am == null ? 0 : am.getInteger();
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
		try {
			parseArguments();
		} catch (ArgsException e) {
			e.printStackTrace();
		}
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
		} else if (isIntegerSchemaElement(elementTail)) {
			parseIntegerSchemaElement(elementId);
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

	private boolean isIntegerSchemaElement(String elementTail) {
		return elementTail.equals("#");
	}

	private void parseBooleanSchemaElement(char elementId) {
		booleanArgs.put(elementId, new BooleanArgumentMarshaler());
	}

	private void parseStringSchemaElement(char elementId) {
		stringArgs.put(elementId, new StringArgumentMarshaler());
	}

	private void parseIntegerSchemaElement(char elementId) {
		intArgs.put(elementId, new IntegerArgumentMarshaler());
	}

	private boolean parseArguments() throws ArgsException {
		for (currentArgument = 0; currentArgument < args.length; currentArgument++) {
			String arg = args[currentArgument];
			parseArgument(arg);
		}
		return true;
	}

	private void parseArgument(String arg) throws ArgsException {
		if (arg.startsWith("-")) {
			parseElements(arg);
		}
	}

	private void parseElements(String arg) throws ArgsException {
		for (int i = 1; i < arg.length(); i++) {
			parseElement(arg.charAt(i));
		}
	}

	private void parseElement(char argChar) throws ArgsException {
		if (setArgument(argChar)) {
			argsFound.add(argChar);
		} else {
			unexpectedArguments.add(argChar);
			valid = false;
		}
	}

	private boolean setArgument(char argChar) throws ArgsException {
		boolean set = true;
		if (isBoolean(argChar)) {
			setBooleanArg(argChar);
		} else if (isString(argChar)) {
			setStringArg(argChar);
		} else if (isInteger(argChar)) {
			setIntegerArg(argChar);
		} else {
			set = false;
		}
		return set;
	}

	private boolean isBoolean(char argChar) {
		return booleanArgs.containsKey(argChar);
	}

	private void setBooleanArg(char argChar) throws ArgsException {
		currentArgument++;
		String parameter = null;
		try {
			parameter = args[currentArgument];
			booleanArgs.get(argChar).setBoolean(Boolean.parseBoolean(parameter));
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
			errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_BOOLEAN;
			throw new ArgsException();

		} catch (NumberFormatException e) {
			valid = false;
			errorArgumentId = argChar;
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_BOOLEAN;
			throw new ArgsException();
		}
	}

	private boolean isString(char argChar) {
		return stringArgs.containsKey(argChar);
	}

	private void setStringArg(char argChar) throws ArgsException {
		currentArgument++;
		try {
			stringArgs.get(argChar).setString(args[currentArgument]);
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
			errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_STRING;
			throw new ArgsException();
		}
	}

	private boolean isInteger(char argChar) {
		return intArgs.containsKey(argChar);
	}

	private void setIntegerArg(char argChar) throws ArgsException {
		currentArgument++;
		String parameter = null;
		try {
			parameter = args[currentArgument];
			intArgs.get(argChar).setInteger(Integer.parseInt(parameter));
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
			errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_INTEGER;
			throw new ArrayIndexOutOfBoundsException();

		} catch (NumberFormatException e) {
			valid = false;
			errorArgumentId = argChar;
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_INTEGER;
			throw new NumberFormatException();
		}
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

	private class ArgumentMarshaler {
		private boolean booleanValue = false;
		private String stringValue;
		private int integerValue;

		public void setBoolean(boolean value) {
			this.booleanValue = value;
		}

		public boolean getBoolean() {
			return booleanValue;
		}

		public void setString(String value) {
			this.stringValue = value;
		}

		public String getString() {
			return stringValue == null ? "" : stringValue;
		}

		public void setInteger(int value) {
			this.integerValue = value;
		}

		public int getInteger() {
			return integerValue;
		}

	}

	// 修正課本錯誤，兩層Inner Class會不讀到
	private class BooleanArgumentMarshaler extends ArgumentMarshaler {

	}

	private class StringArgumentMarshaler extends ArgumentMarshaler {

	}

	private class IntegerArgumentMarshaler extends ArgumentMarshaler {

	}

	private class ArgsException extends Exception {

	}
}
