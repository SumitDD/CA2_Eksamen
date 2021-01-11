/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

/**
 *
 * @author sumit
 */
public class StatusDTO {
    
    public boolean verified;
    public int sentCount;

    public StatusDTO(boolean verified, int sentCount) {
        this.verified = verified;
        this.sentCount = sentCount;
    }

    public StatusDTO() {
    }
    
    
    
}
