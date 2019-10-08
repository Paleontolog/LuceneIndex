package documents;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private String name;
    private String author;
    private String genre;
    private String ISBN;
    private String description;
    @SerializedName("publication_year")
    private String  publicationYear;
    private String language;
    private String publisher;
    private Double price;
    private Double rating;
}
