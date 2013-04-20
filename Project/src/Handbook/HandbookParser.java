package handbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HandbookParser {
	
	private String chapterPath;
	private String jsonFilePath = "Handbook/allchapters.json";
	

	public HandbookParser() { 
		if (!(new File(jsonFilePath).exists())) {
			createJSONFile();
		}
	}
	
	public ArrayList<Chapter> getChapters() {
		JSONParser parser = new JSONParser();
		Object obj;
		JSONObject jsonObject;
		ArrayList<Chapter> allChapters = new ArrayList<Chapter>();
		try {
			obj = parser.parse(new FileReader(jsonFilePath));
			jsonObject = (JSONObject) obj;
			
			for (int i = 0; i < jsonObject.size(); i++) {
				JSONObject chapter = (JSONObject) jsonObject.get(i + "");
				System.out.println("Loading from JSON file... " + (i+1) + "/" + jsonObject.size());
				allChapters.add(new Chapter(chapter));
			}
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return allChapters;
		 
	}
	
	public void createJSONFile() {
		ArrayList<Chapter> allChapters = new ArrayList<Chapter>();
		Document doc;
		
		File folder = new File("Handbook/html/L/");
		for (File file : folder.listFiles()) {
			doc = Jsoup.parse(readFile(file));
			chapterPath = file.getPath();
			System.out.println("Parsing " + file.getName());
			allChapters.add(getChapter(doc));
		}
		folder = new File("Handbook/html/T/");
		for (File file : folder.listFiles()) {
			doc = Jsoup.parse(readFile(file));
			chapterPath = file.getPath();
			System.out.println("Parsing " + file.getName());
			allChapters.add(getChapter(doc));
		}
		JSONObject obj = new JSONObject();
		int count = 0;
		System.out.println("Creating JSON objects");
		for (Chapter chapter : allChapters) {
			obj.put(count, chapter.toJSON());
			count++;
		}
		System.out.println("Saving JSON objects");
		try {
	 
			FileWriter file = new FileWriter(jsonFilePath);
			file.write(obj.toJSONString());
			file.flush();
			file.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Chapter getChapter(Document doc) {
		if (!doc.getElementsByClass("seksjon2").isEmpty()) {
			return createChapter(doc.getElementsByClass("seksjon2").get(0));
		} 
		return null;
	}
	
	
	private Chapter createChapter(Element element) {
		ArrayList<Chapter> subchapters = new ArrayList<Chapter>();
		String text = "";
		
		
		if (!element.className().equals("seksjon4")) {
			Elements subchapterElements = element.getElementsByClass(nextclassName(element.className()));
			for (Element subchapterElement : subchapterElements) {
				subchapters.add(createChapter(subchapterElement));
			}
		}
		
		Elements allElements = element.getAllElements();
		Elements headersToBeIgnored = element.select("h5");
		
		for (Element e: allElements) {
			if (e.parent().id().equals(element.id())) {
				if (e.className().equals("def")) {
					text += getText(e, headersToBeIgnored) + "\n";
				} else if (e.className().equals("sub8")) {
					for (Element sub8e : e.children()) {
						if (sub8e.className().equals("def")) {
							text += getText(e, headersToBeIgnored) + "\n";
						}
					}
				}
			}
		}
		ArrayList<String> textLines = getTextLines(text);
		Chapter c = new Chapter(chapterPath, textLines, subchapters);
		String chapterName = element.child(0).ownText().replace("*", "");
		c.setName(chapterName);
		return c;
	}
	
	/**
	 * We don't want headers in our text, this method removes it. Removes some symbols
	 * @param e
	 * @param headersToBeIgnored
	 * @return String
	 */
	private String getText(Element e, Elements headersToBeIgnored) {
		String text = "";
		for (Element child : e.children()) {
			if (!headersToBeIgnored.contains(child)) {
				text += child.text();
			}
		}
		text = text.replace("*", "");
		text = text.replace(":", "");
		text = text.replace("-", " ");
		text = text.replace("/", " ");
		text = text.replaceAll("[0-9]", "");
		return text;
	}
	
	private ArrayList<String> getTextLines(String fullText) {
		String[] lines = fullText.split("\\. ");
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(lines[0]);
		for (int i = 1; i < lines.length; i++) {
			if (lines[i].length() > 0) {
				int firstLetter = lines[i].charAt(0);
				//check if the next letter start with uppercase
				if ((firstLetter <= 90 && firstLetter >= 65) || firstLetter == 197 || firstLetter == 198 || firstLetter == 216) {
					temp.add(lines[i]);
				} else {
					temp.set(temp.size() - 1, temp.get(temp.size() - 1) + ". " + lines[i]);
				}
			}
		}
		return temp;
	}
	
	private String nextclassName(String className) {
		int newSection = Integer.parseInt(className.substring(className.length() - 1, className.length())) + 1;
		String newClassName = className.substring(0, className.length() - 1) + newSection;
		return newClassName;
	}
	
	private String readFile(File file) {
		String html = "";
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String nextLine = br.readLine();
			do {
				html += nextLine + "\n";
				nextLine = br.readLine();
			} while (nextLine != null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		html = fixNorwegianLetters(html);
		return html;
	}
	
	private String fixNorwegianLetters(String text) {
		text = text.replace("&aring;", "å");
		text = text.replace("&oslash;", "ø");
		text = text.replace("&aelig;", "æ");
		
		return text;
	}

}
