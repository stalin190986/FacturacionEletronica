/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ecu.sescoi.consumosriws;


import java.io.BufferedReader;
import java.io.FileReader;
import net.ecu.sescoi.util.ParametrosConfiguracionUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Ignore;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;
import recepcion.ws.sri.gob.ec.ValidarComprobante;

/**
 *
 * @author STALIN
 */
public class SriWsRecepcionTest {
    
    static private Logger LOG = LoggerFactory.getLogger(SriWsRecepcionTest.class);
    private ParametrosConfiguracionUtil parametrosConfiguracionUtil;
    public String OUTPUT_DIRECTORY;
    
   @Test
    //@Ignore
    public void testService() throws Exception {

        parametrosConfiguracionUtil = new ParametrosConfiguracionUtil();
        OUTPUT_DIRECTORY = parametrosConfiguracionUtil.obtenerConfiguracion("output.directory");
        WSConsultaRecepcionSriPruebas client = new WSConsultaRecepcionSriPruebas();

        ValidarComprobante request = new ValidarComprobante();
        String cadenaXml = readFileXML(OUTPUT_DIRECTORY + "\\xml_ejemplo_offline_factura_v_1_1_firmado.xml");
	byte[] byteData = cadenaXml.getBytes();
        request.setXml(byteData);

        RespuestaSolicitud respuestaRecepcionCEAP = client.consultaRecepcionComprobanteElectronico(request);

        assertNotNull(respuestaRecepcionCEAP);

        assertEquals("RECIBIDA", respuestaRecepcionCEAP.getEstado());
        

    }
    
    private String readFileXML(String path) throws Exception{
            StringBuilder sb = new StringBuilder();
            String sCurrentLine;
            BufferedReader br = null;
            try{

                br = new BufferedReader(new FileReader(path));
                while((sCurrentLine = br.readLine()) != null) {
                    sb.append(sCurrentLine);
                }

            }
            catch (Exception ex){
                LOG.error("Error:", ex);
                throw new Exception("No se puede leer XML");
            }

          return sb.toString();
    }
    
}
