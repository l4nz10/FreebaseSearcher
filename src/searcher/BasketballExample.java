package searcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.CsvExtractor;
import utils.Result;
import verifier.FreebaseVerifier;
import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class BasketballExample {
	
	private static final int LIMIT = 400;
	
	private static String subject, dateOfBirth, team;
	private static Multimap<String, String> properties;
	private static String type = "basketball player";

	public static void main(String[] args) throws IOException {
		
		CsvExtractor extractor = new CsvExtractor("espn.csv");
		File file = new File("basketball_metaschema.csv");
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		String[] entries = "SUBJECT#PROPERTIES#IS#RESULT".split("#");
		writer.writeNext(entries);
		
		int i = 0;
		while (extractor.nextLine() && i < LIMIT) {
			subject = extractor.get("name");
			dateOfBirth = getDateOfBirth(extractor.get("info"));
			properties = HashMultimap.create();
			if (dateOfBirth != null && !dateOfBirth.isEmpty())
				properties.put("date of birth", dateOfBirth);
			team = extractor.get("team");
			if (team != null && !team.isEmpty())
				properties.put("team", team);
			type = "basketball player";
			
			FreebaseVerifier fv = new FreebaseVerifier();
			Result r = fv.subject(subject).setProperties(properties).is(type);
			
			entries = new String[4];
			entries[0] = subject;
			MapJoiner joiner = Joiner.on(',').withKeyValueSeparator(":");
			entries[1] = joiner.join(properties.entries());
			entries[2] = type;
			entries[3] = r.getDescription();
			writer.writeNext(entries);
			
			i++;
		}
		writer.close();
		extractor.close();
		System.out.println("End of example.");
	}
	
	private static String[] getListFromCsv(String field) {
		if (field == null || !(field.startsWith("[") && field.endsWith("]")))
			return null;
		field = field.substring(1, field.length()-2);
		return field.split("##");
	}
	
	private static String getDateOfBirth(String field) {
		String[] infoList = getListFromCsv(field);
		if (infoList == null || infoList.length == 0)
			return null;
		String birthInfo = infoList[0].toLowerCase();
		String birthInfoNoSpace = birthInfo.replaceAll(" ", "");
		String trimmed = birthInfoNoSpace.substring("born".length());
		String monthName = trimmed.substring(0,3);
		trimmed = trimmed.substring(monthName.length());
		String day = trimmed.substring(0, trimmed.indexOf(','));
		day = day.length() == 1 ? "0" + day : day;
		trimmed = trimmed.substring(trimmed.indexOf(',')+1);
		String year = trimmed.substring(0,4);
		Map<String, String> monthName2number = getMonthName2number();
		String month = monthName2number.get(monthName);
		return year + "-" + month + "-" + day;
	}
	
	private static Map<String, String> getMonthName2number() {
		Map<String, String> monthName2number = new HashMap<String, String>();
		monthName2number.put("jan", "01");
		monthName2number.put("feb", "02");
		monthName2number.put("mar", "03");
		monthName2number.put("apr", "04");
		monthName2number.put("may", "05");
		monthName2number.put("jun", "06");
		monthName2number.put("jul", "07");
		monthName2number.put("aug", "08");
		monthName2number.put("sep", "09");
		monthName2number.put("oct", "10");
		monthName2number.put("nov", "11");
		monthName2number.put("dec", "12");
		return monthName2number;
	}
}