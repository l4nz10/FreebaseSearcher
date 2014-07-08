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

public class TopicHandler extends FreebaseHandler {

	public boolean sendRequest(String topicID) {
		return sendRequest(topicID, null);
	}
	
	public boolean sendRequest(String topicID, Multimap<String, String> params) {
		if (invalid(topicID))
			return false;
		try {
			properties.load(new FileInputStream("config.properties"));
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			JSONParser parser = new JSONParser();
			String prefix = "https://www.googleapis.com/freebase/v1/topic" + topicID;
			GenericUrl url;
			if (invalid(params)) { 
				url = new GenericUrl(prefix);
			} else {
				MapJoiner joiner = Joiner.on('&').withKeyValueSeparator("=");
				String stringedParams = joiner.join(params.entries());
				url = new GenericUrl(prefix + "?" + stringedParams);				
			}
			url.put("key", properties.get("API_KEY"));
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			setResponse((JSONObject) parser.parse(httpResponse.parseAsString()));
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}
