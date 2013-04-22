import handbook.Chapter;
import handbook.HandbookParser;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import owl.OWL_Class;
import owl.OntologyClassificator;
import patientCase.PatientCase;
import patientCase.PatientCaseParser;

public class Program {
	private static String path = "owlFiles/";
	private static String fileName = "icd10no.owl";
	// private String fileName = "atc.owl";
	private static int numPatientCases = 8;

	private static void start() {
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

	private static void preprocess() {
		new HandbookParser().createJSONFile();
	}

	private static void task1b() {
		OntologyClassificator oc = new OntologyClassificator("owlFiles/",
				"icd10no.owl");
		ArrayList<Document> hits;
		HandbookParser hp = new HandbookParser();

		// gets main chapters
		ArrayList<Chapter> allMainChapters = hp.getMainChapters();
		// gets all chapters incl. subchapters
		ArrayList<Chapter> allChapters = new ArrayList<Chapter>();
		for (Chapter chapter : allMainChapters) {
			allChapters.addAll(chapter.getAllChapters());
		}

		// find and add icd codes to each chapter
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

	private static void task1a() {
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

	private static void task1c() {
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

	public static void main(String[] args) throws IOException {
		task1b();
		HandbookParser hb = new HandbookParser();
		ArrayList<Chapter> allChapters = new ArrayList<Chapter>();
		for (Chapter chapter : hb.getMainChapters()) {
			allChapters.addAll(chapter.getAllChapters());
		}

		Directory index = index(allChapters);

		PatientCaseParser pcp = new PatientCaseParser();
		ArrayList<PatientCase> cases = new ArrayList<PatientCase>();
		String[] caseNames = { "Case 1", "Case 2", "Case 3", "Case 4",
				"Case 5", "Case 6", "Case 7", "Case 8" };
		for (String caseName : caseNames) {
			cases.add(pcp.getCase(caseName));
		}
		OntologyClassificator oc = new OntologyClassificator();
		for (PatientCase pc : cases) {
			String patientCaseIcdCodes = "";
			for (String line : pc.getTextLines()) {
				for (Document hit : oc.searchLine(line)) {
					patientCaseIcdCodes += hit.get("code") + " ";
				}
			}
			System.out.println(pc.getcaseName() + " - " + patientCaseIcdCodes);
			for (Document hit : search(patientCaseIcdCodes, index)) {
				System.out.println(hit.get("name"));
			}
			System.out.println("--------------------------------");
		}

	}

	public static ArrayList<Document> search(String querystr, Directory index) {
		ArrayList<Document> returnDocs = new ArrayList<Document>();
		try {
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
			Query q = new QueryParser(Version.LUCENE_42, "icdCodes", analyzer)
					.parse(querystr);
			int hitsPerPage = 10;
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(
					hitsPerPage, true);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			for (int i = 0; i < hits.length - 1; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				returnDocs.add(d);
			}

			reader.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return returnDocs;
	}

	private static Directory index(ArrayList<Chapter> allChapters)
			throws IOException {
		Directory index = new RAMDirectory();
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
				analyzer);
		IndexWriter w = new IndexWriter(index, config);
		for (Chapter c : allChapters) {
			addDoc(w, c.getIcdCodes(), c.getPath(), c.getName());
		}
		w.close();
		return index;
	}

	private static void addDoc(IndexWriter w, ArrayList<String> icdCodes,
			String path, String name) throws IOException {
		Document doc = new Document();
		String icdCodeString = "";
		for (String code : icdCodes) {
			icdCodeString += code + " ";
		}
		doc.add(new TextField("icdCodes", icdCodeString, Field.Store.NO));
		doc.add(new StringField("path", path, Field.Store.YES));
		doc.add(new StringField("name", name, Field.Store.YES));
		w.addDocument(doc);
	}

}
