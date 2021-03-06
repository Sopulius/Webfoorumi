/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Webfoorumi.Dom;

import java.util.List;

/**
 *
 * @author Aleksi
 */
public class Alue {
    private int id;
    private String nimi;
    
    private List<Keskustelu> keskustelut;
    private Viesti uusinviesti;
    

    
    public Alue(int id, String nimi){
        this.id = id;
        this.nimi = nimi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public List<Keskustelu> getKeskustelut() {
        return keskustelut;
    }

    public void setKeskustelut(List<Keskustelu> keskustelut) {
        this.keskustelut = keskustelut;
    }
    
    public int viestienlkm(){
        int lkm = 0;
        for(Keskustelu k : keskustelut){
            lkm += k.viestitlkm();
        }
        return lkm;
    }

    public Viesti getUusinviesti() {
        
        return uusinviesti;
    }

    public void setUusinviesti(Viesti uusinviesti) {
        this.uusinviesti = uusinviesti;
    }
    
    
    
    
}
