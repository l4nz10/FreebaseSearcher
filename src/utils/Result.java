package utils;

public class Result {
	
	private boolean outcome;
	private String description;
	private double score;
	
	public Result(boolean outcome, String description, double score) {
		setOutcome(outcome);
		setDescription(description);
		setScore(score);
	}
	
	public Result(boolean outcome, String description) {
		this(outcome, description, 0);
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
		builder.append("Outcome:\t").append(isOutcome()).append("\n")
			   .append("Description:\t").append(getDescription()).append("\n")
			   .append("Score:\t\t").append(getScore());
		return builder.toString();
	}
}
