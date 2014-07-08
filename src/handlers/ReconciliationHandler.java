package handlers;

import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.JsonPath;

public class ReconciliationHandler extends FreebaseHandler {
	
	public String getMID() {
		Object candidate = getResponse().get("match");
		if (candidate == null) {
			JSONArray candidates = (JSONArray) getResponse().get("candidate");
			if (candidates == null)
				return null;
			candidate = candidates.get(0);
		}
		return JsonPath.read(candidate,"$.mid");
	}
	
	public String[] getAllMIDs() {
		JSONArray candidates = (JSONArray) getResponse().get("candidate");
		List<String> mids = new LinkedList<String>();
		for (Object candidate : candidates) {
			mids.add((String) JsonPath.read(candidate,"$.mid"));
		}
		return mids.toArray(new String[mids.size()]);
	}
	
	public boolean sendRequest(String kind, String name) {
		return sendRequest(kind, name, null);
	}	
	
	public boolean sendRequest(String kind, String name, Multimap<String, String> params) {
		if (invalid(kind) || invalid(name))
			return false;
		try {
			properties.load(new FileInputStream("config.properties"));
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			JSONParser parser = new JSONParser();
			String prefix = "https://www.googleapis.com/freebase/v1sandbox/reconcile";
			GenericUrl url;
			if (invalid(params)) { 
				url = new GenericUrl(prefix);
			} else {
				MapJoiner joiner = Joiner.on('&').withKeyValueSeparator("=");
				String stringedParams = joiner.join(params.entries());
				url = new GenericUrl(prefix + "?" + stringedParams);				
			}
			url.put("key", properties.get("API_KEY"));
			url.put("kind", kind);
			url.put("name", name);
			System.out.println(url);
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			setResponse((JSONObject) parser.parse(httpResponse.parseAsString()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
