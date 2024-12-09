package notesAPITests.config;

public interface NotesAPIEndpoints {

    String HEALTH_CHECK =  "/health-check";
    String ALL_USERS = "/users";
    String REGISTER = ALL_USERS + "/register";
    String LOGIN = ALL_USERS + "/login";
    String PROFILE = ALL_USERS + "/profile";
    String CHANGE_PASSWORD = ALL_USERS + "/change-password";
    String ALL_NOTES = "/notes";
    String NOTE = ALL_NOTES + "/{id}";
}
