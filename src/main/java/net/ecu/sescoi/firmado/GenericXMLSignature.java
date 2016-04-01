
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
    
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.StringWriter;
    import java.security.KeyStore;
    import java.security.KeyStoreException;
    import java.security.NoSuchAlgorithmException;
    import java.security.PrivateKey;
    import java.security.Provider;
    import java.security.cert.CertificateException;
    import java.security.cert.X509Certificate;
    
    
    import javax.xml.parsers.DocumentBuilderFactory;
    import javax.xml.parsers.ParserConfigurationException;
    import javax.xml.transform.Transformer;
    import javax.xml.transform.TransformerException;
    import javax.xml.transform.TransformerFactory;
    import javax.xml.transform.dom.DOMSource;
    import javax.xml.transform.stream.StreamResult;
    
    import org.w3c.dom.Document;
    import org.xml.sax.SAXException;
    
    import es.mityc.firmaJava.libreria.utilidades.UtilidadTratarNodo;
    import es.mityc.firmaJava.libreria.xades.DataToSign;
    import es.mityc.firmaJava.libreria.xades.FirmaXML;
    ///import es.mityc.javasign.issues.PassStoreKS;
    ///http://oficinavirtual.mityc.es/componentes1.0.4/MITyCLibXADES/xref-test/es/mityc/javasign/issues/PassStoreKS.html
    //import es.mityc.javasign.pkstore.PassStoreKS;
    
    import es.mityc.javasign.pkstore.CertStoreException;
    import es.mityc.javasign.pkstore.IPKStoreManager;
    import es.mityc.javasign.pkstore.keystore.KSStore;
    import java.io.FileInputStream;
    import java.io.InputStream;
    import net.ecu.sescoi.util.ParametrosConfiguracionUtil;
    import java.util.List;
    import java.util.ArrayList;
    import java.util.Iterator;
    import net.ecu.sescoi.util.Cliente;
    
    /**
     * <p>
     * Clase base que deberían extender los diferentes ejemplos para realizar firmas
     * XML.
     * </p>
     * 
     * @author Ministerio de Industria, Turismo y Comercio
     * @version 1.0
     */
    public abstract class GenericXMLSignature {
    
        /**
         * <p>
         * Almacén PKCS12 con el que se desea realizar la firma
         * </p>
         */
        ///public final static String PKCS12_RESOURCE = "/examples/usr0052.p12";
        ///public final static String PKCS12_RESOURCE = "D:\\FacturacionEletronica\\Implementacion\\CodigoFuente\\ConsumoSriWS\\certificados\\mirna_gisella_cedeno_delgado.p12";
        public static String PKCS12_RESOURCE;
        
        
        /**
         * <p>
         * Constraseña de acceso a la clave privada del usuario
         * </p>
         */
        ///public final static String PKCS12_PASSWORD = "Ecuador2015";
        public static String PKCS12_PASSWORD;
        /**
         * <p>
         * Directorio donde se almacenará el resultado de la firma
         * </p>
         */
        ///public final static String OUTPUT_DIRECTORY = ".";
        public static String OUTPUT_DIRECTORY;
        /**
         * <p>
         * Ejecución del ejemplo. La ejecución consistirá en la firma de los datos
         * creados por el método abstracto <code>createDataToSign</code> mediante el
         * certificado declarado en la constante <code>PKCS12_FILE</code>. El
         * resultado del proceso de firma será almacenado en un fichero XML en el
         * directorio correspondiente a la constante <code>OUTPUT_DIRECTORY</code>
         * del usuario bajo el nombre devuelto por el método abstracto
         * <code>getSignFileName</code>
         * </p>
         */
        public static String caracterSO = "\\";
        private ParametrosConfiguracionUtil parametrosConfiguracionUtil;
        
        
        
        public void getParametrosConfiguracion(String nombreCliente){
            parametrosConfiguracionUtil = new ParametrosConfiguracionUtil();
            PKCS12_RESOURCE = parametrosConfiguracionUtil.obtenerConfiguracion("pkcs12.resource");
            OUTPUT_DIRECTORY = parametrosConfiguracionUtil.obtenerConfiguracion("output.directory");
            
            
            int cantidadClientes = Integer.valueOf(parametrosConfiguracionUtil.obtenerConfiguracion("clientes.cantidad"));
            List<Cliente> listaClientes = new ArrayList<Cliente>();
            for(int i = 1; i <= cantidadClientes; i++)
            {
                String parteClave = "pkcs12."+ String.valueOf(i);
                String nombre = parametrosConfiguracionUtil.obtenerConfiguracion(parteClave + ".nombre");
                String archivo = parametrosConfiguracionUtil.obtenerConfiguracion(parteClave + ".archivo");
                String contrasenia = parametrosConfiguracionUtil.obtenerConfiguracion(parteClave + ".password");
                Cliente objCliente = new Cliente(nombre,archivo,contrasenia);
                listaClientes.add(objCliente);
            }
            
            for(Cliente obj : listaClientes)
            {
                if(obj.getNombre().equalsIgnoreCase(nombreCliente))
                {
                    PKCS12_RESOURCE = PKCS12_RESOURCE + caracterSO + obj.getNombreFirma();
                    PKCS12_PASSWORD = obj.getClaveFirma();
                }
            }
        }
        
        
        
        protected void execute(String nombreCliente) {
            getParametrosConfiguracion(nombreCliente);
            // Obtencion del gestor de claves
            IPKStoreManager storeManager = getPKStoreManager();
            if (storeManager == null) {
                System.err.println("El gestor de claves no se ha obtenido correctamente.");
                return;
            }
    
            // Obtencion del certificado para firmar. Utilizaremos el primer
            // certificado del almacen.
            X509Certificate certificate = getFirstCertificate(storeManager);
            if (certificate == null) {
                System.err.println("No existe ningún certificado para firmar.");
                return;
            }
    
            // Obtención de la clave privada asociada al certificado
            PrivateKey privateKey;
            try {
                privateKey = storeManager.getPrivateKey(certificate);
            } catch (CertStoreException e) {
                System.err.println("Error al acceder al almacén.");
                return;
            }
    
            // Obtención del provider encargado de las labores criptográficas
            Provider provider = storeManager.getProvider(certificate);
    
            /*
             * Creación del objeto que contiene tanto los datos a firmar como la
             * configuración del tipo de firma
             */
            DataToSign dataToSign = createDataToSign();
    
            // Firmamos el documento
            Document docSigned = null;
            try {
                /*
                 * Creación del objeto encargado de realizar la firma
                 */
                FirmaXML firma = createFirmaXML();
                Object[] res = firma.signFile(certificate, dataToSign, privateKey, provider);
                docSigned = (Document) res[0];
            } catch (Exception ex) {
                System.err.println("Error realizando la firma");
                ex.printStackTrace();
                return;
            }
    
            // Guardamos la firma a un fichero en el home del usuario
            String filePath = OUTPUT_DIRECTORY + File.separatorChar + getSignatureFileName();
            System.out.println("Firma salvada en: " + filePath);
            saveDocumentToFile(docSigned, getSignatureFileName());
        }
    
        /**
         * <p>
         * Crea el objeto DataToSign que contiene toda la información de la firma
         * que se desea realizar. Todas las implementaciones deberán proporcionar
         * una implementación de este método
         * </p>
         * 
         * @return El objeto DataToSign que contiene toda la información de la firma
         *         a realizar
         */
        protected abstract DataToSign createDataToSign();
    
        /**
         * <p>
         * Nombre del fichero donde se desea guardar la firma generada. Todas las
         * implementaciones deberán proporcionar este nombre.
         * </p>
         * 
         * @return El nombre donde se desea guardar la firma generada
         */
        protected abstract String getSignatureFileName();
    
        /**
         * <p>
         * Crea el objeto <code>FirmaXML</code> con las configuraciones necesarias
         * que se encargará de realizar la firma del documento.
         * </p>
         * <p>
         * En el caso más simple no es necesaria ninguna configuración específica.
         * En otros casos podría ser necesario por lo que las implementaciones
         * concretas de las diferentes firmas deberían sobreescribir este método
         * (por ejemplo para añadir una autoridad de sello de tiempo en aquellas
         * firmas en las que sea necesario)
         * <p>
         * 
         * 
         * @return firmaXML Objeto <code>FirmaXML</code> configurado listo para
         *         usarse
         */
        protected FirmaXML createFirmaXML() {
            return new FirmaXML();
        }
    
        /**
         * <p>
         * Escribe el documento a un fichero.
         * </p>
         * 
         * @param document
         *            El documento a imprmir
         * @param pathfile
         *            El path del fichero donde se quiere escribir.
         */
        private void saveDocumentToFile(Document document, String pathfile) {
            try {
                FileOutputStream fos = new FileOutputStream(pathfile);
                UtilidadTratarNodo.saveDocumentToOutputStream(document, fos, true);
            } catch (FileNotFoundException e) {
                System.err.println("Error al salvar el documento");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    
        /**
         * <p>
         * Escribe el documento a un fichero. Esta implementacion es insegura ya que
         * dependiendo del gestor de transformadas el contenido podría ser alterado,
         * con lo que el XML escrito no sería correcto desde el punto de vista de
         * validez de la firma.
         * </p>
         * 
         * @param document
         *            El documento a imprmir
         * @param pathfile
         *            El path del fichero donde se quiere escribir.
         */
        @SuppressWarnings("unused")
        private void saveDocumentToFileUnsafeMode(Document document, String pathfile) {
            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer serializer;
            try {
                serializer = tfactory.newTransformer();
    
                serializer.transform(new DOMSource(document), new StreamResult(new File(pathfile)));
            } catch (TransformerException e) {
                System.err.println("Error al salvar el documento");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    
        /**
         * <p>
         * Devuelve el <code>Document</code> correspondiente al
         * <code>resource</code> pasado como parámetro
         * </p>
         * 
         * @param resource
         *            El recurso que se desea obtener
         * @return El <code>Document</code> asociado al <code>resource</code>
         */
        protected Document getDocument(String resource) {
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            try {
                //doc = dbf.newDocumentBuilder().parse(this.getClass().getResourceAsStream(resource));
                File file = new File(resource);
                InputStream stream = new FileInputStream(file);
                doc = dbf.newDocumentBuilder().parse(stream);
            } catch (ParserConfigurationException ex) {
                System.err.println("Error al parsear el documento");
                ex.printStackTrace();
                System.exit(-1);
            } catch (SAXException ex) {
                System.err.println("Error al parsear el documento");
                ex.printStackTrace();
                System.exit(-1);
            } catch (IOException ex) {
                System.err.println("Error al parsear el documento");
                ex.printStackTrace();
                System.exit(-1);
            } catch (IllegalArgumentException ex) {
                System.err.println("Error al parsear el documento");
                ex.printStackTrace();
                System.exit(-1);
            }
            return doc;
        }
    
        /**
         * <p>
         * Devuelve el contenido del documento XML
         * correspondiente al <code>resource</code> pasado como parámetro
         * </p> como un <code>String</code>
         * 
         * @param resource
         *            El recurso que se desea obtener
         * @return El contenido del documento XML como un <code>String</code>
         */
        protected String getDocumentAsString(String resource) {
            Document doc = getDocument(resource);
            TransformerFactory tfactory = TransformerFactory.newInstance();
            Transformer serializer;
            StringWriter stringWriter = new StringWriter();
            try {
                serializer = tfactory.newTransformer();
                serializer.transform(new DOMSource(doc), new StreamResult(stringWriter));
            } catch (TransformerException e) {
                System.err.println("Error al imprimir el documento");
                e.printStackTrace();
                System.exit(-1);
            }
    
            return stringWriter.toString();
        }
    
        /**
         * <p>
         * Devuelve el gestor de claves que se va a utilizar
         * </p>
         * 
         * @return El gestor de claves que se va a utilizar</p>
         */
        private IPKStoreManager getPKStoreManager() {
            IPKStoreManager storeManager = null;
            try {
                
                KeyStore ks = KeyStore.getInstance("PKCS12");
                //ks.load(this.getClass().getResourceAsStream(PKCS12_RESOURCE), PKCS12_PASSWORD.toCharArray());
                File file = new File(PKCS12_RESOURCE);
                InputStream stream = new FileInputStream(file);
                ks.load(stream,PKCS12_PASSWORD.toCharArray());
                storeManager = new KSStore(ks, new PassStoreKS(PKCS12_PASSWORD));
            } catch (KeyStoreException ex) {
                System.err.println("No se puede generar KeyStore PKCS12");
                ex.printStackTrace();
                System.exit(-1);
            } catch (NoSuchAlgorithmException ex) {
                System.err.println("No se puede generar KeyStore PKCS12");
                ex.printStackTrace();
                System.exit(-1);
            } catch (CertificateException ex) {
                System.err.println("No se puede generar KeyStore PKCS12");
                ex.printStackTrace();
                System.exit(-1);
            } catch (IOException ex) {
                System.err.println("No se puede generar KeyStore PKCS12");
                ex.printStackTrace();
                System.exit(-1);
            
            } catch (Exception ex) {
                System.err.println("Error al leer certificado PKCS12");
                ex.printStackTrace();
                System.exit(-1);
            }
            
            return storeManager;
        }
    
        /**
         * <p>
         * Recupera el primero de los certificados del almacén.
         * </p>
         * 
         * @param storeManager
         *            Interfaz de acceso al almacén
         * @return Primer certificado disponible en el almacén
         */
        private X509Certificate getFirstCertificate(
                final IPKStoreManager storeManager) {
            List<X509Certificate> certs = null;
            try {
                certs = storeManager.getSignCertificates();
            } catch (CertStoreException ex) {
                System.err.println("Fallo obteniendo listado de certificados");
                System.exit(-1);
            }
            if ((certs == null) || (certs.size() == 0)) {
                System.err.println("Lista de certificados vacía");
                System.exit(-1);
            }
    
            X509Certificate certificate = certs.get(0);
            return certificate;
        }
    
    }
