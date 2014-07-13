package verifier;

import handlers.SearchHandler;
import handlers.TopicHandler;
import interfaces.FreebaseVerifierInterface;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import utils.Result;
import utils.ResultBuilder;
import utils.StringHelper;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.JsonPath;

public class FreebaseVerifier implements FreebaseVerifierInterface {
	
	private final int LIMIT = 5;
	
	private String subject;
	private Multimap<String, String> properties;
	private Multimap<String, String> metaschemas;
	
	public FreebaseVerifier(String subject) {
		setSubject(subject);
		initializeProperties(); 
		initializeMetaschemas();
	}

	public FreebaseVerifier() {
		this(null);
	}
	
	private void initializeProperties() {
		properties = HashMultimap.create();		
	}
	
	private void initializeMetaschemas() {
		metaschemas = HashMultimap.create();		
	}
	
	public FreebaseVerifier subject(String subject) {
		setSubject(subject);
		return this;
	}
	
	public FreebaseVerifier withProperty(String property, String value) {
		properties.put(property, value);
		return this;
	}
	
	public FreebaseVerifier withMetaschema(String metaschema, String value) {
		String val = value.contains(" ") ? "\"" + value + "\"" : value;
		metaschemas.put(metaschema, val);
		return this;
	}
	
	public Result is(String statement) {
		if (invalid(statement) || invalid(getSubject()))
			return ResultBuilder.buildErrorResult();
		SearchHandler sh = new SearchHandler();
		String query = getSubject();
		int limit = LIMIT;
		Multimap<String, String> params = buildParams();
		if (sh.sendRequest(query, limit, params)) {
			JSONObject response = sh.getResponse();
			JSONArray results = (JSONArray) response.get("result");
			TopicHandler th = new TopicHandler();
			for (Object result : results) {
				String name = JsonPath.read(result, "$.name");
				System.out.println("Result name: " + name);
				if (name.toLowerCase().equals(getSubject().toLowerCase())) {
					String mid = JsonPath.read(result, "$.mid");
					System.out.println("Machine ID: " + mid);
					if (th.sendRequest(mid)) {
						JSONObject topic = th.getResponse();
						double propertiesScore = 1.0;
						if (!invalid(getProperties())) {
							JSONObject properties = (JSONObject) topic.get("property");
							propertiesScore = checkProperties(properties);
						}
						List<String> typesText = JsonPath.read(topic, "$.property./type/object/type.values[*].text");
						for (String text : typesText) {
							if (text.toLowerCase().equals(statement.toLowerCase())) {
								if (propertiesScore > 0.0) {
									double score = propertiesScore * (double) JsonPath.read(result, "$.score");
									return ResultBuilder.buildOKResult(score, propertiesScore);
								} else {
									return ResultBuilder.buildBadResult();
								}
							}
						}
					}
				}
				
			}
		}
		return ResultBuilder.buildBadResult();
	}
	
	private Multimap<String, String> buildParams() {
		Multimap<String, String> params = HashMultimap.create();
		if (!invalid(getMetaschemas())) {
			MapJoiner joiner = Joiner.on(' ').withKeyValueSeparator(":");
			String stringedMetas = joiner.join(getMetaschemas().entries());
			params.put("filter", "(all " + stringedMetas + ")");
		}
		return params;
	}
	
	private double checkProperties(JSONObject properties) {
		@SuppressWarnings("unchecked")
		Set<Entry<String, Object>> freebaseEntries = properties.entrySet();
		int correctProperties = 0;
		for (Entry<String, String> propEntry : getProperties().entries()) {
			for (Entry<String, Object> freebaseEntry : freebaseEntries) {
				String candidateProperty = StringHelper.space2underscore(propEntry.getKey());
				if (freebaseEntry.getKey().endsWith(candidateProperty)) {
					List<String> texts = JsonPath.read(freebaseEntry.getValue(), "$..text");
					for (String text : texts) {
						String text1 = StringHelper.removeSpaces(text).toLowerCase();
						String text2 = StringHelper.removeSpaces(propEntry.getValue()).toLowerCase();
						if (text1.equals(text2)) {
							correctProperties++;
						}
					}
				}
			}
		}
		if (correctProperties <= (getProperties().size() / 2))
			return 0.0;
		return correctProperties / (double) getProperties().size();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Multimap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Multimap<String, String> params) {
		this.properties = params;
	}
	
	public Multimap<String, String> getMetaschemas() {
		return metaschemas;
	}

	public void setMetaschemas(Multimap<String, String> metaschemas) {
		this.metaschemas = metaschemas;
	}
	
	private boolean invalid(String s) {
		return s == null || s.isEmpty();
	}
	
	private boolean invalid(Multimap<String, String> params) {
		return params == null || params.isEmpty();
	}
}
