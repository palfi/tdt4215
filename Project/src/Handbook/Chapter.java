package handbook;

import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.queryparser.surround.query.SrndPrefixQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Chapter {
	
	private String path;
	private ArrayList<String> textLines;
	private ArrayList<Chapter> subchapters;
	private String name;
	private ArrayList<String> icdCodes;
	


	public Chapter(String path, ArrayList<String> textLines, ArrayList<Chapter> subchapters) {
		this.path = path;
		this.textLines = textLines;
		this.subchapters = subchapters;
		icdCodes = new ArrayList<String>();
	}
	
	public Chapter(JSONObject jobject) {
		this.path = (String) jobject.get("path");
		this.name = (String) jobject.get("name");
		
		textLines = new ArrayList<String>();
		JSONArray jarray = (JSONArray) jobject.get("textLines");
		for (Object line : jarray) {
			textLines.add((String) line);
		}
		icdCodes = new ArrayList<String>();
		jarray = (JSONArray) jobject.get("icdCodes");
		for (Object code : jarray) {
			icdCodes.add((String) code);
			
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
	
	public ArrayList<String> getIcdCodes() {
		return icdCodes;
	}	
	
	public ArrayList<String> getIcdCodesIncSub() {
		ArrayList<String> r = new ArrayList<String>();
		r.addAll(icdCodes);
		for (Chapter sub : subchapters) {
			r.addAll(sub.getIcdCodes());
		}
		return r;
	}
		
	public void setIcdCodes(ArrayList<String> icdCodes) {
		this.icdCodes = icdCodes;
	}
	
	/**
	 * Gets the main chapter as well as all subchapters, including the sub chapters of the subchapters.
	 * @return
	 */
	public ArrayList<Chapter> getAllChapters() {
		ArrayList<Chapter> r = new ArrayList<Chapter>();
		r.add(this);
		for (Chapter subchapter : subchapters) {
			r.addAll(subchapter.getAllChapters());
		}
		return r;
		
	}
	
	public JSONObject toJSON() {
		JSONObject jobject = new JSONObject();
		jobject.put("path", path);
		jobject.put("name", name);
		jobject.put("textLines", textLines);
		
		JSONArray jChapters = new JSONArray();
		for (Chapter subchapter : subchapters) {
			jChapters.add(subchapter.toJSON());
		}
		
		JSONArray jCodes = new JSONArray();
		for (String code : icdCodes) {
			jCodes.add(code);
		}
		
		jobject.put("icdCodes", jCodes);
		jobject.put("subchapters", jChapters);
		
		return jobject;
	}

}
