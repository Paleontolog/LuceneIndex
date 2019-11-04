package metrics;

import com.google.gson.Gson;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Metrics {
    private String filePath = "src/main/resources/simpleindex2";
    private String fileTrain = "src/main/resources/data/train.json";
    private Directory directory;
    private IndexReader indexReader ;
    private IndexSearcher indexSearcher;
    private final Gson gson = new Gson();

    public Metrics() throws IOException {
         directory = FSDirectory.open(Paths.get(filePath));
         indexReader = DirectoryReader.open(directory);
         indexSearcher = new IndexSearcher(indexReader);
    }

    private void print(TopDocs topDocs) throws IOException {
        System.out.println("Number of hits: " + topDocs.totalHits);
        for (ScoreDoc sd : topDocs.scoreDocs) {
            Iterator<IndexableField> it = indexSearcher.doc(sd.doc).iterator();
            while (it.hasNext()) {
                IndexableField fld = it.next();
                System.out.println(fld.name() + " : " + fld.stringValue());
            }
            System.out.println("----------------------------------------------------------");
        }
    }

    private Map<String, List<Map<String, String>>> write(TopDocs topDocs, String query)
            throws IOException {
        Map<String, List<Map<String, String>>> result = new TreeMap<>();
        List<Map<String, String>> docs = new ArrayList<>();
        for (ScoreDoc sd : topDocs.scoreDocs) {
            Map<String, String> doc = new TreeMap<>();
//            Iterator<IndexableField> it = indexSearcher.doc(sd.doc).iterator();
//            while (it.hasNext()) {
//                IndexableField fld = it.next();
//                doc.put(fld.name(), fld.stringValue());
//            }
            Document document =  indexSearcher.doc(sd.doc);
            doc.put("Author", document.get("Author"));
            doc.put("Name", document.get("Name"));
            doc.put("Description", document.get("Description"));



            docs.add(doc);
        }
        result.put(query, docs);
        return result;
    }

    private void writeToFile(List<Map<String, List<Map<String, String>>>> result, String file) {
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(file), true))) {
            bufferedWriter.write(gson.toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  Map<String, List<Map<String, String>>> firstSelect(String text) throws IOException {
        try {
            BooleanQuery.Builder query = new BooleanQuery.Builder();
            Analyzer analyzer = new RussianAnalyzer();
            Query query1 = new QueryParser("Description", analyzer).parse(text);
            Query query2 = new QueryParser("Author", analyzer).parse(text);
            Query query3 = new QueryParser("Name", analyzer).parse(text);

            query.add(query1, BooleanClause.Occur.MUST);
            query.add(query2, BooleanClause.Occur.SHOULD);
            query.add(query3, BooleanClause.Occur.SHOULD);

            System.out.println(query1);
            TopDocs hits = indexSearcher.search(query.build(), 40);
//            new ClassicSimilarity().tf();
            print(hits);
            return write(hits, text);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  List<Double> getTfIdf(String query, TopDocs topDocs) throws IOException {
        List<Double> tfIdfList = new ArrayList<>();
        for (ScoreDoc sd : topDocs.scoreDocs) {
            Iterator<IndexableField> it = indexSearcher.doc(sd.doc).iterator();
            while (it.hasNext()) {
                IndexableField fld = it.next();
                //doc.put(fld.name(), fld.stringValue());
            }
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        Metrics metrics = new Metrics();
//        metrics.firstSelect("капитан фракас");
        List<String> queries = new ArrayList<>();
//        queries.add("психолог заложница");
        queries.add("лондон волк приключения север");
//         queries.add("индейцы америка майн");
//         queries.add("золя любовь роман бедность");
//         queries.add("убийство десять остров");
//         queries.add("великая отечественная начало трилогия");
//         queries.add("новелла капуста юмор");
//         queries.add("проклятые волчица роман");
//         queries.add("разбойники замок англия рыцарь");
//         queries.add("звёзды миры король");
//         queries.add("моряк узник предательство");
//         queries.add("капитан фракас");

        List<Map<String, List<Map<String, String>>>> result = new ArrayList<>();
        for (String str : queries) {
            result.add(metrics.firstSelect(str));
        }
        metrics.writeToFile(result, metrics.fileTrain);
    }
}
