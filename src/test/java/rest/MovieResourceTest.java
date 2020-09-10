package rest;

import entities.Movie;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//@Disabled
public class MovieResourceTest{

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    private Movie m1;
    private Movie m2;
    private Movie m3;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }


    @BeforeAll
    public static void setUpClass() {
        //First Drop and Rebuild the test database 
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        //Set System property so the project executed by the Grizly-server wil use this same database
        EMF_Creator.startREST_TestWithDB();
        
        httpServer = startServer();

        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;

        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer()  {
       
       // System.in.read();
       
        httpServer.shutdownNow();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        m1 = new Movie(2001, "Harry Potter and the Philosopher's Stone", new String[]{"Daniel Radcliffe", "Emma Watson", "Alan Rickman", "Rupert Grint"});
        m2 = new Movie(2002, "Harry Potter and the Chamber of Secrets", new String[]{"Daniel Radcliffe", "Emma Watson", "Alan Rickman", "Rupert Grint"});
        m3 = new Movie(2019, "Once Upon a Time... in Hollywood", new String[]{"Leonardo DiCaprio", "Brad Pitt", "Margot Robbie"});
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE from Movie").executeUpdate();
            em.persist(m1);
            em.persist(m2);
            em.persist(m3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void serverIsRunning() {
        System.out.println("Testing is server UP");
        //Gherkin Syntax
        given().when().get("/movie").then().statusCode(200);
        //Hamcrest matcher
        given().when().get("/movie").then().assertThat().statusCode(200);
    }
    @Test
    public void contentType() {
        //Gherkin Syntax
        given().when().get("/movie").then().contentType(ContentType.JSON);
        //Hamcrest matcher
        given().when().get("/movie").then().assertThat().contentType(ContentType.JSON);
    }
    
    @Test
    public void demonStrateLogging() {
        
        given().log().all().when().get("/movie/all").then().log().body();
       
    }

    @Test
    public void testMovieCount() {
        given().
                get("/movie/count")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(3));
    }

    @Test
    public void testGetAll() {
         given()
                .when().
                get("/movie/all").
                then().assertThat().body("title", hasItems("Harry Potter and the Philosopher's Stone","Harry Potter and the Chamber of Secrets"
                        ,"Once Upon a Time... in Hollywood"));
        //TODO
    }
 @Test
    public void testGetAll1() {
        given().
        get("/movie/all").then().assertThat()
      .body("size()", is(3));
                
    }
    
    @Test
    public void testFindByTitle() {    //Finder på specifik titel
      given()
                .when().
                get("/movie/title/Harry Potter and the Philosopher's Stone").
                then().assertThat().body("title", hasItem("Harry Potter and the Philosopher's Stone"));
    
    }
    
        @Test
    public void testFindByTitleNotFound() {
        given()
                .when().
                get("/movie/title/Harry Potter and the Philosopher's Stone").
                then().assertThat().body("title", not(hasItem("Harry Potter and the Philosopher Stone")));
    }
    
       @Test
    public void testFindById() {
        given().get("/movie/"+ m2.getId().intValue()).then().assertThat().body("id", equalTo(m2.getId().intValue())).log().body();
        //TODO
          
    }
}


 