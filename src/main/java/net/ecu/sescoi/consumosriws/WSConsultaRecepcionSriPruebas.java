/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.ecu.sescoi.consumosriws;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import recepcion.ws.sri.gob.ec.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
/**
 *
 * @author STALIN
 */
public class WSConsultaRecepcionSriPruebas {
    
    static private Logger LOG = LoggerFactory.getLogger(WSConsultaRecepcionSriPruebas.class);
    public static final String nsSchema = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
    public static final String soapSchema = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String encodingStyle = "http://schemas.xmlsoap.org/soap/encoding/";
    ///public static final String targetNS = "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl";
    public static final String targetNS = "http://ec.gob.sri.ws.recepcion";
    private static URL wsdl = null;
    private static JAXBContext jaxbContext = null;

    static {
        //Load the wsdl locally to cut down on network traffic
        wsdl = WSConsultaRecepcionSriPruebas.class.getResource("/wsdl/RecepcionComprobantesOffline.wsdl");
        try {
            //JAXBContext's are thread safe, so just use one
            jaxbContext = JAXBContext.newInstance(RespuestaSolicitud.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
        public RespuestaSolicitud consultaRecepcionComprobanteElectronico(ValidarComprobante request) throws Exception {

        try {
            
             Base64 base64 = new Base64();
            //byte[] encoded = base64.encode(raw);
            
            StringBuilder sb = new StringBuilder();
            sb.append("<soapenv:Envelope xmlns:soapenv=\"");
            sb.append(soapSchema);
            sb.append("\" xmlns:ec=\"");
            sb.append(targetNS);
            sb.append("\">");
            sb.append("<soapenv:Header/>");
            sb.append("<soapenv:Body>");
                sb.append("<ec:validarComprobante>");
                    sb.append("<xml>");
                        ///sb.append(request.getXml());
                    sb.append(Base64.encodeBase64(base64.encode(request.getXml())));
                    sb.append("</xml>");
            sb.append("</ec:validarComprobante>");
            sb.append("</soapenv:Body>");
            sb.append("</soapenv:Envelope>");

            QName serviceName = new QName(targetNS, "RecepcionComprobantesOfflineService");
            QName portName = new QName(targetNS, "RecepcionComprobantesOfflinePort");
            Service service = Service.create(wsdl, serviceName);

            Dispatch<Source> dispatch = service.createDispatch(portName, Source.class, Service.Mode.MESSAGE);
            Source response = dispatch.invoke(new StreamSource(new StringReader(sb.toString())));

            MessageFactory msgFactory = MessageFactory.newInstance();
            SOAPMessage soapMsg = msgFactory.createMessage();
            SOAPPart env = soapMsg.getSOAPPart();
            env.setContent(response);

            if (soapMsg.getSOAPBody().hasFault()) {
                SOAPFault fault = soapMsg.getSOAPBody().getFault();
                throw new Exception(fault.getFaultString() + "; " + fault.getDetail().getValue());
            }

            RespuestaSolicitud.Comprobantes listaComprobantesRS = null;
            RespuestaSolicitud objRs = new RespuestaSolicitud();
            String respuestaSriRecepcion = "DEVUELTA";
	    ///String respuestaSriRecepcion = soapMsg.getSOAPBody().getElementsByTagName("RespuestaRecepcionComprobante").item(0).getFirstChild().getNodeValue();
	    
            NodeList listaComprobantes = soapMsg.getSOAPBody().getFirstChild().getFirstChild().getFirstChild().getChildNodes();
           // listaComprobantesRS = (RespuestaSolicitud.Comprobantes) listaComprobantes;
            
            if (listaComprobantes != null && listaComprobantes.getLength() > 0) {
                
                for (int i = 0; i < listaComprobantes.getLength(); i++) {
                    if (listaComprobantes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        
                        Element comprobante = (Element) listaComprobantes.item(i);
                        NodeList listaMensajesComprobante = comprobante.getLastChild().getChildNodes();
                        
                        if (listaMensajesComprobante != null && listaMensajesComprobante.getLength() > 0) {
                             for (int j = 0; j < listaMensajesComprobante.getLength(); j++) {
                                if (listaMensajesComprobante.item(i).getNodeType() == Node.ELEMENT_NODE) {
                                Element mensaje = (Element) listaMensajesComprobante.item(i);
                                String identificador = mensaje.getElementsByTagName("identificador").item(0).getTextContent();
                                String txtMensaje = mensaje.getElementsByTagName("mensaje").item(0).getTextContent();
                                String informacionAdicional = mensaje.getElementsByTagName("informacionAdicional").item(0).getTextContent();
                                System.out.println(identificador + " => " + txtMensaje + " => " + informacionAdicional);
                                }
                             }
                        }
                    }
                }
            }
            else
                respuestaSriRecepcion = "RECIBIDA";
            
             objRs.setComprobantes(listaComprobantesRS);
             objRs.setEstado(respuestaSriRecepcion);
            
//            if(objRs.getEstado().equalsIgnoreCase("DEVUELTA")){
//              
//              listaComprobantes =  (RespuestaSolicitud.Comprobantes)soapMsg.getSOAPBody().getElementsByTagName("RespuestaRecepcionComprobante").item(0).getLastChild().getChildNodes();
//              objRs.setComprobantes(listaComprobantes);
//            }
            
	    
	    
            return objRs;
            
        } catch (JAXBException jbe) {
            LOG.error("Error JAXBException:", jbe);
	    throw new Exception("A server exception JAXBException occurred");
            ///throw new ConsultaCedulaFault_Exception("A server exception occurred");
        } catch (SOAPFaultException sfe) {
            LOG.error("Error SOAPFaultException:", sfe);
	    throw new Exception("A server exception SOAPFaultException occurred");
            //Pass the SOAP Fault directly to the client
            ///throw new ConsultaCedulaFault_Exception(sfe.getFault().getFaultString());
        } catch (SOAPException e) {
            LOG.error("Error SOAPException:", e);
	    throw new Exception("A server exception SOAPException occurred");
            ///throw new ConsultaCedulaFault_Exception("A server exception occurred");
        } catch (UnsupportedEncodingException uee) {
            LOG.error("Error UnsupportedEncodingException:", uee);
	    throw new Exception("A server exception UnsupportedEncodingException occurred");
            ///throw new ConsultaCedulaFault_Exception("A server exception occurred");
        } 
         catch (Exception ex) {
            LOG.error("Error :", ex);
	    throw new Exception("A server exception occurred");
            ///throw new ConsultaCedulaFault_Exception("A server exception occurred");
        }

    }
    
        
    
        
}///FIN FIN CLASE WSConsultaRecepcionSriPruebas
