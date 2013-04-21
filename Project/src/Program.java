import handbook.Chapter;
import handbook.HandbookParser;

import java.util.ArrayList;

import org.apache.lucene.document.Document;
import owl.OntologyClassificator;
import patientCase.PatientCaseParser;

public class Program {
	private String path = "owlFiles/";
	private String fileName = "icd10no.owl";
	// private String fileName = "atc.owl";
	private int numPatientCases = 8;

	private void start() {
		// HandbookParser hp = new HandbookParser();
		// hp.createJSONFile();
		// ArrayList<Chapter> handbookChapters = hp.getChapters();

		OntologyClassificator oc = new OntologyClassificator(path, fileName);
		ArrayList<String> textLines = new PatientCaseParser().getCase("Case 1")
				.getTextLines();

		for (String line : textLines) {
			System.out.println(line);
			ArrayList<Document> hits = oc.searchLine(line);
			System.out.println("Found " + hits.size() + " hits.");
			for (int i = 0; i < hits.size(); ++i) {
				System.out.println(hits.get(i).get("code"));
				// System.out.println((i + 1) + ". " +
				// hits.get(i).get("code")+"\t" + hits.get(i).get("text"));
			}
		}

	}
	
	private void preprocess() {
		new HandbookParser().createJSONFile();
	}
	
	private void task1b() {
		OntologyClassificator oc = new OntologyClassificator("owlFiles/", "icd10no.owl");
		ArrayList<Document> hits;
		HandbookParser hp = new HandbookParser();
		
		//gets main chapters
		ArrayList<Chapter> allMainChapters = hp.getMainChapters();
		//gets all chapters incl. subchapters
		ArrayList<Chapter> allChapters = new ArrayList<Chapter>();
		for (Chapter chapter : allMainChapters) {
			allChapters.addAll(chapter.getAllChapters());
		}
		
		//find and add icd codes to each chapter
		ArrayList<String> icdCodes;
		for (Chapter chapter : allChapters) {
			icdCodes = new ArrayList<String>();
			for (String line : chapter.getTextLines()) {
				hits = oc.searchLine(line);
				for (Document hit : hits) {
					icdCodes.add(hit.get("code"));
				}
			}
			chapter.setIcdCodes(icdCodes);
		}
		hp.createJSONFile(allMainChapters);
	}
	

	private void task1a() {
		System.out.print("Clinical note	Sentence	ICD-10\n");
		OntologyClassificator oc = new OntologyClassificator("owlFiles/",
				"icd10no.owl");
		for (int numCase = 1; numCase <= numPatientCases; numCase++) {
			ArrayList<String> textLines = new PatientCaseParser().getCase(
					"Case " + numCase).getTextLines();
			System.out.print(numCase + "\t\t");
			for (int numLine = 0; numLine < textLines.size(); numLine++) {
				System.out.print((numLine + 1) + "\t\t");
				String line = textLines.get(numLine);
				ArrayList<Document> hits = oc.searchLine(line);
				for (int i = 0; i < hits.size(); ++i) {
					System.out.print(hits.get(i).get("code") + ", ");
				}
				System.out.println();
				System.out.print("\t\t");
			}
			System.out.println();
		}
	}
	
	private void task1c() {
		System.out.print("Clinical note	Sentence	ATC\n");
		OntologyClassificator oc = new OntologyClassificator("owlFiles/",
				"atc.owl");
		for (int numCase = 1; numCase <= numPatientCases; numCase++) {
			ArrayList<String> textLines = new PatientCaseParser().getCase(
					"Case " + numCase).getTextLines();
			System.out.print(numCase + "\t\t");
			for (int numLine = 0; numLine < textLines.size(); numLine++) {
				System.out.print((numLine + 1) + "\t\t");
				String line = textLines.get(numLine);
				ArrayList<Document> hits = oc.searchLine(line);
				for (int i = 0; i < hits.size(); ++i) {
					System.out.print(hits.get(i).get("code") + ", ");
				}
				System.out.println();
				System.out.print("\t\t");
			}
			System.out.println();
		}

	}

	public static void main(String[] args) {
		Program p = new Program();
		p.task1b();
//		p.task1a();
		// p.start();
	}

}
