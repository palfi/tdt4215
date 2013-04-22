package owl;

import handbook.Chapter;

import java.io.IOException;
import java.util.ArrayList;

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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OntologyClassificator {
	private Directory index;

	private static void addDoc(IndexWriter w, String text, String code)
			throws IOException {
		Document doc = new Document();
		doc.add(new TextField("text", text, Field.Store.YES));
		doc.add(new StringField("code", code, Field.Store.YES));
		w.addDocument(doc);
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

	public OntologyClassificator(String path, String fileName) {
		try {
			index(path, fileName);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void index(String path, String fileName)
			throws OWLOntologyCreationException, IOException {
		OwlParser owlParser = new OwlParser();
		owlParser.parse(path, fileName);
		ArrayList<OWL_Class> owl_classes = owlParser.getOwl_Classes();
		index = new RAMDirectory();
		NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_42);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
				analyzer);
		IndexWriter w = new IndexWriter(index, config);
		for (OWL_Class c : owl_classes) {
			addDoc(w, c.getText(), c.getID());
		}
		w.close();
	}

	public ArrayList<Document> search(String querystr) {
		/*
		System.err.println(querystr);
		querystr = querystr.replaceAll("[^a-zA-ZÊ¯Â∆ÿ≈ ]", "");
		System.err.println(querystr);
		*/
		ArrayList<Document> returnDocs = new ArrayList<Document>();
		try {
			NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_42);
			Query q = new QueryParser(Version.LUCENE_42, "text", analyzer)
					.parse(querystr);
			int hitsPerPage = 10;
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
				if (i + 1 < hits.length && hits[i + 1].score < hits[i].score) {
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return returnDocs;
	}

	public static ArrayList<Document> search(String querystr, Directory index) {
		querystr = querystr.replace('*', ' ');
		querystr = querystr.replace('?', ' ');
		
		ArrayList<Document> returnDocs = new ArrayList<Document>();
		try {
			NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_42);
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
			System.err.println(e.getMessage());
		}
		return returnDocs;
	}

	public static Directory createIndex(ArrayList<Chapter> allChapters) throws IOException {
		Directory index = new RAMDirectory();
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_42);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42,
				analyzer);
		IndexWriter w = new IndexWriter(index, config);
		for (Chapter c : allChapters) {
			addDoc(w, c.getIcdCodesIncSub(), c.getPath(), c.getName());
		}
		w.close();
		return index;
	}
}
