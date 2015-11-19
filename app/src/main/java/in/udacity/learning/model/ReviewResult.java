package in.udacity.learning.model;

/**
 * Created by Lokesh on 20-11-2015.
 */
public class ReviewResult {
    String id = "test";
    String author = "test";
    String content = "test";
    String url = "";

    public String getUri() {
        return url;
    }

    public void setUri(String uri) {
        this.url = uri;
    }

    public ReviewResult(String id, String author, String content) {
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
