package notesAPITests;

import io.restassured.response.Response;
import notesAPITests.config.NotesAPIConfig;
import notesAPITests.config.NotesAPIEndpoints;
import notesAPITests.objects.Note;
import notesAPITests.objects.User;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.annotations.Test;
import io.restassured.RestAssured;

public class NotesAPITest extends NotesAPIConfig {

    User user = new User();
    Note note = new Note();

    int random = (int )(Math.random() * 1000000 + 1);
    String name = "John" + random;
    String email = "john" + random + "@gmail.com";
    String currentPassword = "password" + random + "!";
    String phoneNumber = "96595558345";
    String companyName = "Salesforce!";
    String newPassword = "password" + random + random + "!";
    String noteTitle = "First Note";
    String noteDescription = "First Note Description";
    String noteCategory = "Home";

    @Test(priority = 0)
    public void checkNotesAPIHealth() {
        RestAssured
                .given()
                .when()
                    .get(NotesAPIEndpoints.HEALTH_CHECK)
                .then()
                    .assertThat().statusCode(200)
                .extract().response();;
    }

    @Test(priority = 1)
    public void registerNewUser() {

        user.setName(name);
        user.setEmail(email);
        user.setPassword(currentPassword);

        Response response =
            RestAssured
                .given()
                    .body(user)
                .when()
                    .post(NotesAPIEndpoints.REGISTER)
                .then()
                    .assertThat().statusCode(201)
                    .and().assertThat().body("data.id", Matchers.notNullValue())
                    .and().assertThat().body("data.name", Matchers.equalTo(user.getName()))
                    .and().assertThat().body("data.email", Matchers.equalTo(user.getEmail()))
                .extract().response();

        String userId = response.jsonPath().getString("data.id");
        user.setUserId(userId);

    }

    @Test(priority = 2)
    public void registerDuplicateUser() {

        Response response =
                RestAssured
                        .given()
                            .body(user)
                        .when()
                            .post(NotesAPIEndpoints.REGISTER)
                        .then()
                            .assertThat().statusCode(409)
                            .and().assertThat().body("success", Matchers.equalTo(false))
                            .and().assertThat().body("message", Matchers.equalTo("An account already exists with the same email address"))
                        .extract().response();

    }

    @Test(priority = 3)
    public void loginUser() {

        JSONObject json1 = new JSONObject();
        json1.put("email",user.getEmail());
        json1.put("password",user.getPassword());

        Response response =
        RestAssured
                .given()
                    .body(json1.toString())
                .when()
                    .post(NotesAPIEndpoints.LOGIN)
                .then()
                    .assertThat().statusCode(200)
                .extract().response();

        String token  = response.jsonPath().getString("data.token");
        user.setUserToken(token);

    }

    @Test(priority = 4)
    private void verifyUserProfile() {

        Response response =
        RestAssured
                .given()
                    .header("x-auth-token",user.getUserToken())
                .when()
                    .get(NotesAPIEndpoints.PROFILE)
                .then()
                    .assertThat().statusCode(200)
                    .and().assertThat().body("data.id", Matchers.notNullValue())
                    .and().assertThat().body("data.name", Matchers.equalTo(user.getName()))
                    .and().assertThat().body("data.email", Matchers.equalTo(user.getEmail()))
                .extract().response();

    }

    @Test(priority = 5)
    public void updateUserProfile() {

        user.setPhoneNumber(phoneNumber);
        user.setCompanyName(companyName);

        RestAssured
                .given()
                    .header("x-auth-token",user.getUserToken())
                    .body(user)
                .when()
                    .patch(NotesAPIEndpoints.PROFILE)
                .then()
                    .assertThat().statusCode(200)
                .extract().response();;
    }


    @Test(priority = 6)
    public void changeUserPassword() {

        String oldPassword = user.getPassword();
        user.setPassword(newPassword);
        String newPassword = user.getPassword();

        JSONObject json1 = new JSONObject();
        json1.put("name",user.getName());
        json1.put("currentPassword",oldPassword);
        json1.put("newPassword",newPassword);

        RestAssured
                .given()
                    .header("x-auth-token",user.getUserToken())
                    .body(json1.toString())
                .when()
                    .post(NotesAPIEndpoints.CHANGE_PASSWORD)
                .then()
                    .assertThat().statusCode(200)
                .extract().response();

    }

    @Test(priority = 7)
    public void createNote() {

        note.setTitle(noteTitle);
        note.setDescription(noteDescription);
        note.setCategory(noteCategory);

        Response inlineValidatedResponse =
        RestAssured
                .given()
                    .header("x-auth-token",user.getUserToken())
                    .body(note)
                .when()
                    .post(NotesAPIEndpoints.ALL_NOTES)
                .then()
                    .assertThat().statusCode(200)
                .extract().response();

        String noteId = inlineValidatedResponse.jsonPath().getString("data.id");
        note.setId(noteId);
    }

    @Test(priority = 8)
    public void verifyNoteAddedToList() {

        Response inlineValidatedResponse =
                RestAssured
                        .given()
                            .header("x-auth-token",user.getUserToken())
                        .when()
                            .get(NotesAPIEndpoints.ALL_NOTES)
                        .then()
                            .assertThat().statusCode(200)
                            .and().body("data[0].id", Matchers.equalTo(note.getId()))
                        .extract().response();

        inlineValidatedResponse.prettyPrint();
    }

    @Test(priority = 9)
    public void updateNote() {

        note.setCompleted(true);
        JSONObject json1 = new JSONObject();
        json1.put("id",note.getId());
        json1.put("completed",note.getCompleted());

        RestAssured
                .given()
                    .header("x-auth-token",user.getUserToken())
                    .pathParams("id",note.getId())
                    .body(json1.toString())
                .when()
                    .patch(NotesAPIEndpoints.NOTE)
                .then()
                    .assertThat().statusCode(200)
                    .and().assertThat().body("data.completed", Matchers.equalTo(note.getCompleted()))
                .extract().response();
    }

    @Test(priority = 10)
    public void updateNoteWrongCategory() {

        JSONObject json1 = new JSONObject();
        json1.put("id",note.getId());
        json1.put("title",note.getTitle());
        json1.put("description",note.getDescription());
        json1.put("completed",note.getCompleted());
        json1.put("category","Wrong");

        RestAssured
                .given()
                    .header("x-auth-token",user.getUserToken())
                    .pathParams("id",note.getId())
                    .body(json1.toString())
                .when()
                    .put(NotesAPIEndpoints.NOTE)
                .then()
                    .assertThat().statusCode(400)
                    .and().assertThat().body("success", Matchers.equalTo(false))
                    .and().assertThat().body("message", Matchers.equalTo("Category must be one of the categories: Home, Work, Personal"))
                .extract().response();

    }

    @Test(priority = 11)
    public void deleteNote() {

        RestAssured
                .given()
                    .header("x-auth-token",user.getUserToken())
                    .pathParams("id", note.getId())
                .when()
                    .delete(NotesAPIEndpoints.NOTE)
                .then()
                    .assertThat().statusCode(200)
                .extract().response();

    }

    @Test(priority = 12)
    public void verifyNoteIsDeleted() {
        RestAssured
                .given()
                    .header("x-auth-token",user.getUserToken())
                    .pathParams("id", note.getId())
                .when()
                    .get(NotesAPIEndpoints.NOTE)
                .then()
                    .assertThat().statusCode(404)
                    .and().assertThat().body("success", Matchers.equalTo(false))
                    .and().assertThat().body("message",Matchers.equalTo("No note was found with the provided ID, Maybe it was deleted"))
                .extract().response();
    }
}
