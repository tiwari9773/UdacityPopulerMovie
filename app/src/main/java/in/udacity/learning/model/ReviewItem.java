package in.udacity.learning.model;

/**
 * Created by Lokesh on 01-11-2015.
 */
public class ReviewItem implements MarkerItem{

    String id = "test";
    String author = "test";
    String content = "test";
    String uri = "";

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public ReviewItem(String id, String author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
