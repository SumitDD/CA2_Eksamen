
package facades;

import entitiesDTO.UserDTO;
import entitiesDTO.UsersDTO;
import java.util.List;

public interface UserInterFace {
    
    public abstract UserDTO getUserByPhone(String number);
    public abstract UsersDTO getAllUsersByHobby(String hobby);
    public abstract UsersDTO getAllUsersByCity(String city);
    public abstract long getUserCountByhobby(String hobby);
    public abstract List<String> getAllZipCodes();   
    public abstract UserDTO editperson(UserDTO user);
    public abstract UserDTO deletePerson(UserDTO user);
    //public abstract UserDTO addNewPerson (UserDTO user);





        
    
    
}
