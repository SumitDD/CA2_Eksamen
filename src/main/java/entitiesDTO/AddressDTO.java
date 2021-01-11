
package entitiesDTO;

import entities.Address;


public class AddressDTO {
    
    public String street;

    public AddressDTO(Address address) {
        this.street = address.getAddress();
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
    
    
    
}
