/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesDTO;

import entities.Phone;

/**
 *
 * @author sumit
 */
public class PhoneDTO {
    
    public String number;

    public PhoneDTO(Phone phone) {
        this.number = phone.getNumber();
    }
    
    
    
}
