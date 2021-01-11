package facades;

import entities.Hobby;
import entities.Phone;
import entities.User;
import entitiesDTO.HobbyDTO;
import entitiesDTO.UserDTO;
import entitiesDTO.UsersDTO;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade implements UserInterFace {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    @Override
    public UserDTO getUserByPhone(String number) {
                  EntityManager em = emf.createEntityManager();

        TypedQuery query = em.createQuery("Select u FROM User u JOIN u.phones p WHERE p.Number =:number", User.class);
        query.setParameter("number", number);
        
        User user = (User) query.getSingleResult();
        return new UserDTO(user);
        
    }

    @Override
    public UsersDTO getAllUsersByHobby(String description) {
                  EntityManager em = emf.createEntityManager();

        TypedQuery query = em.createQuery("Select u FROM User u JOIN u.hobbies h WHERE h.description =:description", UsersDTO.class);
        query.setParameter("description", description);
        List<User> users = query.getResultList();
        return new UsersDTO(users);
        
    }

    @Override
    public UsersDTO getAllUsersByCity(String city) {
                  EntityManager em = emf.createEntityManager();

        TypedQuery query = em.createQuery("Select u FROM User u JOIN u.address a WHERE a.cityInfo.city =:city", UsersDTO.class);
        query.setParameter("city", city);
        List<User> users = query.getResultList();
        return new UsersDTO(users);
    }

    @Override
    public long getUserCountByhobby(String description) {
                  EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("Select COUNT(u) FROM User u JOIN u.hobbies h WHERE h.description =:description");
        query.setParameter("description", description);
        long count = (long) query.getSingleResult();
        return count;
    }

    @Override
    public List<String> getAllZipCodes() {
          EntityManager em = emf.createEntityManager();
          Query query = em.createQuery("Select c.zip FROM CityInfo c");
          List<String> zips = query.getResultList();
          return zips;
     
    }

    @Override
    public UserDTO editperson(UserDTO userDTO) {
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, userDTO.userName);
        String editedHobby = "";
        
        for(Hobby hobby: user.getHobbies()){
            for(HobbyDTO hobbyDTO: userDTO.hobbies){
                if(!hobby.getDescription().equals(hobbyDTO.description))
                    editedHobby = hobbyDTO.description;
                    user.getHobbies().remove(hobby);
                    user.getHobbies().add(new Hobby(editedHobby));
            }
        }
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
        return userDTO;
    }

    @Override
    public UserDTO deletePerson(UserDTO userDTO) {
 
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, userDTO.userName);
        System.out.println(user.getUserName());
        em.getTransaction().begin();
        for(Phone p: user.getPhones()){
            em.remove(p);
        }
        for(Hobby h: user.getHobbies()){
            if(h.getUsers().size()<= 1){
                em.remove(h);
            }else{
                h.getUsers().remove(h);
            }
        }     
        em.remove(user);
        if(user.getAddress().getUsers().size()<=1){
            em.remove(user.getAddress());
        }else{
            user.getAddress().getUsers().remove(user);
        }
        em.remove(user.getAddress().getCityInfo());
        em.getTransaction().commit();
        return new UserDTO(user);
    }
    
        public static void main(String[] args) {
        utils.SetupTestUsers.setUpUsers();
        EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
        UserFacade f = UserFacade.getUserFacade(EMF);
        EntityManager em = emf.createEntityManager();
        User user1 = em.find(User.class, "user");
            
        UserDTO user = new UserDTO(user1);
        System.out.println(user.userName);



    }
    
  

}
