package owl;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.no.NorwegianAnalyzer;
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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OntologyClassificator {
	private String path = "owlFiles/";
	private String fileName = "icd10no.owl";
	// private String fileName = "atc.owl";
	private NorwegianAnalyzer analyzer;
	private Directory index;

	private static void addDoc(IndexWriter w, String text, String code)
			throws IOException {
		Document doc = new Document();
		doc.add(new TextField("text", text, Field.Store.YES));
		doc.add(new StringField("code", code, Field.Store.YES));
		w.addDocument(doc);
	}
	
	public OntologyClassificator(String path, String fileName) {
		this.path = path;
		this.fileName = fileName;
		try {
			index();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public OntologyClassificator() {
		try {
			index();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void index() throws OWLOntologyCreationException, IOException {
		OwlParser owlParser = new OwlParser();
		owlParser.parse(path, fileName);
		ArrayList<OWL_Class> owl_classes = owlParser.getOwl_Classes();
		// 1. index
		index = new RAMDirectory();
		analyzer = new NorwegianAnalyzer(Version.LUCENE_42);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
				analyzer);
		IndexWriter w = new IndexWriter(index, config);
		for (OWL_Class c : owl_classes) {
			addDoc(w, c.getText(), c.getID());
		}
		w.close();
	}

	public ArrayList<Document> searchLine(String querystr) {
		ArrayList<Document> returnDocs = new ArrayList<Document>();
		try {
			// the "text" argument specifies the default field to use
			// when no field is explicitly specified in the query.
			Query q = new QueryParser(Version.LUCENE_42, "text", analyzer)
					.parse(querystr);
			// 3. search
			int hitsPerPage = 2;
			IndexReader reader = DirectoryReader.open(index);
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(
					hitsPerPage, true);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			for (int i = 0; i < hits.length; ++i) {
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
}