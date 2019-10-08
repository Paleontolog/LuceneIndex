package documents;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book2 {
    private String name;
    private String author;
    private String series;
    private String ISBN;
    private String description;
    @SerializedName("publication_year")
    private Long publicationYear;
    private String language;
    private String publisher;
    private Double price;
    private Integer pages;
}
