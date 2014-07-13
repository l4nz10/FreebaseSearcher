package utils;

public class ResultBuilder {
	public static Result buildOKResult(double score, double propertiesScore) {
		String description = propertiesScore == 1.0 ? "Match found." : "Match found. Some properties do not match."; 
		return new Result(true, description, score);
	}

	public static Result buildBadResult() {
		return new Result(false, "No matches found.");
	}

	public static Result buildErrorResult() {
		return new Result(false, "Invalid subject or statement.");
	}
}
