/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entitiesDTO;

import entities.Hobby;


public class HobbyDTO {
    
    public String description;

    public HobbyDTO(Hobby hobby) {
        this.description = hobby.getDescription();
    }
    
    
    
}
