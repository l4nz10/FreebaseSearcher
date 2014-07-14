package verifier;

import java.io.IOException;

import utils.Metaschemas;
import utils.Result;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import handlers.TopicHandler;

public class Main {

	public static void main(String[] args) throws IOException {
		
		Multimap<String, String> params = HashMultimap.create();
		params.put("uniqueness_failure", "soft");
		
		TopicHandler th = new TopicHandler();
		th.sendRequest("/m/04bz52");
		th.saveResponseToFile("wonderwall.json");

		FreebaseVerifier fv = new FreebaseVerifier();
		Result result = fv.subject("wonderwall")
						  .withMetaschema(Metaschemas.CREATED_BY, "Oasis")
						  .is("Composition");
		System.out.println(result);
	}

}
