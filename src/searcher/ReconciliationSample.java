package searcher;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReconciliationSample {

	public static void main(String[] args) throws IOException {
		
		CsvExtractor extractor = new CsvExtractor("data.csv");
		
		ReconciliationHandler rh = new ReconciliationHandler();
		TopicHandler th = new TopicHandler();
		CsvWriterBasketball cwb = new CsvWriterBasketball("basketball.csv");

		while (extractor.nextLine()) {
			String kind = "/basketball/basketball_player";
			String playerName = extractor.get("name");
			Map<String, Collection<String>> reconciliationParams = new HashMap<String, Collection<String>>();
			String team = extractor.get("team");
			List<String> values = new LinkedList<String>();
			if (team != null && !team.isEmpty())
				values.add("/sports/pro_athlete/teams/team:" + team);
				String freebaseDate = textDate2FreebaseDate(extractor.get("info"));
				String placeOfBirth = getPlaceofBirth(extractor.get("info"));
				values.add("/people/person/date_of_birth:" + freebaseDate);
				values.add("/people/person/place_of_birth:" + placeOfBirth);
				reconciliationParams.put("prop", values);
			if (rh.sendRequest(kind, playerName, reconciliationParams)) {
				if (th.sendRequest(rh.getMID())) {
					cwb.writeOnCsv(th.getTopic());
				}
			}			
		}
	}
	
	private static String[] getListFromCsv(String field) {
		if (field == null || !(field.startsWith("[") && field.endsWith("]")))
			return null;
		field = field.substring(1, field.length()-2);
		return field.split("[|]");
	}
	
	private static String textDate2FreebaseDate(String field) {
		Map<String, String> month2number = new HashMap<String, String>();
		month2number.put("jan", "01");
		month2number.put("feb", "02");
		month2number.put("mar", "03");
		month2number.put("apr", "04");
		month2number.put("may", "05");
		month2number.put("jun", "06");
		month2number.put("jul", "07");
		month2number.put("aug", "08");
		month2number.put("sep", "09");
		month2number.put("oct", "10");
		month2number.put("nov", "11");
		month2number.put("dec", "12");
		String birthInfo = getListFromCsv(field)[0];
		String biWithoutSpaces = birthInfo.replaceAll(" ", "").toLowerCase();
		String trimmed = biWithoutSpaces.substring("born".length());
		String textMonth = trimmed.substring(0,3);
		trimmed = trimmed.substring(textMonth.length());
		String day = trimmed.substring(0, trimmed.indexOf(','));
		if(day.length() <2 )
			day = "0"+day;
		trimmed = trimmed.substring(trimmed.indexOf(',')+1);
		String year = trimmed.substring(0,4);
		String month = month2number.get(textMonth);
		return year + "-" + month + "-" + day;
	}
	
	private static String getPlaceofBirth(String field) {
		String birthInfo = getListFromCsv(field)[0];
		String biWithoutSpaces = birthInfo.replaceAll(" ", "");
		int startIn = biWithoutSpaces.indexOf("in");
		String trimmed = biWithoutSpaces.substring(startIn + 2);
		trimmed = trimmed.substring(0, trimmed.indexOf('('));
		if (trimmed.contains(","))
			return trimmed.substring(0, trimmed.indexOf(','));
		return trimmed;
			
	}
	
}