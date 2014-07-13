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
		th.sendRequest("/m/02nd_");
		th.saveResponseToFile("empire_state_building.json");

		FreebaseVerifier fv = new FreebaseVerifier();
		Result result = fv.subject("The lord of the rings").withMetaschema(Metaschemas.CONTRIBUTOR, "peter jackson").is("film");
		System.out.println(result);
	}

}
