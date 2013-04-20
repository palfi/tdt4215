package lucene;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import owlParser.OWL_Class;
import owlParser.OwlParser;

public class HelloLucene {
	private String path = "/Users/javier/Downloads/web_intelligence_project/";
	private String fileName = "icd10no.owl";
	// private String fileName = "atc.owl";
	private StandardAnalyzer analyzer;
	private Directory index;

	private static void addDoc(IndexWriter w, String text, String code)
			throws IOException {
		Document doc = new Document();
		doc.add(new TextField("text", text, Field.Store.YES));
		doc.add(new StringField("code", code, Field.Store.YES));
		w.addDocument(doc);
	}

	public void index() throws OWLOntologyCreationException, IOException {
		OwlParser owlParser = new OwlParser();
		owlParser.parse(path, fileName);
		ArrayList<OWL_Class> owl_classes = owlParser.getOwl_Classes();
		// 1. index
		index = new RAMDirectory();
		analyzer = new StandardAnalyzer(Version.LUCENE_42);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
				analyzer);
		IndexWriter w = new IndexWriter(index, config);
		for (OWL_Class c : owl_classes) {
			addDoc(w, c.getText(), c.getID());
		}
		w.close();
	}

	public void search(String querystr) throws ParseException, IOException {
		// the "text" argument specifies the default field to use
		// when no field is explicitly specified in the query.
		Query q = new QueryParser(Version.LUCENE_42, "text", analyzer)
				.parse(querystr);
		// 3. search
		int hitsPerPage = 10;
		IndexReader reader = DirectoryReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		// 4. display results
		System.out.println("Found " + hits.length + " hits.");
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			System.out.println((i + 1) + ". " + d.get("code") + "\t"
					+ d.get("text"));
		}
		reader.close();
	}

	public void start() throws OWLOntologyCreationException, IOException,
			ParseException {
		index();
		search("Pasienten har smerter i korsryggen, trolig Hekseskudd");
	}

	public static void main(String[] args) throws IOException, ParseException,
			OWLOntologyCreationException {
		HelloLucene helloLucene = new HelloLucene();
		helloLucene.start();
	}
}