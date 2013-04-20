import handbook.Chapter;
import handbook.HandbookParser;

import java.util.ArrayList;

import org.apache.lucene.document.Document;


import owl.OntologyClassificator;
import patientCase.PatientCase;
import patientCase.PatientCaseParser;


public class Program {
	
	public static void main(String[] args) {
		
//		HandbookParser hp = new HandbookParser();
//		hp.createJSONFile();
//		ArrayList<Chapter> handbookChapters = hp.getChapters();
		
		OntologyClassificator oc = new OntologyClassificator();
		ArrayList<String> textLines = new PatientCaseParser().getCase("Case 1").getTextLines();
		
		for (String line : textLines) {
			System.out.println(line);
			ArrayList<Document> hits = oc.searchLine(line);
			System.out.println("Found " + hits.size() + " hits.");
			for (int i = 0; i < hits.size(); ++i) {
				System.out.println((i + 1) + ". " + hits.get(i).get("code") + "\t"
						+ hits.get(i).get("text"));
			}		
		}
	}

}
