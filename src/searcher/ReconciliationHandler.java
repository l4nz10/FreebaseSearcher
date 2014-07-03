package searcher;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
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
import com.jayway.jsonpath.JsonPath;

public class ReconciliationHandler {
	
	private static final Properties properties = new Properties();	
	private JSONObject response;
	
	public String getMID() {
		Object candidate = response.get("match");
		if (candidate == null) {
			JSONArray candidates = (JSONArray) response.get("candidate");
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
	
	public boolean sendRequest(String kind, String name, Map<String, Collection<String>> params) {
		if (kind == null || kind.isEmpty() || name == null || name.isEmpty())
			return false;
		try {
			properties.load(new FileInputStream("config.properties"));
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			JSONParser parser = new JSONParser();
			StringBuilder textUrl = new StringBuilder("https://www.googleapis.com/freebase/v1/reconcile?");
			if (params != null)
				for (String key : params.keySet())
					for (String value : params.get(key)) {
						textUrl.append(key)
							   .append("=")
							   .append(value)
							   .append("&");
					}
			GenericUrl url = new GenericUrl(textUrl.substring(0,textUrl.length()-1));			
			url.put("key", properties.get("API_KEY"));
			url.put("kind", kind);
			url.put("name", name);
			if (params != null)
				for (String key : params.keySet())
					url.put(key, params.get(key));
			
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			setResponse((JSONObject) parser.parse(httpResponse.parseAsString()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
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
