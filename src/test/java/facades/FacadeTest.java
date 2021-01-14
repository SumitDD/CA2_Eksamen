package facades;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Phone;
import utils.EMF_Creator;
import entities.Role;
import entities.User;
import entitiesDTO.NewUserDTO;
import entitiesDTO.UserDTO;
import entitiesDTO.UsersDTO;
import exceptions.UserNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class FacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    private static User admin, user, both;
    private static Address a1, a2;
    private static CityInfo c1;
    private static Phone p1;
    private static Hobby h1;
    private static Role userRole;
    private static Role adminRole;
    

    public FacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
                
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        EntityManager em = emf.createEntityManager();
        facade = UserFacade.getUserFacade(emf);
        
        user = new User("user", "testuser");
        
        admin = new User("admin", "testadmin");
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
        //em.persist(newUser);
        em.getTransaction().commit();
        } finally {
            em.close();
        }
    
        
    }


    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {


    }
    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }
    
    @Test
    @Order(1)
    public void testGetUserByPhone() throws UserNotFoundException{
        UserDTO uDTO = facade.getUserByPhone("213213213");
        String userName = "user";

        assertEquals(uDTO.userName, userName);
    }
    
    @Test
    @Order(2)
    public void testGetAllZipCodes(){
        List<String> allZips = facade.getAllZipCodes();
        int expectedSize = 1;

        assertEquals(allZips.size(), expectedSize);
        assertThat(allZips, contains("2630"));
    }
    
    @Test
    @Order(3)
    public void testGetUsersByHobby() throws UserNotFoundException{
        UsersDTO allUsers = facade.getAllUsersByHobby("Fitness");
        UserDTO uDTO = new UserDTO(user);
        UserDTO aDTO = new UserDTO(admin);
        int expectedSize = 2;
        
        assertThat(allUsers.users, containsInAnyOrder(uDTO, aDTO));
        assertEquals(allUsers.users.size(), expectedSize);
    }
    
    @Test
    @Order(4)
    public void testGetUsersByCity() throws UserNotFoundException{
        UsersDTO allUsers = facade.getAllUsersByCity("Taastrup");
        UserDTO uDTO = new UserDTO(user);
        UserDTO aDTO = new UserDTO(admin);
        int expectedSize = 3;
        
        //assertThat(allUsers.users, containsInAnyOrder(uDTO, aDTO));
        assertEquals(allUsers.users.size(), expectedSize);
    }
    
    @Test
    @Order(5)
    public void testGetUserCountByHobby() throws UserNotFoundException{
        long count = facade.getUserCountByhobby("Fitness");
        int expectedCount = 2;
        assertEquals(count, expectedCount);
       
    }
    @Disabled
    @Test
    @Order(6)
    public void testEditPerson() throws UserNotFoundException{    
        Address a = admin.getAddress();
        a.setStreet("Gade123");
        admin.setAddress(a);
        UserDTO editedUser = new UserDTO(admin);
        UserDTO newEditedUser = facade.editperson(editedUser);
        assertEquals(newEditedUser.street, "Gade123");
 
    }
    @Disabled
    @Test
    @Order(7)
    public void testDeletePerson() throws UserNotFoundException{
        UserDTO userDTO = new UserDTO(user);
        UserDTO uDTO = facade.deleteUser(userDTO);     
        assertEquals(uDTO.userName, "user"); 
    }
    @Test
    public void testAddNewUser() throws UserNotFoundException{
        User newUser = new User("Sebastian", "Sebastian123");
        newUser.setAddress(a1);
        newUser.setHobbies(new Hobby("Svømning"));
        newUser.setPhone(new Phone("5521346"));
        newUser.addRole(userRole);
        UserDTO userDTO = facade.addNewUser(new NewUserDTO(newUser));
        String expected = "Sebastian";
        assertEquals(userDTO.userName, expected);
    }
    
    

}
