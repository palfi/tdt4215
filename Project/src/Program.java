import java.util.ArrayList;

import org.apache.lucene.document.Document;

import Handbook.Chapter;
import Handbook.HandbookParser;

import owl.OntologyClassificator;


public class Program {
	
	public static void main(String[] args) {
		
		HandbookParser hp = new HandbookParser();
		ArrayList<Chapter> handbookChapters = hp.getChapters();
		
		OntologyClassificator oc = new OntologyClassificator();
		String line = handbookChapters.get(0).getTextLines().get(0);
		System.out.println(line);
		ArrayList<Document> hits = oc.searchLine(line);
		System.out.println("Found " + hits.size() + " hits.");
		for (int i = 0; i < hits.size(); ++i) {
			System.out.println((i + 1) + ". " + hits.get(i).get("code") + "\t"
					+ hits.get(i).get("text"));
		}
	}

}
