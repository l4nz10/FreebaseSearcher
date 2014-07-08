package searcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Main {

	public static void main(String[] args) throws IOException {
		
		CsvExtractor extractor = new CsvExtractor("data.csv");
		
		ReconciliationHandler rh = new ReconciliationHandler();
		TopicHandler th = new TopicHandler();
		CsvWriterBasketball cwb = new CsvWriterBasketball("basketball_complete.csv");
		
		
//		sendOnce(extractor, rh, th, cwb);

		while (extractor.nextLine()) {
			String kind = "/basketball/basketball_player";
			String playerName = extractor.get("name");
			Multimap<String, String> params = buildParams(extractor);
			if (rh.sendRequest(kind, playerName, params)) {
				if (th.sendRequest(rh.getMID())) {
					cwb.writeOnCsv(th.getTopic(),
								   playerName,
								   getDateOfBirth(extractor.get("info")),
								   getPlaceOfBirth(extractor.get("info")));
				}
			}			
		}
		extractor.close();
	}
	
	private static Multimap<String, String> buildParams(CsvExtractor extractor) {
		Multimap<String, String> params = HashMultimap.create();
		String info = extractor.get("info");
		String dateOfBirth = getDateOfBirth(info);
		if (dateOfBirth != null)
			params.put("prop", "/people/person/date_of_birth:" + dateOfBirth);
		String placeOfBirth = getPlaceOfBirth(info);
		if (placeOfBirth != null)
			params.put("prop", "/people/person/place_of_birth:" + placeOfBirth);
		return params.isEmpty() ? null : params; 
	}
	
	@SuppressWarnings("unused")
	private static void sendOnce(CsvExtractor extractor, ReconciliationHandler rh, TopicHandler th, CsvWriterBasketball cwb) throws IOException {
		String kind = "/basketball/basketball_player";
		String playerName = extractor.get("name");
		Multimap<String, String> params = buildParams(extractor);
		if (rh.sendRequest(kind, playerName, params)) {
			if (th.sendRequest(rh.getMID())) {
				cwb.writeOnCsv(th.getTopic(),
							   playerName,
							   getDateOfBirth(extractor.get("info")),
							   getPlaceOfBirth(extractor.get("info")));
			}
		}
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
	
	private static String getPlaceOfBirth(String field) {
		String[] infoList= getListFromCsv(field);
		if (infoList == null || infoList.length == 0)
			return null;
		String birthInfo = infoList[0].toLowerCase();
		String birthInfoNoSpace = birthInfo.replaceAll(" ", "");
		int startIndex = birthInfoNoSpace.indexOf("in");
		if (startIndex == -1)
			return null;
		String trimmed = birthInfoNoSpace.substring(startIndex + 2);
		try{
			trimmed = trimmed.substring(0, trimmed.indexOf('('));
		}catch(StringIndexOutOfBoundsException e){
			System.err.println("STRING: "+trimmed);
		}
		if (trimmed.contains(","))
			return trimmed.substring(0, trimmed.indexOf(','));
		return trimmed;
			
	}
}