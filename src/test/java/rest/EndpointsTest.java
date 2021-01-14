package rest;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Phone;
import entities.RenameMe;
import entities.Role;
import entities.User;
import entitiesDTO.UserDTO;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test
@Disabled

public class EndpointsTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static User admin, user, both, sumit;
    private static Address a1, a2;
    private static CityInfo c1;
    private static Phone p1;
    private static Hobby h1;
    private static Role userRole;
    private static Role adminRole;
    

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        EntityManager em = emf.createEntityManager();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
        
        user = new User("user", "testuser");
        admin = new User("admin", "testadmin");
        sumit = new User("dey", "sumit");
        both = new User("user_admin", "testuseradmin");
        a1 = new Address("Street");
        a2 = new Address("Taastrupvej123");
        c1 = new CityInfo("2630", "Taastrup");
        p1 = new Phone("213213213");
        h1 = new Hobby("Fitness");

        if (admin.getUserPass().equals("test") || user.getUserPass().equals("test") || both.getUserPass().equals("test")) {
            throw new UnsupportedOperationException("You have not changed the passwords");
        }

        user.setHobbies(h1);
        user.setPhone(p1);
        a1.setCityInfo(c1);
        a2.setCityInfo(c1);
        user.setAddress(a1);
        
        admin.setHobbies(h1);
        admin.setPhone(new Phone("12321321321"));
        admin.setAddress(a2);
        
        sumit.setPhone(new Phone("53426321"));
        sumit.setAddress(a1);
        sumit.setHobbies(h1);
      
        userRole = new Role("user");
        adminRole = new Role("admin");
        user.addRole(userRole);
        admin.addRole(adminRole);
        both.addRole(userRole);
        both.addRole(adminRole);
        
        try {
            em.getTransaction().begin();
//        em.createNativeQuery("DELETE FROM PHONE").executeUpdate();
//        em.createNativeQuery("DELETE FROM HOBBY_users").executeUpdate();
//        em.createNativeQuery("DELETE FROM user_roles").executeUpdate();
//        em.createNativeQuery("DELETE FROM USERS").executeUpdate();
//        em.createNativeQuery("DELETE FROM ADDRESS").executeUpdate();
//        em.createNativeQuery("DELETE FROM CITYINFO").executeUpdate();
//        em.createNativeQuery("DELETE FROM HOBBY").executeUpdate();
//        em.createNativeQuery("DELETE FROM ROLES").executeUpdate();
        
        em.persist(userRole);
        em.persist(adminRole);
        em.persist(user);
        em.persist(admin);
        em.persist(both);
        em.persist(sumit);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

//     Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
//    TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {

    }

    @Test
    public void testServerIsUp() {
        given().when().get("/info").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
                .contentType("application/json")
                .get("/info/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Hello anonymous"));
    }

    @Disabled
    @Test
    @Order(1)
    public void testParrallel() throws Exception {
        given()
                .contentType("application/json")
                .get("/info/parrallel").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("peopleName", equalTo("Luke Skywalker"))
                .body("planetName", equalTo("Yavin IV"))
                .body("speciesName", equalTo("Ewok"))
                .body("starshipName", equalTo("Star Destroyer"))
                .body("vehicleName", equalTo("Sand Crawler"));

 
    }
    @Disabled
    @Test
    @Order(2)
    public void testCached() throws Exception {
        given()
                .contentType("application/json")
                .get("/info/cached").then()            
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("peopleName", equalTo("Luke Skywalker"))
                .body("planetName", equalTo("Yavin IV"))
                .body("speciesName", equalTo("Ewok"))
                .body("starshipName", equalTo("Star Destroyer"))
                .body("vehicleName", equalTo("Sand Crawler"));
    }
    
    @Test
    public void getPersonByphone() {
        given()
                .contentType("application/json")
                .when()
                .get("/info/personphone/213213213").then()
                .statusCode(200)
                .body("userName", equalTo(user.getUserName()))
                .body("street", equalTo(user.getAddress().getStreet()))
                .body("zip", equalTo(user.getAddress().getCityInfo().getZip()))
                .body("city", equalTo(user.getAddress().getCityInfo().getCity()))
                .body("hobbies.description", hasItems("Fitness"))
                .body("phones.number", hasItems("213213213"));
    }

    @Test
    public void getAllUsersByCity(){
        List<UserDTO> userDTOs;
        userDTOs = given()
        .contentType("application/json")
        .when()
        .get("info/personcity/Taastrup")
        .then()
        .extract().body().jsonPath().getList("users", UserDTO.class);
        
        UserDTO userDTO = new UserDTO(user);
        UserDTO adminDTO = new UserDTO(admin);
        
        assertThat(userDTOs, containsInAnyOrder(userDTO, adminDTO));
              
    }
    @Test
    public void testDeletePerson() {
     
            given()
                .contentType("application/json")
                .body(new UserDTO(admin))
                .when()
                .delete("/info/delete/")
                .then();
        
    }
    
    @Test
    public void testAddNewUser() {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactoryForTest();
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(sumit);
        em.getTransaction().commit();
     
            given()
                .contentType("application/json")
                .body(new UserDTO(sumit))
                .when()
                .post("/info/add/")
                .then();
        
    }
    

}
