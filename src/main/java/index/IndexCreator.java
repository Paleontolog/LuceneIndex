package index;

import documents.Book;
import documents.Book2;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spans.SpanBoostQuery;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.QueryBuilder;

import preparation.Parser;
import preparation.SynonymAnalizer;
import preparation.SynonymsPreparation;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class IndexCreator {

    private IndexWriter indexWriter;

    private Parser parser = new Parser();

    private SynonymMap synonymMap =  new SynonymsPreparation().getSynonyms("data/synonums_fast.json");

    private Document bookToDocument1(Book book) {
        Document document = new Document();
        document.add(new TextField("Author", book.getAuthor() != null ?
                book.getAuthor() : "null", Field.Store.YES));
        document.add(new TextField("Name", book.getName() != null ?
                book.getName() : "null", Field.Store.YES));
        document.add(new TextField("Genre", book.getGenre() != null ?
                book.getGenre() : "null", Field.Store.YES));
        document.add(new TextField("Description", book.getDescription() != null ?
                book.getDescription() : "null", Field.Store.YES));
        document.add(new TextField("Language", book.getLanguage() != null ?
                book.getLanguage() : "null", Field.Store.YES));
        document.add(new TextField("Publisher", book.getPublisher() != null ?
                book.getPublisher() : "null", Field.Store.YES));
        document.add(new TextField("ISBN", book.getISBN() != null ?
                book.getISBN() : "null", Field.Store.YES));
        document.add(new TextField("PublicationYear", book.getPublicationYear() != null ?
                book.getPublicationYear() : "null", Field.Store.YES));
        document.add(new StoredField("Price", book.getPrice() != null ?
                book.getPrice() : 0.0));
        document.add(new StoredField("Rating", book.getRating() != null ?
                book.getRating() : 0.0));
        return document;
    }

    private Document bookToDocument2(Book2 book) {
        Document document = new Document();
        document.add(new TextField("Author", book.getAuthor() != null ?
                book.getAuthor() : "null", Field.Store.YES));
        document.add(new TextField("Name", book.getName() != null ?
                book.getName() : "null", Field.Store.YES));
        document.add(new TextField("Series", book.getSeries() != null ?
                book.getSeries() : "null", Field.Store.YES));
        document.add(new TextField("Description", book.getDescription() != null ?
                book.getDescription() : "null", Field.Store.YES));
        document.add(new TextField("Language", book.getLanguage() != null ?
                book.getLanguage() : "null", Field.Store.YES));
        document.add(new TextField("Publisher", book.getPublisher() != null ?
                book.getPublisher() : "null", Field.Store.YES));
        document.add(new TextField("ISBN", book.getISBN() != null ?
                book.getISBN() : "null", Field.Store.YES));


        document.add(new LongPoint("PublicationYear", book.getPublicationYear() != null ?
                book.getPublicationYear() : 0));
        document.add(new StoredField("PublicationYear", book.getPublicationYear() != null ?
                book.getPublicationYear() : 0));

        document.add(new DoublePoint("Price", book.getPrice() != null ?
                book.getPrice() : 0.0));
        document.add(new StoredField("Price", book.getPrice() != null ?
                book.getPrice() : 0.0));

        document.add(new LongPoint("Pages", book.getPages() != null ?
                book.getPages() : 0));
        document.add(new StoredField("Pages", book.getPages() != null ?
                book.getPages() : 0));

        return document;
    }

    private void addDocuments(IndexWriter indexWriter, List<Book2> books) {
        List<Document> documents = books.stream().map(this::bookToDocument2)
                .collect(Collectors.toList());
        try {
            indexWriter.addDocuments(documents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createIndex(String filePath) {
        try {
            Directory directory = FSDirectory.open(Paths.get(filePath));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(directory, config);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void findWithSynonyms(String filePath, String field, String text) {
        try {
            Directory directory = FSDirectory.open(Paths.get(filePath));
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            Query query = new QueryParser(field, new SynonymAnalizer(synonymMap)).parse(text);

//            SpanQuery[] clauses = new SpanQuery[text.split(" ").length];
//            clauses[0] = new SpanMultiTermQueryWrapper(new FuzzyQuery(new Term("contents", "mosa")));
//            clauses[1] = new SpanMultiTermQueryWrapper(new FuzzyQuery(new Term("contents", "employee")));
//            clauses[2] = new SpanMultiTermQueryWrapper(new FuzzyQuery(new Term("contents", "appreicata")));
//            SpanNearQuery query = new SpanNearQuery(clauses, 0, true);


            System.out.println("Search... '" + query + "'");
            TopDocs topDocs = indexSearcher.search(query, 100);
            System.out.println(topDocs.totalHits);
            for (ScoreDoc sd : topDocs.scoreDocs) {
                Iterator<IndexableField> it = indexSearcher.doc(sd.doc).iterator();
                while (it.hasNext()) {
                    IndexableField fld = it.next();
                    System.out.println(fld.name() + " : " + fld.stringValue());
                }
            }
            indexReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void find(String filePath, String field, String text) {
        try {
            Directory directory = FSDirectory.open(Paths.get(filePath));
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            Analyzer analyzer = new StandardAnalyzer();

            Query q = new QueryParser( field, analyzer).parse(text.replace(" ", " AND "));

            TopDocs topDocs = indexSearcher.search(q, 10);
            for (ScoreDoc sd : topDocs.scoreDocs) {
                Iterator<IndexableField> it = indexSearcher.doc(sd.doc).iterator();
                while (it.hasNext()) {
                    IndexableField fld = it.next();
                    System.out.println(fld.name() + " : " + fld.stringValue());
                }
            }
            indexReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void search(String filePath, Long ...other) {
        Directory directory = null;
        try {
            directory = FSDirectory.open(Paths.get(filePath));
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            BooleanQuery.Builder query =  new BooleanQuery.Builder();

            boolean fl = other.length >= 2;

            Query query1 = DoublePoint.newRangeQuery("Price",
                    fl ? other[0] : Long.MIN_VALUE,
                    fl ? other[1] : Long.MAX_VALUE);

            fl = other.length >= 4;

            Query query2 = LongPoint.newRangeQuery("PublicationYear",
                    fl ? other[2] : Long.MIN_VALUE,
                    fl ? other[3] : Long.MAX_VALUE);

            fl = other.length == 6;

            Query query3 = LongPoint.newRangeQuery("Pages",
                    fl ? other[4] : Long.MIN_VALUE,
                    fl ? other[5] : Long.MAX_VALUE);

            query.add(query1, BooleanClause.Occur.MUST);
            query.add(query2, BooleanClause.Occur.MUST);
            query.add(query3, BooleanClause.Occur.SHOULD);

            TopDocs hits = indexSearcher.search(query.build(), 10);
            System.out.println("Number of hits: " + hits.totalHits);
            for (ScoreDoc sd : hits.scoreDocs) {
                Iterator<IndexableField> it = indexSearcher.doc(sd.doc).iterator();
                while (it.hasNext()) {
                    IndexableField fld = it.next();
                    System.out.println(fld.name() + " : " + fld.stringValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        IndexCreator indexCreator = new IndexCreator();
        boolean work = true;
        Scanner in = new Scanner(System.in);
        while (work) {
            System.out.print("Command: ");
            switch (in.nextLine()){//.strip()) {
                case "q":
                    work = false;
                    break;
                case "new": {
                    indexCreator.createIndex("src/main/resources/simpleindex2");
                    indexCreator.addDocuments(indexCreator.indexWriter,
                            indexCreator.parser.parse2("data/scraped_data_utf8_1.json"));
                    indexCreator.close();
                    break;
                }
                case "ft": {
                    System.out.print("Field: ");
                    String field = in.nextLine();//.strip();
                    System.out.print("\nText: ");
                    String text = in.nextLine();//.strip();
                    indexCreator.find("src/main/resources/simpleindex2", field, text);
                    break;
                }
                case "fn": {
                    System.out.print("Input [pr, yr, pg]: ");
                    Stream<Object> inp = Arrays.stream(in.nextLine().split(" "))
                            .map(Long::parseLong);

                    indexCreator.search("src/main/resources/simpleindex2",
                            inp.toArray(Long[]::new));
                    break;
                }
                case "i": {
                    System.out.print("Field: ");
                    String field = in.nextLine();//.strip();
                    System.out.print("\nText: ");
                    String text = in.nextLine();//.strip();
                    indexCreator.findWithSynonyms("src/main/resources/simpleindex2", field, text);
                    break;
                }
            }
        }
    }
}
