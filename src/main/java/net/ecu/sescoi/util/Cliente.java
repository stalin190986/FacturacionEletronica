/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ecu.sescoi.util;

/**
 *
 * @author STALIN
 */
public class Cliente {
    
    private String nombre;
    private String nombreFirma;
    private String claveFirma;

    
    public Cliente(String nombre, String nombreFirma, String claveFirma) {
        this.nombre = nombre;
        this.nombreFirma = nombreFirma;
        this.claveFirma = claveFirma;
       
    }
 
    @Override
    public String toString() {
        return "Persona-> ID: "+nombreFirma+" Clave: "+claveFirma+"\n";
    }
    
    /**
     * @return the nombreFirma
     */
    public String getNombreFirma() {
        return nombreFirma;
    }

    /**
     * @param nombreFirma the nombreFirma to set
     */
    public void setNombreFirma(String nombreFirma) {
        this.nombreFirma = nombreFirma;
    }

    /**
     * @return the claveFirma
     */
    public String getClaveFirma() {
        return claveFirma;
    }

    /**
     * @param claveFirma the claveFirma to set
     */
    public void setClaveFirma(String claveFirma) {
        this.claveFirma = claveFirma;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
}
