package searcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TopicHandler {
	
	private File file;
	private JSONObject topic;
	private static final Properties properties = new Properties();
	
	public boolean sendRequest(String topicID) {
		return sendRequest(topicID, null);
	}
	
	public boolean sendRequest(String topicID, Map<String, String> params) {
		if (invalid(topicID))
			return false;
		try {
			properties.load(new FileInputStream("config.properties"));
			HttpTransport httpTransport = new NetHttpTransport();
			HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
			JSONParser parser = new JSONParser();
			GenericUrl url = new GenericUrl("https://www.googleapis.com/freebase/v1/topic" + topicID);
			url.put("key", properties.get("API_KEY"));
			if (!invalid(params))
				for (String key : params.keySet())
					url.put(key, params.get(key));
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse httpResponse = request.execute();
			setTopic((JSONObject) parser.parse(httpResponse.parseAsString()));
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public void saveTopicToFile(String fileName) throws IOException {
		FileWriter writer = new FileWriter(fileName);
		try {
			JsonParser jp = new JsonParser();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement je = jp.parse(topic.toJSONString());
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
	
	public void setTopic(JSONObject topic) {
		this.topic = topic;
	}
	
	public JSONObject getTopic() {
		return topic;
	}
	
	public void createFile(String fileName) {
		setFile(new File(fileName));
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
}
