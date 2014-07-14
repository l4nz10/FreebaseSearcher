package searcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import utils.CsvExtractor;
import utils.Metaschemas;
import utils.Result;
import verifier.FreebaseVerifier;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;

import au.com.bytecode.opencsv.CSVWriter;

public class AllmusicExample {
	
	public static final int LIMIT = 400;
	
	private static String subject;
	private static String[] artists;
	private static String type = "Musical Album";
	
	public static void main(String[] args) throws IOException {
		CsvExtractor extractor = new CsvExtractor("allmusic.csv");
		File file = new File("album_metaschema.csv");
		CSVWriter writer = new CSVWriter(new FileWriter(file), ';');
		String[] entries = "SUBJECT#METASCHEMAS#IS#RESULT".split("#");
		writer.writeNext(entries);
		
		int i = 0;
		while (extractor.nextLine() && i < LIMIT) {
			subject = extractor.get("title");
			artists = splitArtists(extractor.get("artist"));
			type = "Musical Album";
			
			FreebaseVerifier fv = new FreebaseVerifier(subject);
			fv = fv.withMetaschema(Metaschemas.CREATED_BY, checkAnd(artists[0]));
			Result r = fv.is(type);
			
			entries = new String[4];
			entries[0] = subject;
			MapJoiner joiner = Joiner.on(',').withKeyValueSeparator(":");
			entries[1] = joiner.join(fv.getMetaschemas().entries());
			entries[2] = type;
			entries[3] = r.getDescription();
			writer.writeNext(entries);

			i++;
		}
		writer.close();
		extractor.close();
		System.out.println("Results written on: " + file.getAbsolutePath());
	}
	
	private static String[] splitArtists(String artists) {
		return artists.split("/");
	}
	
	private static String checkAnd(String artist) {
		return artist.replaceAll("&", "and");
	}
}
