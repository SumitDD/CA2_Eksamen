
package facades;

import entitiesDTO.NewUserDTO;
import entitiesDTO.UserDTO;
import entitiesDTO.UsersDTO;
import exceptions.UserNotFoundException;
import java.util.List;

public interface UserInterFace {
    
    public abstract UserDTO getUserByPhone(String number) throws UserNotFoundException;
    public abstract UsersDTO getAllUsersByHobby(String hobby) throws UserNotFoundException;
    public abstract UsersDTO getAllUsersByCity(String city) throws UserNotFoundException;
    public abstract long getUserCountByhobby(String hobby) throws UserNotFoundException;
    public abstract List<String> getAllZipCodes() throws UserNotFoundException;   
    public abstract UserDTO editperson(UserDTO user) throws UserNotFoundException;
    public abstract UserDTO deleteUser(UserDTO user) throws UserNotFoundException;
    public abstract UsersDTO getAllUsers() throws UserNotFoundException;
    public abstract UserDTO addNewUser(NewUserDTO userDTO) throws UserNotFoundException;
    





        
    
    
}
