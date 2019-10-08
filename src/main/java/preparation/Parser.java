package preparation;

import com.google.gson.Gson;
import documents.Book;
import documents.Book2;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Gson gson = new Gson();

    public List<Book> parse1(String filePath) {
        URL url = this.getClass().getClassLoader().getResource(filePath);
        assert url != null;
        List<Book> books = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(url.getFile())))) {
            String str;
            books = new ArrayList<>();
            while ((str = bufferedReader.readLine()) != null) {
                books.add(gson.fromJson(str, Book.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book2> parse2(String filePath) {
        URL url = this.getClass().getClassLoader().getResource(filePath);
        assert url != null;
        List<Book2> books = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(url.getFile())))) {
            String str;
            books = new ArrayList<>();
            while ((str = bufferedReader.readLine()) != null) {
                books.add(gson.fromJson(str, Book2.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return books;
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.parse1("data/scraped_data_utf8_2.json");
    }
}
