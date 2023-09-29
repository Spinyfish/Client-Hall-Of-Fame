package com.github.drapostolos.typeparser;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

enum Parsers implements Parser<Object> {
	BYTE(Byte.class, byte.class, (input, helper) -> Byte.valueOf(input.trim())),
	INTEGER(Integer.class, int.class, (input, helper) -> Integer.valueOf(input.trim())),
	LONG(Long.class, long.class, (input, helper) -> Long.valueOf(input.trim())),
	SHORT(Short.class, short.class, (input, helper) -> Short.valueOf(input.trim())),
	FLOAT(Float.class, float.class, (input, helper) -> Float.valueOf(input)),
	DOUBLE(Double.class, double.class, (input, helper) -> Double.valueOf(input)),
	BOOLEAN(Boolean.class, boolean.class, (input, helper) -> {
		String value = input.trim().toLowerCase();
		if ("true".equals(value)) {
			return Boolean.TRUE;
		} else if ("false".equals(value)) {
			return Boolean.FALSE;
		}
		String message = "\"%s\" is not parsable to a Boolean.";
		throw new IllegalArgumentException(String.format(message, input));}),
	CHARCTER(Character.class, char.class, (input, helper) -> {
        if (input.length() == 1) {
            return Character.valueOf(input.charAt(0));
        }
        String message = "\"%s\" must only contain a single character.";
        throw new IllegalArgumentException(String.format(message, input));}),
    BIG_INTEGER(BigInteger.class, (input, helper) -> new BigInteger(input.trim())),
    BIG_DECIMAL(BigDecimal.class, (input, helper) -> {
        try {
            return new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            /*
             * The NumberFormatException thrown by BigDecimal contains
             * an empty error message. The below is done to address that.
             */
            String message = "NumberFormatException For input string: \"" + input + "\"";
            NumberFormatException e2 = new NumberFormatException(message);
            e2.setStackTrace(e.getStackTrace());
            throw e2;
        }}),
    URL(URL.class, (input, helper) -> {
        try {
            return new URL(input.trim());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("MalformedURLException: " + e.getMessage(), e);
        }}),
    URI(URI.class, (input, helper) -> {
        try {
            return new URI(input.trim());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }}),
	FILE(File.class, (input, helper) -> new File(input.trim())),
    STRING(String.class, (input, helper) -> input),
    OBJECT(Object.class, (input, helper) -> input),
    NUMBER(Number.class, (input, helper) -> {
    	/*
    	 * Number formats are generally not synchronized. It is recommended to create 
    	 * separate format instances for each thread. If multiple threads access a 
    	 * format concurrently, it must be synchronized externally.
    	 */
    	final ThreadLocal<NumberFormat> NUMBER_FORMAT = new ThreadLocal<NumberFormat>(){
    		@Override 
    		protected NumberFormat initialValue(){
    			return NumberFormat.getInstance(Locale.US);
    		}
    	};
		try {
			return NUMBER_FORMAT.get().parse(input.trim());
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}}),
    PATH(Path.class, (input,helper) -> Paths.get(input.trim()));

	private static final Map<Type, Parser<?>> DEFAULT_PARSERS;
	private List<Type> types = new ArrayList<>();
	private Parser<Object> parser;

	private Parsers(Type type, Parser<Object> parser) {
		this.parser = parser;
		types.add(type);
	}

	private Parsers(Type type0, Type type1, Parser<Object> parser) {
		this.parser = parser;
		types.add(type0);
		types.add(type1);
	}

	@Override
	public Object parse(String input, ParserHelper helper) {
		return parser.parse(input, helper);
	}

	static {
		DEFAULT_PARSERS = new LinkedHashMap<Type, Parser<?>>();
		for (Parsers parser : values()) {
			for (Type type : parser.types) {
				DEFAULT_PARSERS.put(type, parser);
			}
		}
	}

	static Map<Type, Parser<?>> copyDefault() {
		return new LinkedHashMap<Type, Parser<?>>(DEFAULT_PARSERS);
	}
}
