package utils;

public class StringHelper {
	
	public static String space2underscore(String s) {
		return s.replaceAll(" ", "_");
	}
	
	public static String removeSpaces(String s) {
		return s.replaceAll(" ", "");
	}
	
	public static String removeQuotes(String s) {
		return s.replaceAll("\"", "");
	}
}
