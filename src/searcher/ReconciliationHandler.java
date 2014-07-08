package searcher;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.JsonPath;

public class ReconciliationHandler {
	
	private static final Properties properties = new Properties();	
	private JSONObject response;
	
	public String getMID() {
		Object candidate = response.get("match");
		if (candidate == null) {
			JSONArray candidates = (JSONArray) response.get("candidate");
			if (candidates == null)
				return null;
			candidate = candidates.get(0);
		}
		return JsonPath.read(candidate,"$.mid");
	}
	
	public String[] getAllMIDs() {
		JSONArray candidates = (JSONArray) response.get("candidate");
		StringBuilder builder = new StringBuilder();
		for (Object candidate : candidates) {
			builder.append(JsonPath.read(candidate,"$.mid"));
			builder.append("#");
		}
		if (builder.length() != 0)
			builder.replace(builder.length() - 1, builder.length(), "");
		return builder.toString().split("#");
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
			String prefix = "https://www.googleapis.com/freebase/v1/reconcile";
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
			this.saveResponseToFile("response.json");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void saveResponseToFile(String fileName) throws IOException {
		FileWriter writer = new FileWriter(fileName);
		try {
			JsonParser jp = new JsonParser();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement je = jp.parse(response.toJSONString());
			writer.write(gson.toJson(je));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
		}
	}
	
	@SuppressWarnings("rawtypes")
	private boolean invalid(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof String) {
			String s = (String) o;
			return s.isEmpty();
		}
		if (o instanceof Collection) {
			Collection c = (Collection) o;
			return c.isEmpty();
		}
		return false;
	}

	public JSONObject getResponse() {
		return response;
	}

	public void setResponse(JSONObject response) {
		this.response = response;
	}
	
}
