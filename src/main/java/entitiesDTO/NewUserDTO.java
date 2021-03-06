
package entitiesDTO;

import entities.Hobby;
import entities.Phone;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class NewUserDTO {
    
    public String userName;
    public String userPass;
    public String street;
    public String zip;
    public String city;
    public List<HobbyDTO> hobbies = new ArrayList();
    public List<PhoneDTO> phones = new ArrayList();

    public NewUserDTO(User user) {
        this.userName = user.getUserName();
        this.userPass = user.getUserPass();
        this.street = user.getAddress().getStreet();
        this.zip = user.getAddress().getCityInfo().getZip();
        this.city = user.getAddress().getCityInfo().getCity();

        for(Hobby hobby: user.getHobbies()){
            this.hobbies.add(new HobbyDTO(hobby));
           
        }
        for(Phone phone: user.getPhones()){
            this.phones.add(new PhoneDTO(phone));
        }

    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserDTO other = (UserDTO) obj;
        if (!Objects.equals(this.userName, other.userName)) {
            return false;
        }
        return true;
    }

}
