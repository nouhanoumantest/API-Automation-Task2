package notesAPITests.objects;

public class Note {

    private String title;
    private String description;
    private String category;
    private String id;
    private boolean completed;

    /**
     * No args constructor for use in serialization
     *
     */
    public Note() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCompleted(boolean bool) {
        this.completed = bool;
    }

    public boolean getCompleted() {
        return completed;
    }


}
