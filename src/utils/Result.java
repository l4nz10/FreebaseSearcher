package utils;

public class Result {
	
	private boolean outcome;
	private String description, longDescription;
	private double score;
	
	public Result(boolean outcome, String description, String longDescription, double score) {
		setOutcome(outcome);
		setDescription(description);
		setLongDescription(longDescription);
		setScore(score);
	}
	
	public Result(boolean outcome, String description,  String longDescription) {
		this(outcome, description, longDescription, 0);
	}
	
	public boolean isOutcome() {
		return outcome;
	}
	
	public void setOutcome(boolean outcome) {
		this.outcome = outcome;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Outcome:\t\t").append(isOutcome()).append("\n")
			   .append("Description:\t\t").append(getDescription()).append("\n")
			   .append("Long description:\t").append(getLongDescription()).append("\n")
			   .append("Score:\t\t\t").append(getScore());
		return builder.toString();
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
}
