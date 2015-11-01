package in.udacity.learning.model;

/**
 * Created by Lokesh on 01-11-2015.
 */
public class TrailerItem implements MarkerItem{

    String id = "test";
    String iso_639_1 = "test";
    String key = "test";
    String name = "test";
    String site = "test";
    String size = "test";
    String type = "test";

    public String getTrailerPath() {
        return trailerPath;
    }

    public void setTrailerPath(String trailerPath) {
        this.trailerPath = trailerPath;
    }

    String trailerPath = "0";

    public TrailerItem(String id, String name, String key, String type) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso_639_1() {
        return iso_639_1;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
