package verificator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVWriter;
import searcher.CsvExtractor;

public class Verificator {

	public static void main(String[] args) throws IOException {
		String name1, name2, date1, date2, place1, place2;
		String name_by_data;
		
		long correct=0, analized=0, uncorrect=0, notFounded = 0, semicorrect = 0;

		CsvExtractor dataExtractor = new CsvExtractor("data.csv");
		CsvExtractor freeBaseExtractor = new CsvExtractor("basketball_complete.csv");

		freeBaseExtractor.nextLine();
		while (dataExtractor.nextLine()) {
			analized ++;
			name1 = freeBaseExtractor.get("name").replaceAll("ì", "i");
			name2 = freeBaseExtractor.get("(o)name");
			date1 = freeBaseExtractor.get("date_of_birth");
			date2 = freeBaseExtractor.get("(o)date_of_birth");
			place1 = freeBaseExtractor.get("place_of_birth").toLowerCase().replaceAll(" ", "");
			place2 = freeBaseExtractor.get("(o)place_of_birth");
			name_by_data = dataExtractor.get("name");
			System.out.println("{....");
			System.out.println("DATA NAME: "+name_by_data);
			System.out.println("NAME: "+name1+" - "+name2);
			System.out.println("DATE: "+date1+" - "+date2);
			System.out.println("PLACE: "+place1+" - "+place2);
			System.out.println("...}");
			if(name_by_data.equals(name2)){
				if(name1.equals(name2) && date1.equals(date2)){
					if(place1.equals(place2)){
						correct++;
						writeOnCsv(name_by_data,"CORRECT","","");
						freeBaseExtractor.nextLine();
					}else{
						semicorrect++;
						writeOnCsv(name_by_data,"SEMICORRECT",place2,place1);
						freeBaseExtractor.nextLine();
					}
				}else{
					uncorrect++;
					if(name1.equals(name2))
						writeOnCsv(name_by_data,"UNCORRECT",date2,date1);
					else
						writeOnCsv(name_by_data,"UNCORRECT",name2,name1);
					freeBaseExtractor.nextLine();
				}
			}else{
				notFounded++;
				writeOnCsv(name_by_data,"NOT FOUND","","");
			}
		}
		writeStats(analized,notFounded,correct,uncorrect,semicorrect);
		System.out.println("ANALIZED: "+analized);
		System.out.println("NOT FOUND: "+notFounded);
		System.out.println("CORRECT: "+correct);
		System.out.println("UNCORRECT: "+uncorrect);
		System.out.println("SEMICORRECT: "+semicorrect);

	}
	
	private static void writeOnCsv(String origName, String outcome, String origField, String freeBaseField) throws IOException {
		boolean alreadyExists = new File("stats.csv").exists();
		CSVWriter writer = new CSVWriter(new FileWriter("stats.csv", true), ';');
		String[] entries;
		
		if (!alreadyExists) {
			entries = "NAME#OUTCOME#ORIGIN_FIELD#FREEBASE_FIELD".split("#");
			writer.writeNext(entries);
		}
		
		entries = new String[4];
		entries[0] = origName;
		entries[1] = outcome;
		entries[2] = origField;
		entries[3] = freeBaseField;
	
		
		writer.writeNext(entries);
		writer.close();
	}
	
	private static void writeStats(long analized, long notFounded, long correct, long uncorrect, long semicorrect) throws IOException{
		FileWriter writer = new FileWriter("stats.txt");
		try {
			writer.write("ANALAZIED: "+analized+"\n");
			writer.write("NOT FOUND: "+notFounded+"\n");
			writer.write("CORRECT: "+correct+"\n");
			writer.write("UNCORRECT: "+uncorrect+"\n");
			writer.write("SEMICORRECT: "+semicorrect);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
	}

}
