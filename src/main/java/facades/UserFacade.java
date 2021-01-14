package facades;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Phone;
import entities.Role;
import entities.User;
import entitiesDTO.HobbyDTO;
import entitiesDTO.NewUserDTO;
import entitiesDTO.PhoneDTO;
import entitiesDTO.UserDTO;
import entitiesDTO.UsersDTO;
import exceptions.UserNotFoundException;
import java.util.ArrayList;
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
    public UserDTO getUserByPhone(String number) throws UserNotFoundException {
                  EntityManager em = emf.createEntityManager();

        TypedQuery query = em.createQuery("Select u FROM User u JOIN u.phones p WHERE p.Number =:number", User.class);
        query.setParameter("number", number);
        
        User user = (User) query.getSingleResult();
        
        if(user == null){
            throw new UserNotFoundException("User cannot be found");
        }
        return new UserDTO(user);
        
    }

    @Override
    public UsersDTO getAllUsersByHobby(String description) throws UserNotFoundException {
                  EntityManager em = emf.createEntityManager();

        TypedQuery query = em.createQuery("Select u FROM User u JOIN u.hobbies h WHERE h.description =:description", UsersDTO.class);
        query.setParameter("description", description);
        List<User> users = query.getResultList();
        if(users.size() == 0){
            throw new UserNotFoundException("Users cannot be found");
        }
        return new UsersDTO(users);
        
    }

    @Override
    public UsersDTO getAllUsersByCity(String city) throws UserNotFoundException {
                  EntityManager em = emf.createEntityManager();

        TypedQuery query = em.createQuery("Select u FROM User u JOIN u.address a WHERE a.cityInfo.city =:city", UsersDTO.class);
        query.setParameter("city", city);
        List<User> users = query.getResultList();
        if(users.size() == 0){
           throw new UserNotFoundException("Users cannot be found");
        }
        return new UsersDTO(users);
    }

    @Override
    public long getUserCountByhobby(String description) throws UserNotFoundException {
                  EntityManager em = emf.createEntityManager();

        Query query = em.createQuery("Select COUNT(u) FROM User u JOIN u.hobbies h WHERE h.description =:description");
        query.setParameter("description", description);
        long count = (long) query.getSingleResult();
        if(count == 0){
           throw new UserNotFoundException("Users cannot be found by the given hobby");
        }
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
    public UserDTO editperson(UserDTO userDTO) throws UserNotFoundException {
        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, userDTO.userName);
        if(user == null){
           throw new UserNotFoundException("User cannot be found");
        }
        String editedHobby = "";
        
        for(Hobby hobby: user.getHobbies()){
            for(HobbyDTO hobbyDTO: userDTO.hobbies){
                if(!hobby.getDescription().equals(hobbyDTO.description))
                    editedHobby = hobbyDTO.description;
                    user.getHobbies().remove(hobby);
                    user.getHobbies().add(new Hobby(editedHobby));
            }
        }
        user.setAddress(new Address(userDTO.street));
        user.setUserName(userDTO.userName);
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
        return userDTO;
    }

   @Override
    public UserDTO deleteUser(UserDTO userDTO) throws UserNotFoundException {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.find(User.class, userDTO.userName);
           if(user == null){
           throw new UserNotFoundException("User cannot be found by the given hobby");
        }
            em.getTransaction().begin();

            for (Phone p : user.getPhones()) {
                em.remove(p);
            }

            for (Hobby h : user.getHobbies()) {
                if (h.getUsers().size() <= 1) {
                    em.remove(h);
                } else {
                    h.getUsers().remove(h);
                }
            }

            em.remove(user);

            if (user.getAddress().getUsers().size() <= 1) {
                em.remove(user.getAddress());
                
            } else {
                user.getAddress().getUsers().remove(user);
            }
            if(user.getAddress().getCityInfo().getAddresses().size() <=1){
                em.remove(user.getAddress().getCityInfo());
            } else{
               user.getAddress().getCityInfo().getAddresses().remove(user.getAddress());
            }

            em.getTransaction().commit();

            return new UserDTO(user);
        } finally {
            em.close();
        }
    }

    @Override
    public UsersDTO getAllUsers(){
        EntityManager em = emf.createEntityManager();
        Query query = em.createQuery("Select u from User u");
        List<User> allUsers = query.getResultList();
        return new UsersDTO(allUsers);
    }


    @Override
    public UserDTO addNewUser(NewUserDTO newUserDTO) throws UserNotFoundException {
        
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        
        CityInfo cityInfo = em.find(CityInfo.class, newUserDTO.zip);
        if(cityInfo == null){
            cityInfo = new CityInfo(newUserDTO.zip, newUserDTO.city);
        }
        
        User user = new User(newUserDTO.userName, newUserDTO.userPass);
        
        if(user == null){
            throw new UserNotFoundException("The user cannot be added");
        }
        Address a = new Address(newUserDTO.street);
        Role role = em.find(Role.class, "user");
      
         for (PhoneDTO phone : newUserDTO.phones) {
          user.setPhone(new Phone(phone.number));       
        }
          for (HobbyDTO h : newUserDTO.hobbies) {
          user.setHobbies(new Hobby(h.description));
        }
        a.setCityInfo(cityInfo);
        user.setAddress(a);
        user.addRole(role); 
        
        em.persist(user);
        em.getTransaction().commit();
        return new UserDTO(user);
    }



}
