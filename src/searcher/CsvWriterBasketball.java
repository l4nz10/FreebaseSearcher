package searcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvWriterBasketball {
	
	private String fileName;

	public CsvWriterBasketball(String fileName) {
		this.fileName = fileName;
	}
	
	private String getDateOfBirth(JSONObject topic) {
		try {
			return JsonPath.read(topic, "$.property['/people/person/date_of_birth'].values[0].value").toString();
		} catch (PathNotFoundException e) {
			return "";
		}
	}
	
	private String getPlaceOfBirth(JSONObject topic) {
		try {
			return JsonPath.read(topic, "$.property['/people/person/place_of_birth'].values[0].text").toString();
		} catch (PathNotFoundException e) {
			return "";
		}
	}
	
	private String getName(JSONObject topic) {
		try {
			return JsonPath.read(topic, "$.property['/type/object/name'].values[0].text").toString();
		} catch (PathNotFoundException e) {
			return "";
		}
	}
	
	public void writeOnCsv(JSONObject topic, String origName, String origDate, String origPlace) throws IOException {
		boolean alreadyExists = new File(fileName).exists();
		CSVWriter writer = new CSVWriter(new FileWriter(fileName, true), ';');
		String[] entries;
		
		if (!alreadyExists) {
			entries = "NAME#(O)NAME#DATE_OF_BIRTH#(O)DATE_OF_BIRTH#PLACE_OF_BIRTH#(O)PLACE_OF_BIRTH".split("#");
			writer.writeNext(entries);
		}
		
		entries = new String[6];
		entries[0] = getName(topic);
		entries[1] = origName;
		entries[2] = getDateOfBirth(topic);
		entries[3] = origDate;
		entries[4] = getPlaceOfBirth(topic);
		entries[5] = origPlace;		
		
		writer.writeNext(entries);
		writer.close();
	}

}
