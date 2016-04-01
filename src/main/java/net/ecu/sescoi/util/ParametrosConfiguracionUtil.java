/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ecu.sescoi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author mauriciochilan
 */
public class ParametrosConfiguracionUtil {
    
    public Properties prop;
    public InputStream input;

    public ParametrosConfiguracionUtil() {
        prop = new Properties();
        input = null;
        
        try {

		String filename = "propiedades/parametros.properties";
		input = getClass().getClassLoader().getResourceAsStream(filename);
		if (input == null) {
			System.out.println("Sorry, unable to find " + filename);
			return;
		}

		prop.load(input);

		

	} catch (IOException ex) {
		ex.printStackTrace();
	} 
        //finally {
	//	if (input != null) {
	//		try {
	//			input.close();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}
	//}
        
    }

    public String obtenerConfiguracion(String clave) {
        String value = "";
        Enumeration<?> e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if(clave.equalsIgnoreCase(key)){
                value = prop.getProperty(key);
                break;
            }
            //System.out.println("Key : " + key + ", Value : " + value);
	}
        return value;
    }
    
}
