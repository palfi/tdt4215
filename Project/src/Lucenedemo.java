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
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;

public class Lucenedemo {
  public static void main(String[] args) throws IOException, ParseException {
    // 0. Specify the analyzer for tokenizing text.
    //    The same analyzer should be used for indexing and searching
    NorwegianAnalyzer analyzer = new NorwegianAnalyzer(Version.LUCENE_42);

    // 1. create the index
    Directory index = new RAMDirectory();

    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_42, analyzer);
    IndexWriter w = new IndexWriter(index, config);
    addDoc(w, CaseReader.readCase("Case 1"), "Case 1");
    addDoc(w, CaseReader.readCase("Case 2"), "Case 2");
    addDoc(w, CaseReader.readCase("Case 3"), "Case 3");
    addDoc(w, CaseReader.readCase("Case 4"), "Case 4");
    addDoc(w, CaseReader.readCase("Case 5"), "Case 5");
    addDoc(w, CaseReader.readCase("Case 6"), "Case 6");
    addDoc(w, CaseReader.readCase("Case 7"), "Case 7");
    addDoc(w, CaseReader.readCase("Case 8"), "Case 8");
    w.close();

    // 2. query
    args = new String[1];
    args[0] = "diabetes insulin";
    String querystr = args.length > 0 ? args[0] : "lucene";

    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    Query q = new QueryParser(Version.LUCENE_42, "caseText", analyzer).parse(querystr);

    // 3. search
    int hitsPerPage = 10;
    IndexReader reader = DirectoryReader.open(index);
    System.out.println(reader.getTermVector(0, "caseText"));
    IndexSearcher searcher = new IndexSearcher(reader);
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
    
    // 4. display results
    System.out.println("Found " + hits.length + " hits.");
    for(int i=0;i<hits.length;++i) {
      int docId = hits[i].doc;
      float score = hits[i].score;
      Document d = searcher.doc(docId);
      System.out.println((i + 1) + ". " + d.get("caseName") + "\t" + score);
    }

    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close();
  }

  private static void addDoc(IndexWriter w, String caseText, String caseName) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("caseText", caseText, Field.Store.NO));

    // use a string field for isbn because we don't want it tokenized
    doc.add(new StringField("caseName", caseName, Field.Store.YES));
    w.addDocument(doc);
  }
}