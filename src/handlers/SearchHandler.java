package handlers;

import java.io.FileInputStream;

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

public class SearchHandler extends FreebaseHandler {

	public boolean sendRequest(String query) {
		return sendRequest(query, null, null);
	}

	public boolean sendRequest(String subject, Integer limit) {
		return sendRequest(subject, limit, null);
	}

	public boolean sendRequest(String query, Integer limit, Multimap<String, String> params) {
		if (invalid(query))
			return false;
		try {
			properties.load(new FileInputStream("config.properties"));
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			JSONParser parser = new JSONParser();
			String prefix = "https://www.googleapis.com/freebase/v1/search";
			GenericUrl url;
			if (invalid(params)) {
				url = new GenericUrl(prefix);
			} else {
				MapJoiner joiner = Joiner.on('&').withKeyValueSeparator("=");
				String stringedParams = joiner.join(params.entries());
				url = new GenericUrl(prefix + "?" + stringedParams);
			}
			url.put("query", query);
			url.put("limit", invalid(limit) ? 10 : limit);
			url.put("indent", "true");
			url.put("key", properties.get("API_KEY"));
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
