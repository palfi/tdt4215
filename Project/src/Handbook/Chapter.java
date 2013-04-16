package Handbook;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Chapter {
	
	private String path;
	private ArrayList<String> textLines;
	private ArrayList<Chapter> subchapters;
	private String name;
	

	public Chapter(String path, ArrayList<String> textLines, ArrayList<Chapter> subchapters) {
		this.path = path;
		this.textLines = textLines;
		this.subchapters = subchapters;
	}
	
	public Chapter(JSONObject jobject) {
		this.path = (String) jobject.get("path");
		this.name = (String) jobject.get("name");
		
		textLines = new ArrayList<String>();
		JSONArray jarray = (JSONArray) jobject.get("textLines");
		for (Object line : jarray) {
			textLines.add((String) line);
		}
		
		jarray = (JSONArray) jobject.get("subchapters");
		
		subchapters = new ArrayList<Chapter>();
		for (Object subchapter : jarray) {
			subchapters.add(new Chapter((JSONObject) subchapter));
		}
		
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ArrayList<String> getTextLines() {
		return textLines;
	}

	public void setTextLines(ArrayList<String> textLines) {
		this.textLines = textLines;
	}

	public ArrayList<Chapter> getSubchapters() {
		return subchapters;
	}

	public void setSubchapters(ArrayList<Chapter> subchapters) {
		this.subchapters = subchapters;
	}
	
	public JSONObject toJSON() {
		JSONObject jobject = new JSONObject();
		jobject.put("path", path);
		jobject.put("name", name);
		jobject.put("textLines", textLines);
		
		JSONArray jarray = new JSONArray();
		for (Chapter subchapter : subchapters) {
			jarray.add(subchapter.toJSON());
		}
		
		jobject.put("subchapters", jarray);
		
		return jobject;
	}

}
