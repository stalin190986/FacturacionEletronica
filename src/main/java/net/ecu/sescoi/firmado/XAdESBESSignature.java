/*
     * LICENCIA LGPL:
     * 
     * Esta librería es Software Libre; Usted puede redistribuirlo y/o modificarlo
     * bajo los términos de la GNU Lesser General Public License (LGPL)
     * tal y como ha sido publicada por la Free Software Foundation; o
     * bien la versión 2.1 de la Licencia, o (a su elección) cualquier versión posterior.
     * 
     * Esta librería se distribuye con la esperanza de que sea útil, pero SIN NINGUNA
     * GARANTÍA; tampoco las implícitas garantías de MERCANTILIDAD o ADECUACIÓN A UN
     * PROPÓSITO PARTICULAR. Consulte la GNU Lesser General Public License (LGPL) para más
     * detalles
     * 
     * Usted debe recibir una copia de la GNU Lesser General Public License (LGPL)
     * junto con esta librería; si no es así, escriba a la Free Software Foundation Inc.
     * 51 Franklin Street, 5º Piso, Boston, MA 02110-1301, USA.
     * 
     */
    package net.ecu.sescoi.firmado;
    
    import org.w3c.dom.Document;
    
    import es.mityc.firmaJava.libreria.xades.DataToSign;
    import es.mityc.firmaJava.libreria.xades.EnumFormatoFirma;
    import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
    //import es.mityc.javasign.xades.examples.GenericXMLSignature;
    import es.mityc.javasign.xml.refs.AllXMLToSign;
    import es.mityc.javasign.xml.refs.ObjectToSign;
import static net.ecu.sescoi.firmado.GenericXMLSignature.OUTPUT_DIRECTORY;
import net.ecu.sescoi.util.ParametrosConfiguracionUtil;
    
    /**
     * <p>
     * Clase de ejemplo para la firma XAdES-BES enveloped de un documento
     * </p>
     * <p>
     * Para realizar la firma se utilizará el almacén PKCS#12 definido en la
     * constante <code>GenericXMLSignature.PKCS12_FILE</code>, al que se accederá
     * mediante la password definida en la constante
     * <code>GenericXMLSignature.PKCS12_PASSWORD</code>. El directorio donde quedará
     * el archivo XML resultante será el indicado en al constante
     * <code>GenericXMLSignature.OUTPUT_DIRECTORY</code>
     * </p>
     * 
     * @author Ministerio de Industria, Turismo y Comercio
     * @version 1.0
     */
    public class XAdESBESSignature extends GenericXMLSignature {
    
        /**
         * <p>
         * Recurso a firmar
         * </p>
         */
        //private  static String RESOURCE_TO_SIGN = "D:\\FacturacionEletronica\\Implementacion\\CodigoFuente\\ConsumoSriWS\\ficheros_xml\\xml_ejemplo_offline_factura_v_1_1.xml";
        private  static String RESOURCE_TO_SIGN;
        /**
         * <p>
         * Fichero donde se desea guardar la firma
         * </p>
         */
        //private  static String SIGN_FILE_NAME = "D:\\FacturacionEletronica\\Implementacion\\CodigoFuente\\ConsumoSriWS\\ficheros_xml\\xml_ejemplo_offline_factura_v_1_1_firmado.xml";
        private  static String SIGN_FILE_NAME;
        /**
         * <p>
         * Punto de entrada al programa
         * </p>
         * 
         * @param args
         *            Argumentos del programa
         */ 
        
        private ParametrosConfiguracionUtil parametrosConfiguracionUtil;
        public String INPUT_DIRECTORY;
        public String OUTPUT_DIRECTORY;
        
        
        public static void main(String[] args) {
            String nombreCliente = "sescoi";
            XAdESBESSignature signature = new XAdESBESSignature();
            signature.obtenerParametrosConfiguracion();
            signature.execute(nombreCliente);
        }
        
        private void obtenerParametrosConfiguracion(){
            parametrosConfiguracionUtil = new ParametrosConfiguracionUtil();
            INPUT_DIRECTORY = parametrosConfiguracionUtil.obtenerConfiguracion("input.directory");
            OUTPUT_DIRECTORY = parametrosConfiguracionUtil.obtenerConfiguracion("output.directory");
            RESOURCE_TO_SIGN = INPUT_DIRECTORY + "\\xml_ejemplo_offline_factura_v_1_1.xml";
            SIGN_FILE_NAME = OUTPUT_DIRECTORY + "\\xml_ejemplo_offline_factura_v_1_1_firmado.xml";
        }
    
        @Override
        protected DataToSign createDataToSign() {
            DataToSign dataToSign = new DataToSign();
            dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
            dataToSign.setEsquema(XAdESSchemas.XAdES_132);
            dataToSign.setXMLEncoding("UTF-8");
            dataToSign.setEnveloped(true);
            dataToSign.addObject(new ObjectToSign(new AllXMLToSign(), "Firmado por SESCOI SA Ecuador", null, "text/xml", null));
            Document docToSign = getDocument(RESOURCE_TO_SIGN);
            dataToSign.setDocument(docToSign);
            return dataToSign;
        }
    
        @Override
        protected String getSignatureFileName() {
            return SIGN_FILE_NAME;
        }
        
        
    }