package utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class CsvExtractor {
	
	private CSVReader reader;
	private Map<String, Integer> name2index;
	private String[] line = null;
	
	public CsvExtractor(String fileName) throws IOException {
		reader = new CSVReader(new FileReader(fileName), ';');
		initializeFields(reader.readNext());
	}
	
	private void initializeFields(String[] fieldNames) {
		name2index = new HashMap<String, Integer>();
		for (int i = 0; i < fieldNames.length; i++) {
			name2index.put(fieldNames[i].toLowerCase(), i);
		}
	}
	
	public boolean nextLine() throws IOException {
		setLine(reader.readNext());
		return getLine() != null ? true : false;
	}
	
	public String[] getLine() {
		return this.line;
	}
	
	public void setLine(String[] line) {
		this.line = line;
	}
	
	public void close() throws IOException {
		reader.close();
	}
	
	public String get(String fieldName) {
		return getLine() == null ? null : line[name2index.get(fieldName.toLowerCase())];
	}
}
