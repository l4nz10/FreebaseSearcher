package searcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;

import com.jayway.jsonpath.JsonPath;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvWriterBasketball {
	
	private String fileName;

	public CsvWriterBasketball(String fileName) {
		this.fileName = fileName;
	}
	
	private String getDateOfBirth(JSONObject topic) {
		return JsonPath.read(topic, "$.property['/people/person/date_of_birth'].values[0].value").toString();
	}
	
	private String getPlaceOfBirth(JSONObject topic) {
		return JsonPath.read(topic, "$.property['/people/person/place_of_birth'].values[0].text").toString();
	}
	
	private String getName(JSONObject topic) {
		return JsonPath.read(topic, "$.property['/type/object/name'].values[0].text").toString();
	}
	
	public void writeOnCsv(JSONObject topic) throws IOException {
		boolean alreadyExists = new File(fileName).exists();
		CSVWriter writer = new CSVWriter(new FileWriter(fileName, true), ';');
		String[] entries;
		
		if (!alreadyExists) {
			entries = "NAME#DATE_OF_BIRTH#PLACE_OF_BIRTH".split("#");
			writer.writeNext(entries);
		}
		
		entries = new String[3];
		entries[0] = getName(topic);
		System.out.println(entries[0]);
		entries[1] = getDateOfBirth(topic);
		entries[2] = getPlaceOfBirth(topic);
		writer.writeNext(entries);
		writer.close();
	}

}
