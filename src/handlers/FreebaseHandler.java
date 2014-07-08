package handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public abstract class FreebaseHandler {
	
	protected static final Properties properties = new Properties();	
	protected JSONObject response;

	public JSONObject getResponse() {
		return response;
	}

	public void setResponse(JSONObject response) {
		this.response = response;
	}
	
	public void saveResponseToFile(String fileName) throws IOException {
		File file = new File(fileName);
		FileWriter writer = new FileWriter(file);
		try {
			JsonParser jp = new JsonParser();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement je = jp.parse(getResponse().toJSONString());
			writer.write(gson.toJson(je));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writer.flush();
			writer.close();
			System.out.println("Response saved in " + file.getAbsolutePath());
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected boolean invalid(Object o) {
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
}
