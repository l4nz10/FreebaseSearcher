package utils;

public class ResultBuilder {
	public static Result buildOKResult(double score, double propertiesScore) {
		String description, longDescription;
		if (propertiesScore == 1.0) {
			description = "OK";
			longDescription = "Match found.";
		} else {
			description = "UNCERTAIN";
			longDescription = "Match found. Some properties do not match.";
		}
		return new Result(true, description, longDescription, score);
	}

	public static Result buildBadResult() {
		return new Result(false, "KO","No matches found.");
	}

	public static Result buildErrorResult() {
		return new Result(false, "ERROR", "Invalid subject or statement.");
	}
}
