package searcher;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class CsvExtractor {
	
	private CSVReader reader;
	private Map<String, Integer> name2index;
	private String[] line = null;
	
	public CsvExtractor(String fileName) {
		try {
			reader = new CSVReader(new FileReader(fileName), ';');
			initializeFields(reader.readNext());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initializeFields(String[] fieldNames) {
		name2index = new HashMap<String, Integer>();
		for (int i = 0; i < fieldNames.length; i++) {
			name2index.put(fieldNames[i].toLowerCase(), i);
		}
	}
	
	public boolean nextLine() {
		try {
			line = reader.readNext();
			return true;
		} catch (IOException e) { 
			e.printStackTrace();
			return false;
		}
	}
	
	public String get(String fieldName) {
		if (line == null)
			return null;
		return line[name2index.get(fieldName.toLowerCase())];
	}
}
