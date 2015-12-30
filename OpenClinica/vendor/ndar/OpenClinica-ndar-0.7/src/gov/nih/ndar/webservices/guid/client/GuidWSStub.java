/**
 * GuidWSStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.1 Nov 13, 2006 (07:31:44 LKT)
 */
package gov.nih.ndar.webservices.guid.client;

import java.io.FileNotFoundException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;

/*
 *  GuidWSStub java implementation
 */

public class GuidWSStub extends org.apache.axis2.client.Stub {
    protected org.apache.axis2.description.AxisOperation[] _operations;

    //hashmaps to keep the fault mapping
    private java.util.HashMap faultExeptionNameMap = new java.util.HashMap();

    private java.util.HashMap faultExeptionClassNameMap = new java.util.HashMap();

    private java.util.HashMap faultMessageMap = new java.util.HashMap();

    private void populateAxisService() throws org.apache.axis2.AxisFault {

        //creating the Service with a unique name
        _service = new org.apache.axis2.description.AxisService("GuidWS"
                + this.hashCode());

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[2];

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation
                .setName(new javax.xml.namespace.QName("", "getGuidWSConfig"));
        _service.addOperation(__operation);

        _operations[0] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("", "getGuid"));
        _service.addOperation(__operation);

        _operations[1] = __operation;

    }

    //populates the faults
    private void populateFaults() {

        faultExeptionNameMap.put(new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidFault"),
                "gov.nih.ndar.webservices.guid.clients.GetGuidFaultException");
        faultExeptionClassNameMap.put(new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidFault"),
                "gov.nih.ndar.webservices.guid.clients.GetGuidFaultException");
        faultMessageMap.put(new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidFault"),
                "gov.nih.ndar.webservices.guid.clients.GuidWSStub$GuidFault");

        faultExeptionNameMap.put(new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidFault"),
                "gov.nih.ndar.webservices.guid.clients.GetGuidFaultException");
        faultExeptionClassNameMap.put(new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidFault"),
                "gov.nih.ndar.webservices.guid.clients.GetGuidFaultException");
        faultMessageMap.put(new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidFault"),
                "gov.nih.ndar.webservices.guid.clients.GuidWSStub$GuidFault");

    }

    /**
     Constructor that takes in a configContext
     */
    public GuidWSStub(
            org.apache.axis2.context.ConfigurationContext configurationContext,
            java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        //To populate AxisService
        populateAxisService();
        populateFaults();

        _serviceClient = new org.apache.axis2.client.ServiceClient(
                configurationContext, _service);

        configurationContext = _serviceClient.getServiceContext()
                .getConfigurationContext();

        _serviceClient.getOptions().setTo(
                new org.apache.axis2.addressing.EndpointReference(
                        targetEndpoint));

    }

    /**
    Constructor that takes in a configContext
    */
   public GuidWSStub(
           org.apache.axis2.context.ConfigurationContext configurationContext,
           java.lang.String targetEndpoint, java.lang.String policyPath) throws org.apache.axis2.AxisFault {
       //To populate AxisService
       populateAxisService();
       populateFaults();

       _serviceClient = new org.apache.axis2.client.ServiceClient(
               configurationContext, _service);

       configurationContext = _serviceClient.getServiceContext()
               .getConfigurationContext();

       _serviceClient.getOptions().setTo(
               new org.apache.axis2.addressing.EndpointReference(
                       targetEndpoint));
       
       StAXOMBuilder builder = null;
       try{
       	builder = new StAXOMBuilder(policyPath);
       }catch(FileNotFoundException e){
       	e.printStackTrace();
       }catch(XMLStreamException e){
       	e.printStackTrace();
       }catch(Exception e){
       	e.printStackTrace();
       }
       Policy policy = PolicyEngine.getPolicy(builder.getDocumentElement());
       _serviceClient.getOptions().setProperty(RampartMessageData.KEY_RAMPART_POLICY, policy);
       
       _serviceClient.engageModule(new QName("addressing"));
       _serviceClient.engageModule(new QName("rampart"));
   }

    /**
     * Default Constructor
     */
    public GuidWSStub() throws org.apache.axis2.AxisFault {

        this("http://localhost:8080/axis2/services/GuidWS");

    }

    /**
     * Constructor taking the target endpoint
     */
    public GuidWSStub(java.lang.String targetEndpoint)
            throws org.apache.axis2.AxisFault {
        this(null, targetEndpoint);
    }

    /**
     * Auto generated method signature
     * @see gov.nih.ndar.webservices.guid.clients.GuidWS#getGuidWSConfig
     */
    public gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidWSConfigResponse getGuidWSConfig(

    ) throws java.rmi.RemoteException, gov.nih.ndar.webservices.guid.client.GetGuidFaultException {
        try {
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient
                    .createClient(_operations[0].getName());
            _operationClient.getOptions().setAction(
                    "http://ndar.nih.gov/services/GuidService/getGuidWSConfig");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(
                    true);

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is taken to be "document". No input parameters
            org.apache.axiom.soap.SOAPFactory factory = getFactory(_operationClient
                    .getOptions().getSoapVersionURI());
            env = factory.getDefaultEnvelope();
            env.getBody().addChild(
                    factory.createOMElement("getGuidWSConfig", "", ""));

            //adding SOAP headers
            _serviceClient.addHeadersToEnvelope(env);
            // create message context with that soap envelope
            org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
                    .getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext
                    .getEnvelope();

            java.lang.Object object = fromOM(
                    _returnEnv.getBody().getFirstElement(),
                    gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidWSConfigResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(
                    _messageContext);
            return (gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidWSConfigResponse) object;

        } catch (org.apache.axis2.AxisFault f) {
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExeptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        java.lang.String exceptionClassName = (java.lang.String) faultExeptionClassNameMap
                                .get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class
                                .forName(exceptionClassName);
                        java.lang.Exception ex = (java.lang.Exception) exceptionClass
                                .newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String) faultMessageMap
                                .get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class
                                .forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,
                                messageClass, null);
                        java.lang.reflect.Method m = exceptionClass.getMethod(
                                "setFaultMessage",
                                new java.lang.Class[] { messageClass });
                        m.invoke(ex, new java.lang.Object[] { messageObject });

                        if (ex instanceof gov.nih.ndar.webservices.guid.client.GetGuidFaultException) {
                            throw (gov.nih.ndar.webservices.guid.client.GetGuidFaultException) ex;
                        }

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        }
    }

    /**
     * Auto generated method signature
     * @see gov.nih.ndar.webservices.guid.client.GuidWS#getGuid
     * @param param2
     
     */
    public gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidResponse getGuid(

    gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidRequest param2)
            throws java.rmi.RemoteException

            , gov.nih.ndar.webservices.guid.client.GetGuidFaultException {
        try {
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient
                    .createClient(_operations[1].getName());
            _operationClient.getOptions().setAction(
                    "http://ndar.nih.gov/services/GuidService/getGuid");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(
                    true);

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            //Style is Doc.

            env = toEnvelope(
                    getFactory(_operationClient.getOptions()
                            .getSoapVersionURI()),
                    param2,
                    optimizeContent(new javax.xml.namespace.QName("", "getGuid")));

            //adding SOAP headers
            _serviceClient.addHeadersToEnvelope(env);
            // create message context with that soap envelope
            org.apache.axis2.context.MessageContext _messageContext = new org.apache.axis2.context.MessageContext();
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
                    .getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext
                    .getEnvelope();

            java.lang.Object object = fromOM(
                    _returnEnv.getBody().getFirstElement(),
                    gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidResponse.class,
                    getEnvelopeNamespaces(_returnEnv));
            _messageContext.getTransportOut().getSender().cleanup(
                    _messageContext);
            return (gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidResponse) object;

        } catch (org.apache.axis2.AxisFault f) {
            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExeptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        java.lang.String exceptionClassName = (java.lang.String) faultExeptionClassNameMap
                                .get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class
                                .forName(exceptionClassName);
                        java.lang.Exception ex = (java.lang.Exception) exceptionClass
                                .newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String) faultMessageMap
                                .get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class
                                .forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt,
                                messageClass, null);
                        java.lang.reflect.Method m = exceptionClass.getMethod(
                                "setFaultMessage",
                                new java.lang.Class[] { messageClass });
                        m.invoke(ex, new java.lang.Object[] { messageObject });

                        if (ex instanceof gov.nih.ndar.webservices.guid.client.GetGuidFaultException) {
                            throw (gov.nih.ndar.webservices.guid.client.GetGuidFaultException) ex;
                        }

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    } catch (java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    } catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                } else {
                    throw f;
                }
            } else {
                throw f;
            }
        }
    }

    /**
     *  A utility method that copies the namepaces from the SOAPEnvelope
     */
    private java.util.Map getEnvelopeNamespaces(
            org.apache.axiom.soap.SOAPEnvelope env) {
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
            org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator
                    .next();
            returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
        }
        return returnMap;
    }

    private javax.xml.namespace.QName[] opNameArray = null;

    private boolean optimizeContent(javax.xml.namespace.QName opName) {

        if (opNameArray == null) {
            return false;
        }
        for (int i = 0; i < opNameArray.length; i++) {
            if (opName.equals(opNameArray[i])) {
                return true;
            }
        }
        return false;
    }

    //http://localhost:8080/axis2/services/GuidWS
    public static class ExtensionMapper {

        public static java.lang.Object getTypeObject(
                java.lang.String namespaceURI, java.lang.String typeName,
                javax.xml.stream.XMLStreamReader reader)
                throws java.lang.Exception {

            if ("http://ndar.nih.gov/webservices/guid".equals(namespaceURI)
                    && "HashCodeMatchRule".equals(typeName)) {

                return HashCodeMatchRule.Factory.parse(reader);

            }

            if ("http://ndar.nih.gov/webservices/guid".equals(namespaceURI)
                    && "SubjectMatchRule".equals(typeName)) {

                return SubjectMatchRule.Factory.parse(reader);

            }

            throw new java.lang.RuntimeException("Unsupported type "
                    + namespaceURI + " " + typeName);
        }

    }

    public static class HashCodeMatchRule implements
            org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
         name = HashCodeMatchRule
         Namespace URI = http://ndar.nih.gov/webservices/guid
         Namespace Prefix = ns1
         */

        /**
         * field for UpperT
         */

        protected int localUpperT;

        /**
         * Auto generated getter method
         * @return int
         */
        public int getUpperT() {
            return localUpperT;
        }

        /**
         * Auto generated setter method
         * @param param UpperT
         */
        public void setUpperT(int param) {

            this.localUpperT = param;

        }

        /**
         * field for LowerT
         */

        protected int localLowerT;

        /**
         * Auto generated getter method
         * @return int
         */
        public int getLowerT() {
            return localLowerT;
        }

        /**
         * Auto generated setter method
         * @param param LowerT
         */
        public void setLowerT(int param) {

            this.localLowerT = param;

        }

        /**
         *
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(
                    this, parentQName) {

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {

                    java.lang.String prefix = parentQName.getPrefix();
                    java.lang.String namespace = parentQName.getNamespaceURI();

                    if (namespace != null) {
                        java.lang.String writerPrefix = xmlWriter
                                .getPrefix(namespace);
                        if (writerPrefix != null) {
                            xmlWriter.writeStartElement(namespace, parentQName
                                    .getLocalPart());
                        } else {
                            if (prefix == null) {
                                prefix = org.apache.axis2.databinding.utils.BeanUtil
                                        .getUniquePrefix();
                            }

                            xmlWriter.writeStartElement(prefix, parentQName
                                    .getLocalPart(), namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);
                        }
                    } else {
                        xmlWriter.writeStartElement(parentQName.getLocalPart());
                    }

                    namespace = "";
                    if (!namespace.equals("")) {
                        prefix = xmlWriter.getPrefix(namespace);

                        if (prefix == null) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil
                                    .getUniquePrefix();

                            xmlWriter.writeStartElement(prefix, "upperT",
                                    namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);

                        } else {
                            xmlWriter.writeStartElement(namespace, "upperT");
                        }

                    } else {
                        xmlWriter.writeStartElement("upperT");
                    }

                    xmlWriter
                            .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localUpperT));

                    xmlWriter.writeEndElement();

                    namespace = "";
                    if (!namespace.equals("")) {
                        prefix = xmlWriter.getPrefix(namespace);

                        if (prefix == null) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil
                                    .getUniquePrefix();

                            xmlWriter.writeStartElement(prefix, "lowerT",
                                    namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);

                        } else {
                            xmlWriter.writeStartElement(namespace, "lowerT");
                        }

                    } else {
                        xmlWriter.writeStartElement("lowerT");
                    }

                    xmlWriter
                            .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localLowerT));

                    xmlWriter.writeEndElement();

                    xmlWriter.writeEndElement();

                }

                /**
                 * Util method to write an attribute with the ns prefix
                 */
                private void writeAttribute(java.lang.String prefix,
                        java.lang.String namespace, java.lang.String attName,
                        java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (xmlWriter.getPrefix(namespace) == null) {
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }

                    xmlWriter.writeAttribute(namespace, attName, attValue);

                }

                /**
                 * Util method to write an attribute without the ns prefix
                 */
                private void writeAttribute(java.lang.String namespace,
                        java.lang.String attName, java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (namespace.equals("")) {
                        xmlWriter.writeAttribute(attName, attValue);
                    } else {
                        registerPrefix(xmlWriter, namespace);
                        xmlWriter.writeAttribute(namespace, attName, attValue);
                    }
                }

                /**
                 * Register a namespace prefix
                 */
                private java.lang.String registerPrefix(
                        javax.xml.stream.XMLStreamWriter xmlWriter,
                        java.lang.String namespace)
                        throws javax.xml.stream.XMLStreamException {
                    java.lang.String prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = createPrefix();

                        while (xmlWriter.getNamespaceContext().getNamespaceURI(
                                prefix) != null) {
                            prefix = createPrefix();
                        }

                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);
                    }

                    return prefix;
                }

                /**
                 * Create a prefix
                 */
                private java.lang.String createPrefix() {
                    return "ns" + (int) Math.random();
                }
            };

            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    parentQName, factory, dataSource);

        }

        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            elementList.add(new javax.xml.namespace.QName("", "upperT"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                    .convertToString(localUpperT));

            elementList.add(new javax.xml.namespace.QName("", "lowerT"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                    .convertToString(localLowerT));

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(
                    qName, elementList.toArray(), attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             *                If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static HashCodeMatchRule parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                HashCodeMatchRule object = new HashCodeMatchRule();
                int event;
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader
                            .getAttributeValue(
                                    "http://www.w3.org/2001/XMLSchema-instance",
                                    "type") != null) {
                        java.lang.String fullTypeName = reader
                                .getAttributeValue(
                                        "http://www.w3.org/2001/XMLSchema-instance",
                                        "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = fullTypeName.substring(
                                    0, fullTypeName.indexOf(":"));
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName
                                    .substring(fullTypeName.indexOf(":") + 1);
                            if (!"HashCodeMatchRule".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader
                                        .getNamespaceContext().getNamespaceURI(
                                                nsPrefix);
                                return (HashCodeMatchRule) ExtensionMapper
                                        .getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    boolean isReaderMTOMAware = false;

                    try {
                        isReaderMTOMAware = java.lang.Boolean.TRUE
                                .equals(reader
                                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
                    } catch (java.lang.IllegalArgumentException e) {
                        isReaderMTOMAware = false;
                    }

                    reader.next();

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "upperT").equals(reader.getName())) {

                                java.lang.String content = reader
                                        .getElementText();

                                object
                                        .setUpperT(org.apache.axis2.databinding.utils.ConverterUtil
                                                .convertToInt(content));

                                reader.next();

                            } // End of if for expected property start element

                            else

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "lowerT").equals(reader.getName())) {

                                java.lang.String content = reader
                                        .getElementText();

                                object
                                        .setLowerT(org.apache.axis2.databinding.utils.ConverterUtil
                                                .convertToInt(content));

                                reader.next();

                            } // End of if for expected property start element

                            else {
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new java.lang.RuntimeException(
                                        "Unexpected subelement "
                                                + reader.getLocalName());
                            }

                        } else
                            reader.next();
                    } // end of while loop

                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class GuidResponse implements
            org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidResponse", "ns1");

        /**
         * field for GuidResponse
         */

        protected java.lang.String localGuidResponse;

        /**
         * Auto generated getter method
         * @return java.lang.String
         */
        public java.lang.String getGuidResponse() {
            return localGuidResponse;
        }

        /**
         * Auto generated setter method
         * @param param GuidResponse
         */
        public void setGuidResponse(java.lang.String param) {

            this.localGuidResponse = param;

        }

        /**
         *
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(
                    this, parentQName) {

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {

                    //We can safely assume an element has only one type associated with it

                    java.lang.String namespace = "http://ndar.nih.gov/webservices/guid";
                    java.lang.String localName = "GuidResponse";

                    if (!namespace.equals("")) {
                        java.lang.String prefix = xmlWriter
                                .getPrefix(namespace);

                        if (prefix == null) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil
                                    .getUniquePrefix();

                            xmlWriter.writeStartElement(prefix, localName,
                                    namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);

                        } else {
                            xmlWriter.writeStartElement(namespace, localName);
                        }

                    } else {
                        xmlWriter.writeStartElement(localName);
                    }

                    if (localGuidResponse == null) {

                        throw new RuntimeException(
                                "testValue cannot be null !!");

                    } else {

                        xmlWriter
                                .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                        .convertToString(localGuidResponse));

                    }

                    xmlWriter.writeEndElement();

                }

                /**
                 * Util method to write an attribute with the ns prefix
                 */
                private void writeAttribute(java.lang.String prefix,
                        java.lang.String namespace, java.lang.String attName,
                        java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (xmlWriter.getPrefix(namespace) == null) {
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }

                    xmlWriter.writeAttribute(namespace, attName, attValue);

                }

                /**
                 * Util method to write an attribute without the ns prefix
                 */
                private void writeAttribute(java.lang.String namespace,
                        java.lang.String attName, java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (namespace.equals("")) {
                        xmlWriter.writeAttribute(attName, attValue);
                    } else {
                        registerPrefix(xmlWriter, namespace);
                        xmlWriter.writeAttribute(namespace, attName, attValue);
                    }
                }

                /**
                 * Register a namespace prefix
                 */
                private java.lang.String registerPrefix(
                        javax.xml.stream.XMLStreamWriter xmlWriter,
                        java.lang.String namespace)
                        throws javax.xml.stream.XMLStreamException {
                    java.lang.String prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = createPrefix();

                        while (xmlWriter.getNamespaceContext().getNamespaceURI(
                                prefix) != null) {
                            prefix = createPrefix();
                        }

                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);
                    }

                    return prefix;
                }

                /**
                 * Create a prefix
                 */
                private java.lang.String createPrefix() {
                    return "ns" + (int) Math.random();
                }
            };

            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    MY_QNAME, factory, dataSource);

        }

        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) {

            //We can safely assume an element has only one type associated with it
            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(
                    MY_QNAME,
                    new java.lang.Object[] {
                            org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT,
                            org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localGuidResponse) }, null);

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             *                If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GuidResponse parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                GuidResponse object = new GuidResponse();
                int event;
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    boolean isReaderMTOMAware = false;

                    try {
                        isReaderMTOMAware = java.lang.Boolean.TRUE
                                .equals(reader
                                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
                    } catch (java.lang.IllegalArgumentException e) {
                        isReaderMTOMAware = false;
                    }

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName(
                                            "http://ndar.nih.gov/webservices/guid",
                                            "GuidResponse").equals(reader
                                            .getName())) {

                                java.lang.String content = reader
                                        .getElementText();

                                object
                                        .setGuidResponse(org.apache.axis2.databinding.utils.ConverterUtil
                                                .convertToString(content));

                            } // End of if for expected property start element

                            else {
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new java.lang.RuntimeException(
                                        "Unexpected subelement "
                                                + reader.getLocalName());
                            }

                        } else
                            reader.next();
                    } // end of while loop

                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class SubjectMatchRule implements
            org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
         name = SubjectMatchRule
         Namespace URI = http://ndar.nih.gov/webservices/guid
         Namespace Prefix = ns1
         */

        /**
         * field for ThresholdForPerfectMatch
         */

        protected int localThresholdForPerfectMatch;

        /**
         * Auto generated getter method
         * @return int
         */
        public int getThresholdForPerfectMatch() {
            return localThresholdForPerfectMatch;
        }

        /**
         * Auto generated setter method
         * @param param ThresholdForPerfectMatch
         */
        public void setThresholdForPerfectMatch(int param) {

            this.localThresholdForPerfectMatch = param;

        }

        /**
         * field for ThresholdForGoodMatch
         */

        protected int localThresholdForGoodMatch;

        /**
         * Auto generated getter method
         * @return int
         */
        public int getThresholdForGoodMatch() {
            return localThresholdForGoodMatch;
        }

        /**
         * Auto generated setter method
         * @param param ThresholdForGoodMatch
         */
        public void setThresholdForGoodMatch(int param) {

            this.localThresholdForGoodMatch = param;

        }

        /**
         * field for ThresholdForMixedMatch
         */

        protected int localThresholdForMixedMatch;

        /**
         * Auto generated getter method
         * @return int
         */
        public int getThresholdForMixedMatch() {
            return localThresholdForMixedMatch;
        }

        /**
         * Auto generated setter method
         * @param param ThresholdForMixedMatch
         */
        public void setThresholdForMixedMatch(int param) {

            this.localThresholdForMixedMatch = param;

        }

        /**
         *
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(
                    this, parentQName) {

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {

                    java.lang.String prefix = parentQName.getPrefix();
                    java.lang.String namespace = parentQName.getNamespaceURI();

                    if (namespace != null) {
                        java.lang.String writerPrefix = xmlWriter
                                .getPrefix(namespace);
                        if (writerPrefix != null) {
                            xmlWriter.writeStartElement(namespace, parentQName
                                    .getLocalPart());
                        } else {
                            if (prefix == null) {
                                prefix = org.apache.axis2.databinding.utils.BeanUtil
                                        .getUniquePrefix();
                            }

                            xmlWriter.writeStartElement(prefix, parentQName
                                    .getLocalPart(), namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);
                        }
                    } else {
                        xmlWriter.writeStartElement(parentQName.getLocalPart());
                    }

                    namespace = "";
                    if (!namespace.equals("")) {
                        prefix = xmlWriter.getPrefix(namespace);

                        if (prefix == null) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil
                                    .getUniquePrefix();

                            xmlWriter.writeStartElement(prefix,
                                    "thresholdForPerfectMatch", namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);

                        } else {
                            xmlWriter.writeStartElement(namespace,
                                    "thresholdForPerfectMatch");
                        }

                    } else {
                        xmlWriter.writeStartElement("thresholdForPerfectMatch");
                    }

                    xmlWriter
                            .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localThresholdForPerfectMatch));

                    xmlWriter.writeEndElement();

                    namespace = "";
                    if (!namespace.equals("")) {
                        prefix = xmlWriter.getPrefix(namespace);

                        if (prefix == null) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil
                                    .getUniquePrefix();

                            xmlWriter.writeStartElement(prefix,
                                    "thresholdForGoodMatch", namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);

                        } else {
                            xmlWriter.writeStartElement(namespace,
                                    "thresholdForGoodMatch");
                        }

                    } else {
                        xmlWriter.writeStartElement("thresholdForGoodMatch");
                    }

                    xmlWriter
                            .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localThresholdForGoodMatch));

                    xmlWriter.writeEndElement();

                    namespace = "";
                    if (!namespace.equals("")) {
                        prefix = xmlWriter.getPrefix(namespace);

                        if (prefix == null) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil
                                    .getUniquePrefix();

                            xmlWriter.writeStartElement(prefix,
                                    "thresholdForMixedMatch", namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);

                        } else {
                            xmlWriter.writeStartElement(namespace,
                                    "thresholdForMixedMatch");
                        }

                    } else {
                        xmlWriter.writeStartElement("thresholdForMixedMatch");
                    }

                    xmlWriter
                            .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localThresholdForMixedMatch));

                    xmlWriter.writeEndElement();

                    xmlWriter.writeEndElement();

                }

                /**
                 * Util method to write an attribute with the ns prefix
                 */
                private void writeAttribute(java.lang.String prefix,
                        java.lang.String namespace, java.lang.String attName,
                        java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (xmlWriter.getPrefix(namespace) == null) {
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }

                    xmlWriter.writeAttribute(namespace, attName, attValue);

                }

                /**
                 * Util method to write an attribute without the ns prefix
                 */
                private void writeAttribute(java.lang.String namespace,
                        java.lang.String attName, java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (namespace.equals("")) {
                        xmlWriter.writeAttribute(attName, attValue);
                    } else {
                        registerPrefix(xmlWriter, namespace);
                        xmlWriter.writeAttribute(namespace, attName, attValue);
                    }
                }

                /**
                 * Register a namespace prefix
                 */
                private java.lang.String registerPrefix(
                        javax.xml.stream.XMLStreamWriter xmlWriter,
                        java.lang.String namespace)
                        throws javax.xml.stream.XMLStreamException {
                    java.lang.String prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = createPrefix();

                        while (xmlWriter.getNamespaceContext().getNamespaceURI(
                                prefix) != null) {
                            prefix = createPrefix();
                        }

                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);
                    }

                    return prefix;
                }

                /**
                 * Create a prefix
                 */
                private java.lang.String createPrefix() {
                    return "ns" + (int) Math.random();
                }
            };

            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    parentQName, factory, dataSource);

        }

        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            elementList.add(new javax.xml.namespace.QName("",
                    "thresholdForPerfectMatch"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                    .convertToString(localThresholdForPerfectMatch));

            elementList.add(new javax.xml.namespace.QName("",
                    "thresholdForGoodMatch"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                    .convertToString(localThresholdForGoodMatch));

            elementList.add(new javax.xml.namespace.QName("",
                    "thresholdForMixedMatch"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                    .convertToString(localThresholdForMixedMatch));

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(
                    qName, elementList.toArray(), attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             *                If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static SubjectMatchRule parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                SubjectMatchRule object = new SubjectMatchRule();
                int event;
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader
                            .getAttributeValue(
                                    "http://www.w3.org/2001/XMLSchema-instance",
                                    "type") != null) {
                        java.lang.String fullTypeName = reader
                                .getAttributeValue(
                                        "http://www.w3.org/2001/XMLSchema-instance",
                                        "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = fullTypeName.substring(
                                    0, fullTypeName.indexOf(":"));
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName
                                    .substring(fullTypeName.indexOf(":") + 1);
                            if (!"SubjectMatchRule".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader
                                        .getNamespaceContext().getNamespaceURI(
                                                nsPrefix);
                                return (SubjectMatchRule) ExtensionMapper
                                        .getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    boolean isReaderMTOMAware = false;

                    try {
                        isReaderMTOMAware = java.lang.Boolean.TRUE
                                .equals(reader
                                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
                    } catch (java.lang.IllegalArgumentException e) {
                        isReaderMTOMAware = false;
                    }

                    reader.next();

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "thresholdForPerfectMatch")
                                            .equals(reader.getName())) {

                                java.lang.String content = reader
                                        .getElementText();

                                object
                                        .setThresholdForPerfectMatch(org.apache.axis2.databinding.utils.ConverterUtil
                                                .convertToInt(content));

                                reader.next();

                            } // End of if for expected property start element

                            else

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "thresholdForGoodMatch").equals(reader
                                            .getName())) {

                                java.lang.String content = reader
                                        .getElementText();

                                object
                                        .setThresholdForGoodMatch(org.apache.axis2.databinding.utils.ConverterUtil
                                                .convertToInt(content));

                                reader.next();

                            } // End of if for expected property start element

                            else

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "thresholdForMixedMatch").equals(reader
                                            .getName())) {

                                java.lang.String content = reader
                                        .getElementText();

                                object
                                        .setThresholdForMixedMatch(org.apache.axis2.databinding.utils.ConverterUtil
                                                .convertToInt(content));

                                reader.next();

                            } // End of if for expected property start element

                            else {
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new java.lang.RuntimeException(
                                        "Unexpected subelement "
                                                + reader.getLocalName());
                            }

                        } else
                            reader.next();
                    } // end of while loop

                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class GuidRequest implements
            org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidRequest", "ns1");

        /**
         * field for HashCode1
         * This was an Array!
         */

        protected byte[] localHashCode1;

        /**
         * Auto generated getter method
         * @return byte[]
         */
        public byte[] getHashCode1() {
            return localHashCode1;
        }

        /**
         * validate the array for HashCode1
         */
        protected void validateHashCode1(byte[] param) {

            if ((param != null) && (param.length > 65)) {
                throw new java.lang.RuntimeException();
            }

            if ((param != null) && (param.length < 65)) {
                throw new java.lang.RuntimeException();
            }

        }

        /**
         * Auto generated setter method
         * @param param HashCode1
         */
        public void setHashCode1(byte[] param) {

            validateHashCode1(param);

            this.localHashCode1 = param;
        }

        /**
         * field for HashCode2
         * This was an Array!
         */

        protected byte[] localHashCode2;

        /**
         * Auto generated getter method
         * @return byte[]
         */
        public byte[] getHashCode2() {
            return localHashCode2;
        }

        /**
         * validate the array for HashCode2
         */
        protected void validateHashCode2(byte[] param) {

            if ((param != null) && (param.length > 65)) {
                throw new java.lang.RuntimeException();
            }

            if ((param != null) && (param.length < 65)) {
                throw new java.lang.RuntimeException();
            }

        }

        /**
         * Auto generated setter method
         * @param param HashCode2
         */
        public void setHashCode2(byte[] param) {

            validateHashCode2(param);

            this.localHashCode2 = param;
        }

        /**
         * field for HashCode3
         * This was an Array!
         */

        protected byte[] localHashCode3;

        /**
         * Auto generated getter method
         * @return byte[]
         */
        public byte[] getHashCode3() {
            return localHashCode3;
        }

        /**
         * validate the array for HashCode3
         */
        protected void validateHashCode3(byte[] param) {

            if ((param != null) && (param.length > 65)) {
                throw new java.lang.RuntimeException();
            }

            if ((param != null) && (param.length < 65)) {
                throw new java.lang.RuntimeException();
            }

        }

        /**
         * Auto generated setter method
         * @param param HashCode3
         */
        public void setHashCode3(byte[] param) {

            validateHashCode3(param);

            this.localHashCode3 = param;
        }

        /**
         * field for HashCode4
         * This was an Array!
         */

        protected byte[] localHashCode4;

        /**
         * Auto generated getter method
         * @return byte[]
         */
        public byte[] getHashCode4() {
            return localHashCode4;
        }

        /**
         * validate the array for HashCode4
         */
        protected void validateHashCode4(byte[] param) {

            if ((param != null) && (param.length > 65)) {
                throw new java.lang.RuntimeException();
            }

            if ((param != null) && (param.length < 65)) {
                throw new java.lang.RuntimeException();
            }

        }

        /**
         * Auto generated setter method
         * @param param HashCode4
         */
        public void setHashCode4(byte[] param) {

            validateHashCode4(param);

            this.localHashCode4 = param;
        }

        /**
         * field for HashCode5
         * This was an Array!
         */

        protected byte[] localHashCode5;

        /**
         * Auto generated getter method
         * @return byte[]
         */
        public byte[] getHashCode5() {
            return localHashCode5;
        }

        /**
         * validate the array for HashCode5
         */
        protected void validateHashCode5(byte[] param) {

            if ((param != null) && (param.length > 65)) {
                throw new java.lang.RuntimeException();
            }

            if ((param != null) && (param.length < 65)) {
                throw new java.lang.RuntimeException();
            }

        }

        /**
         * Auto generated setter method
         * @param param HashCode5
         */
        public void setHashCode5(byte[] param) {

            validateHashCode5(param);

            this.localHashCode5 = param;
        }

        /**
         *
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(
                    this, parentQName) {

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {

                    java.lang.String prefix = parentQName.getPrefix();
                    java.lang.String namespace = parentQName.getNamespaceURI();

                    if (namespace != null) {
                        java.lang.String writerPrefix = xmlWriter
                                .getPrefix(namespace);
                        if (writerPrefix != null) {
                            xmlWriter.writeStartElement(namespace, parentQName
                                    .getLocalPart());
                        } else {
                            if (prefix == null) {
                                prefix = org.apache.axis2.databinding.utils.BeanUtil
                                        .getUniquePrefix();
                            }

                            xmlWriter.writeStartElement(prefix, parentQName
                                    .getLocalPart(), namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);
                        }
                    } else {
                        xmlWriter.writeStartElement(parentQName.getLocalPart());
                    }

                    if (localHashCode1 != null) {
                        for (int i = 0; i < localHashCode1.length; i++) {

                            namespace = "";
                            if (!namespace.equals("")) {
                                prefix = xmlWriter.getPrefix(namespace);

                                if (prefix == null) {
                                    prefix = org.apache.axis2.databinding.utils.BeanUtil
                                            .getUniquePrefix();

                                    xmlWriter.writeStartElement(prefix,
                                            "hashCode1", namespace);
                                    xmlWriter.writeNamespace(prefix, namespace);
                                    xmlWriter.setPrefix(prefix, namespace);

                                } else {
                                    xmlWriter.writeStartElement(namespace,
                                            "hashCode1");
                                }

                            } else {
                                xmlWriter.writeStartElement("hashCode1");
                            }
                            xmlWriter
                                    .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                            .convertToString(localHashCode1[i]));
                            xmlWriter.writeEndElement();

                        }
                    } else {

                        throw new RuntimeException("hashCode1 cannot be null!!");

                    }

                    if (localHashCode2 != null) {
                        for (int i = 0; i < localHashCode2.length; i++) {

                            namespace = "";
                            if (!namespace.equals("")) {
                                prefix = xmlWriter.getPrefix(namespace);

                                if (prefix == null) {
                                    prefix = org.apache.axis2.databinding.utils.BeanUtil
                                            .getUniquePrefix();

                                    xmlWriter.writeStartElement(prefix,
                                            "hashCode2", namespace);
                                    xmlWriter.writeNamespace(prefix, namespace);
                                    xmlWriter.setPrefix(prefix, namespace);

                                } else {
                                    xmlWriter.writeStartElement(namespace,
                                            "hashCode2");
                                }

                            } else {
                                xmlWriter.writeStartElement("hashCode2");
                            }
                            xmlWriter
                                    .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                            .convertToString(localHashCode2[i]));
                            xmlWriter.writeEndElement();

                        }
                    } else {

                        throw new RuntimeException("hashCode2 cannot be null!!");

                    }

                    if (localHashCode3 != null) {
                        for (int i = 0; i < localHashCode3.length; i++) {

                            namespace = "";
                            if (!namespace.equals("")) {
                                prefix = xmlWriter.getPrefix(namespace);

                                if (prefix == null) {
                                    prefix = org.apache.axis2.databinding.utils.BeanUtil
                                            .getUniquePrefix();

                                    xmlWriter.writeStartElement(prefix,
                                            "hashCode3", namespace);
                                    xmlWriter.writeNamespace(prefix, namespace);
                                    xmlWriter.setPrefix(prefix, namespace);

                                } else {
                                    xmlWriter.writeStartElement(namespace,
                                            "hashCode3");
                                }

                            } else {
                                xmlWriter.writeStartElement("hashCode3");
                            }
                            xmlWriter
                                    .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                            .convertToString(localHashCode3[i]));
                            xmlWriter.writeEndElement();

                        }
                    } else {

                        throw new RuntimeException("hashCode3 cannot be null!!");

                    }

                    if (localHashCode4 != null) {
                        for (int i = 0; i < localHashCode4.length; i++) {

                            namespace = "";
                            if (!namespace.equals("")) {
                                prefix = xmlWriter.getPrefix(namespace);

                                if (prefix == null) {
                                    prefix = org.apache.axis2.databinding.utils.BeanUtil
                                            .getUniquePrefix();

                                    xmlWriter.writeStartElement(prefix,
                                            "hashCode4", namespace);
                                    xmlWriter.writeNamespace(prefix, namespace);
                                    xmlWriter.setPrefix(prefix, namespace);

                                } else {
                                    xmlWriter.writeStartElement(namespace,
                                            "hashCode4");
                                }

                            } else {
                                xmlWriter.writeStartElement("hashCode4");
                            }
                            xmlWriter
                                    .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                            .convertToString(localHashCode4[i]));
                            xmlWriter.writeEndElement();

                        }
                    } else {

                        throw new RuntimeException("hashCode4 cannot be null!!");

                    }

                    if (localHashCode5 != null) {
                        for (int i = 0; i < localHashCode5.length; i++) {

                            namespace = "";
                            if (!namespace.equals("")) {
                                prefix = xmlWriter.getPrefix(namespace);

                                if (prefix == null) {
                                    prefix = org.apache.axis2.databinding.utils.BeanUtil
                                            .getUniquePrefix();

                                    xmlWriter.writeStartElement(prefix,
                                            "hashCode5", namespace);
                                    xmlWriter.writeNamespace(prefix, namespace);
                                    xmlWriter.setPrefix(prefix, namespace);

                                } else {
                                    xmlWriter.writeStartElement(namespace,
                                            "hashCode5");
                                }

                            } else {
                                xmlWriter.writeStartElement("hashCode5");
                            }
                            xmlWriter
                                    .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                            .convertToString(localHashCode5[i]));
                            xmlWriter.writeEndElement();

                        }
                    } else {

                        throw new RuntimeException("hashCode5 cannot be null!!");

                    }

                    xmlWriter.writeEndElement();

                }

                /**
                 * Util method to write an attribute with the ns prefix
                 */
                private void writeAttribute(java.lang.String prefix,
                        java.lang.String namespace, java.lang.String attName,
                        java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (xmlWriter.getPrefix(namespace) == null) {
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }

                    xmlWriter.writeAttribute(namespace, attName, attValue);

                }

                /**
                 * Util method to write an attribute without the ns prefix
                 */
                private void writeAttribute(java.lang.String namespace,
                        java.lang.String attName, java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (namespace.equals("")) {
                        xmlWriter.writeAttribute(attName, attValue);
                    } else {
                        registerPrefix(xmlWriter, namespace);
                        xmlWriter.writeAttribute(namespace, attName, attValue);
                    }
                }

                /**
                 * Register a namespace prefix
                 */
                private java.lang.String registerPrefix(
                        javax.xml.stream.XMLStreamWriter xmlWriter,
                        java.lang.String namespace)
                        throws javax.xml.stream.XMLStreamException {
                    java.lang.String prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = createPrefix();

                        while (xmlWriter.getNamespaceContext().getNamespaceURI(
                                prefix) != null) {
                            prefix = createPrefix();
                        }

                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);
                    }

                    return prefix;
                }

                /**
                 * Create a prefix
                 */
                private java.lang.String createPrefix() {
                    return "ns" + (int) Math.random();
                }
            };

            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    MY_QNAME, factory, dataSource);

        }

        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localHashCode1 != null) {
                for (int i = 0; i < localHashCode1.length; i++) {

                    elementList.add(new javax.xml.namespace.QName("",
                            "hashCode1"));
                    elementList
                            .add(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localHashCode1[i]));

                }
            } else {

                throw new RuntimeException("hashCode1 cannot be null!!");

            }

            if (localHashCode2 != null) {
                for (int i = 0; i < localHashCode2.length; i++) {

                    elementList.add(new javax.xml.namespace.QName("",
                            "hashCode2"));
                    elementList
                            .add(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localHashCode2[i]));

                }
            } else {

                throw new RuntimeException("hashCode2 cannot be null!!");

            }

            if (localHashCode3 != null) {
                for (int i = 0; i < localHashCode3.length; i++) {

                    elementList.add(new javax.xml.namespace.QName("",
                            "hashCode3"));
                    elementList
                            .add(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localHashCode3[i]));

                }
            } else {

                throw new RuntimeException("hashCode3 cannot be null!!");

            }

            if (localHashCode4 != null) {
                for (int i = 0; i < localHashCode4.length; i++) {

                    elementList.add(new javax.xml.namespace.QName("",
                            "hashCode4"));
                    elementList
                            .add(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localHashCode4[i]));

                }
            } else {

                throw new RuntimeException("hashCode4 cannot be null!!");

            }

            if (localHashCode5 != null) {
                for (int i = 0; i < localHashCode5.length; i++) {

                    elementList.add(new javax.xml.namespace.QName("",
                            "hashCode5"));
                    elementList
                            .add(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localHashCode5[i]));

                }
            } else {

                throw new RuntimeException("hashCode5 cannot be null!!");

            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(
                    qName, elementList.toArray(), attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             *                If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GuidRequest parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                GuidRequest object = new GuidRequest();
                int event;
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader
                            .getAttributeValue(
                                    "http://www.w3.org/2001/XMLSchema-instance",
                                    "type") != null) {
                        java.lang.String fullTypeName = reader
                                .getAttributeValue(
                                        "http://www.w3.org/2001/XMLSchema-instance",
                                        "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = fullTypeName.substring(
                                    0, fullTypeName.indexOf(":"));
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName
                                    .substring(fullTypeName.indexOf(":") + 1);
                            if (!"GuidRequest".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader
                                        .getNamespaceContext().getNamespaceURI(
                                                nsPrefix);
                                return (GuidRequest) ExtensionMapper
                                        .getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    boolean isReaderMTOMAware = false;

                    try {
                        isReaderMTOMAware = java.lang.Boolean.TRUE
                                .equals(reader
                                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
                    } catch (java.lang.IllegalArgumentException e) {
                        isReaderMTOMAware = false;
                    }

                    reader.next();

                    java.util.ArrayList list1 = new java.util.ArrayList();

                    java.util.ArrayList list2 = new java.util.ArrayList();

                    java.util.ArrayList list3 = new java.util.ArrayList();

                    java.util.ArrayList list4 = new java.util.ArrayList();

                    java.util.ArrayList list5 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "hashCode1")
                                    .equals(reader.getName())) {

                        // Process the array and step past its final element's end.
                        list1.add(reader.getElementText());

                        //loop until we find a start element that is not part of this array
                        boolean loopDone1 = false;
                        while (!loopDone1) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement()
                                    && !reader.isEndElement())
                                reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone1 = true;
                            } else {
                                if (new javax.xml.namespace.QName("",
                                        "hashCode1").equals(reader.getName())) {
                                    list1.add(reader.getElementText());

                                } else {
                                    loopDone1 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array
                        object
                                .setHashCode1((byte[]) org.apache.axis2.databinding.utils.ConverterUtil
                                        .convertToArray(byte.class, list1));

                    } // End of if for expected property start element

                    else {
                        // A start element we are not expecting indicates an invalid parameter was passed
                        throw new java.lang.RuntimeException(
                                "Unexpected subelement "
                                        + reader.getLocalName());
                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "hashCode2")
                                    .equals(reader.getName())) {

                        // Process the array and step past its final element's end.
                        list2.add(reader.getElementText());

                        //loop until we find a start element that is not part of this array
                        boolean loopDone2 = false;
                        while (!loopDone2) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement()
                                    && !reader.isEndElement())
                                reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone2 = true;
                            } else {
                                if (new javax.xml.namespace.QName("",
                                        "hashCode2").equals(reader.getName())) {
                                    list2.add(reader.getElementText());

                                } else {
                                    loopDone2 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array
                        object
                                .setHashCode2((byte[]) org.apache.axis2.databinding.utils.ConverterUtil
                                        .convertToArray(byte.class, list2));

                    } // End of if for expected property start element

                    else {
                        // A start element we are not expecting indicates an invalid parameter was passed
                        throw new java.lang.RuntimeException(
                                "Unexpected subelement "
                                        + reader.getLocalName());
                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "hashCode3")
                                    .equals(reader.getName())) {

                        // Process the array and step past its final element's end.
                        list3.add(reader.getElementText());

                        //loop until we find a start element that is not part of this array
                        boolean loopDone3 = false;
                        while (!loopDone3) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement()
                                    && !reader.isEndElement())
                                reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone3 = true;
                            } else {
                                if (new javax.xml.namespace.QName("",
                                        "hashCode3").equals(reader.getName())) {
                                    list3.add(reader.getElementText());

                                } else {
                                    loopDone3 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array
                        object
                                .setHashCode3((byte[]) org.apache.axis2.databinding.utils.ConverterUtil
                                        .convertToArray(byte.class, list3));

                    } // End of if for expected property start element

                    else {
                        // A start element we are not expecting indicates an invalid parameter was passed
                        throw new java.lang.RuntimeException(
                                "Unexpected subelement "
                                        + reader.getLocalName());
                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "hashCode4")
                                    .equals(reader.getName())) {

                        // Process the array and step past its final element's end.
                        list4.add(reader.getElementText());

                        //loop until we find a start element that is not part of this array
                        boolean loopDone4 = false;
                        while (!loopDone4) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement()
                                    && !reader.isEndElement())
                                reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone4 = true;
                            } else {
                                if (new javax.xml.namespace.QName("",
                                        "hashCode4").equals(reader.getName())) {
                                    list4.add(reader.getElementText());

                                } else {
                                    loopDone4 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array
                        object
                                .setHashCode4((byte[]) org.apache.axis2.databinding.utils.ConverterUtil
                                        .convertToArray(byte.class, list4));

                    } // End of if for expected property start element

                    else {
                        // A start element we are not expecting indicates an invalid parameter was passed
                        throw new java.lang.RuntimeException(
                                "Unexpected subelement "
                                        + reader.getLocalName());
                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "hashCode5")
                                    .equals(reader.getName())) {

                        // Process the array and step past its final element's end.
                        list5.add(reader.getElementText());

                        //loop until we find a start element that is not part of this array
                        boolean loopDone5 = false;
                        while (!loopDone5) {
                            // Ensure we are at the EndElement
                            while (!reader.isEndElement()) {
                                reader.next();
                            }
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement()
                                    && !reader.isEndElement())
                                reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone5 = true;
                            } else {
                                if (new javax.xml.namespace.QName("",
                                        "hashCode5").equals(reader.getName())) {
                                    list5.add(reader.getElementText());

                                } else {
                                    loopDone5 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array
                        object
                                .setHashCode5((byte[]) org.apache.axis2.databinding.utils.ConverterUtil
                                        .convertToArray(byte.class, list5));

                    } // End of if for expected property start element

                    else {
                        // A start element we are not expecting indicates an invalid parameter was passed
                        throw new java.lang.RuntimeException(
                                "Unexpected subelement "
                                        + reader.getLocalName());
                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();
                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new java.lang.RuntimeException(
                                "Unexpected subelement "
                                        + reader.getLocalName());

                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class GuidWSConfigResponse implements
            org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidWSConfigResponse",
                "ns1");

        /**
         * field for HashCode1MatchRule
         */

        protected HashCodeMatchRule localHashCode1MatchRule;

        /**
         * Auto generated getter method
         * @return HashCodeMatchRule
         */
        public HashCodeMatchRule getHashCode1MatchRule() {
            return localHashCode1MatchRule;
        }

        /**
         * Auto generated setter method
         * @param param HashCode1MatchRule
         */
        public void setHashCode1MatchRule(HashCodeMatchRule param) {

            this.localHashCode1MatchRule = param;

        }

        /**
         * field for HashCode2MatchRule
         */

        protected HashCodeMatchRule localHashCode2MatchRule;

        /**
         * Auto generated getter method
         * @return HashCodeMatchRule
         */
        public HashCodeMatchRule getHashCode2MatchRule() {
            return localHashCode2MatchRule;
        }

        /**
         * Auto generated setter method
         * @param param HashCode2MatchRule
         */
        public void setHashCode2MatchRule(HashCodeMatchRule param) {

            this.localHashCode2MatchRule = param;

        }

        /**
         * field for HashCode3MatchRule
         */

        protected HashCodeMatchRule localHashCode3MatchRule;

        /**
         * Auto generated getter method
         * @return HashCodeMatchRule
         */
        public HashCodeMatchRule getHashCode3MatchRule() {
            return localHashCode3MatchRule;
        }

        /**
         * Auto generated setter method
         * @param param HashCode3MatchRule
         */
        public void setHashCode3MatchRule(HashCodeMatchRule param) {

            this.localHashCode3MatchRule = param;

        }

        /**
         * field for HashCode4MatchRule
         */

        protected HashCodeMatchRule localHashCode4MatchRule;

        /**
         * Auto generated getter method
         * @return HashCodeMatchRule
         */
        public HashCodeMatchRule getHashCode4MatchRule() {
            return localHashCode4MatchRule;
        }

        /**
         * Auto generated setter method
         * @param param HashCode4MatchRule
         */
        public void setHashCode4MatchRule(HashCodeMatchRule param) {

            this.localHashCode4MatchRule = param;

        }

        /**
         * field for HashCode5MatchRule
         */

        protected HashCodeMatchRule localHashCode5MatchRule;

        /**
         * Auto generated getter method
         * @return HashCodeMatchRule
         */
        public HashCodeMatchRule getHashCode5MatchRule() {
            return localHashCode5MatchRule;
        }

        /**
         * Auto generated setter method
         * @param param HashCode5MatchRule
         */
        public void setHashCode5MatchRule(HashCodeMatchRule param) {

            this.localHashCode5MatchRule = param;

        }

        /**
         * field for SubjectMatchRule
         */

        protected SubjectMatchRule localSubjectMatchRule;

        /**
         * Auto generated getter method
         * @return SubjectMatchRule
         */
        public SubjectMatchRule getSubjectMatchRule() {
            return localSubjectMatchRule;
        }

        /**
         * Auto generated setter method
         * @param param SubjectMatchRule
         */
        public void setSubjectMatchRule(SubjectMatchRule param) {

            this.localSubjectMatchRule = param;

        }

        /**
         *
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(
                    this, parentQName) {

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {

                    java.lang.String prefix = parentQName.getPrefix();
                    java.lang.String namespace = parentQName.getNamespaceURI();

                    if (namespace != null) {
                        java.lang.String writerPrefix = xmlWriter
                                .getPrefix(namespace);
                        if (writerPrefix != null) {
                            xmlWriter.writeStartElement(namespace, parentQName
                                    .getLocalPart());
                        } else {
                            if (prefix == null) {
                                prefix = org.apache.axis2.databinding.utils.BeanUtil
                                        .getUniquePrefix();
                            }

                            xmlWriter.writeStartElement(prefix, parentQName
                                    .getLocalPart(), namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);
                        }
                    } else {
                        xmlWriter.writeStartElement(parentQName.getLocalPart());
                    }

                    if (localHashCode1MatchRule == null) {
                        throw new RuntimeException(
                                "hashCode1MatchRule cannot be null!!");
                    }
                    localHashCode1MatchRule.getOMElement(
                            new javax.xml.namespace.QName("",
                                    "hashCode1MatchRule"), factory).serialize(
                            xmlWriter);

                    if (localHashCode2MatchRule == null) {
                        throw new RuntimeException(
                                "hashCode2MatchRule cannot be null!!");
                    }
                    localHashCode2MatchRule.getOMElement(
                            new javax.xml.namespace.QName("",
                                    "hashCode2MatchRule"), factory).serialize(
                            xmlWriter);

                    if (localHashCode3MatchRule == null) {
                        throw new RuntimeException(
                                "hashCode3MatchRule cannot be null!!");
                    }
                    localHashCode3MatchRule.getOMElement(
                            new javax.xml.namespace.QName("",
                                    "hashCode3MatchRule"), factory).serialize(
                            xmlWriter);

                    if (localHashCode4MatchRule == null) {
                        throw new RuntimeException(
                                "hashCode4MatchRule cannot be null!!");
                    }
                    localHashCode4MatchRule.getOMElement(
                            new javax.xml.namespace.QName("",
                                    "hashCode4MatchRule"), factory).serialize(
                            xmlWriter);

                    if (localHashCode5MatchRule == null) {
                        throw new RuntimeException(
                                "hashCode5MatchRule cannot be null!!");
                    }
                    localHashCode5MatchRule.getOMElement(
                            new javax.xml.namespace.QName("",
                                    "hashCode5MatchRule"), factory).serialize(
                            xmlWriter);

                    if (localSubjectMatchRule == null) {
                        throw new RuntimeException(
                                "subjectMatchRule cannot be null!!");
                    }
                    localSubjectMatchRule.getOMElement(
                            new javax.xml.namespace.QName("",
                                    "subjectMatchRule"), factory).serialize(
                            xmlWriter);

                    xmlWriter.writeEndElement();

                }

                /**
                 * Util method to write an attribute with the ns prefix
                 */
                private void writeAttribute(java.lang.String prefix,
                        java.lang.String namespace, java.lang.String attName,
                        java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (xmlWriter.getPrefix(namespace) == null) {
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }

                    xmlWriter.writeAttribute(namespace, attName, attValue);

                }

                /**
                 * Util method to write an attribute without the ns prefix
                 */
                private void writeAttribute(java.lang.String namespace,
                        java.lang.String attName, java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (namespace.equals("")) {
                        xmlWriter.writeAttribute(attName, attValue);
                    } else {
                        registerPrefix(xmlWriter, namespace);
                        xmlWriter.writeAttribute(namespace, attName, attValue);
                    }
                }

                /**
                 * Register a namespace prefix
                 */
                private java.lang.String registerPrefix(
                        javax.xml.stream.XMLStreamWriter xmlWriter,
                        java.lang.String namespace)
                        throws javax.xml.stream.XMLStreamException {
                    java.lang.String prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = createPrefix();

                        while (xmlWriter.getNamespaceContext().getNamespaceURI(
                                prefix) != null) {
                            prefix = createPrefix();
                        }

                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);
                    }

                    return prefix;
                }

                /**
                 * Create a prefix
                 */
                private java.lang.String createPrefix() {
                    return "ns" + (int) Math.random();
                }
            };

            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    MY_QNAME, factory, dataSource);

        }

        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            elementList.add(new javax.xml.namespace.QName("",
                    "hashCode1MatchRule"));

            if (localHashCode1MatchRule == null) {
                throw new RuntimeException(
                        "hashCode1MatchRule cannot be null!!");
            }
            elementList.add(localHashCode1MatchRule);

            elementList.add(new javax.xml.namespace.QName("",
                    "hashCode2MatchRule"));

            if (localHashCode2MatchRule == null) {
                throw new RuntimeException(
                        "hashCode2MatchRule cannot be null!!");
            }
            elementList.add(localHashCode2MatchRule);

            elementList.add(new javax.xml.namespace.QName("",
                    "hashCode3MatchRule"));

            if (localHashCode3MatchRule == null) {
                throw new RuntimeException(
                        "hashCode3MatchRule cannot be null!!");
            }
            elementList.add(localHashCode3MatchRule);

            elementList.add(new javax.xml.namespace.QName("",
                    "hashCode4MatchRule"));

            if (localHashCode4MatchRule == null) {
                throw new RuntimeException(
                        "hashCode4MatchRule cannot be null!!");
            }
            elementList.add(localHashCode4MatchRule);

            elementList.add(new javax.xml.namespace.QName("",
                    "hashCode5MatchRule"));

            if (localHashCode5MatchRule == null) {
                throw new RuntimeException(
                        "hashCode5MatchRule cannot be null!!");
            }
            elementList.add(localHashCode5MatchRule);

            elementList.add(new javax.xml.namespace.QName("",
                    "subjectMatchRule"));

            if (localSubjectMatchRule == null) {
                throw new RuntimeException("subjectMatchRule cannot be null!!");
            }
            elementList.add(localSubjectMatchRule);

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(
                    qName, elementList.toArray(), attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             *                If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GuidWSConfigResponse parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                GuidWSConfigResponse object = new GuidWSConfigResponse();
                int event;
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader
                            .getAttributeValue(
                                    "http://www.w3.org/2001/XMLSchema-instance",
                                    "type") != null) {
                        java.lang.String fullTypeName = reader
                                .getAttributeValue(
                                        "http://www.w3.org/2001/XMLSchema-instance",
                                        "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = fullTypeName.substring(
                                    0, fullTypeName.indexOf(":"));
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName
                                    .substring(fullTypeName.indexOf(":") + 1);
                            if (!"GuidWSConfigResponse".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader
                                        .getNamespaceContext().getNamespaceURI(
                                                nsPrefix);
                                return (GuidWSConfigResponse) ExtensionMapper
                                        .getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    boolean isReaderMTOMAware = false;

                    try {
                        isReaderMTOMAware = java.lang.Boolean.TRUE
                                .equals(reader
                                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
                    } catch (java.lang.IllegalArgumentException e) {
                        isReaderMTOMAware = false;
                    }

                    reader.next();

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "hashCode1MatchRule").equals(reader
                                            .getName())) {

                                object
                                        .setHashCode1MatchRule(HashCodeMatchRule.Factory
                                                .parse(reader));

                                reader.next();

                            } // End of if for expected property start element

                            else

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "hashCode2MatchRule").equals(reader
                                            .getName())) {

                                object
                                        .setHashCode2MatchRule(HashCodeMatchRule.Factory
                                                .parse(reader));

                                reader.next();

                            } // End of if for expected property start element

                            else

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "hashCode3MatchRule").equals(reader
                                            .getName())) {

                                object
                                        .setHashCode3MatchRule(HashCodeMatchRule.Factory
                                                .parse(reader));

                                reader.next();

                            } // End of if for expected property start element

                            else

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "hashCode4MatchRule").equals(reader
                                            .getName())) {

                                object
                                        .setHashCode4MatchRule(HashCodeMatchRule.Factory
                                                .parse(reader));

                                reader.next();

                            } // End of if for expected property start element

                            else

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "hashCode5MatchRule").equals(reader
                                            .getName())) {

                                object
                                        .setHashCode5MatchRule(HashCodeMatchRule.Factory
                                                .parse(reader));

                                reader.next();

                            } // End of if for expected property start element

                            else

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName("",
                                            "subjectMatchRule").equals(reader
                                            .getName())) {

                                object
                                        .setSubjectMatchRule(SubjectMatchRule.Factory
                                                .parse(reader));

                                reader.next();

                            } // End of if for expected property start element

                            else {
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new java.lang.RuntimeException(
                                        "Unexpected subelement "
                                                + reader.getLocalName());
                            }

                        } else
                            reader.next();
                    } // end of while loop

                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class GuidFault implements
            org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://ndar.nih.gov/webservices/guid", "GuidFault", "ns1");

        /**
         * field for GuidFault
         */

        protected java.lang.String localGuidFault;

        /**
         * Auto generated getter method
         * @return java.lang.String
         */
        public java.lang.String getGuidFault() {
            return localGuidFault;
        }

        /**
         * Auto generated setter method
         * @param param GuidFault
         */
        public void setGuidFault(java.lang.String param) {

            this.localGuidFault = param;

        }

        /**
         *
         * @param parentQName
         * @param factory
         * @return org.apache.axiom.om.OMElement
         */
        public org.apache.axiom.om.OMElement getOMElement(
                final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(
                    this, parentQName) {

                public void serialize(javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {

                    //We can safely assume an element has only one type associated with it

                    java.lang.String namespace = "http://ndar.nih.gov/webservices/guid";
                    java.lang.String localName = "GuidFault";

                    if (!namespace.equals("")) {
                        java.lang.String prefix = xmlWriter
                                .getPrefix(namespace);

                        if (prefix == null) {
                            prefix = org.apache.axis2.databinding.utils.BeanUtil
                                    .getUniquePrefix();

                            xmlWriter.writeStartElement(prefix, localName,
                                    namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);

                        } else {
                            xmlWriter.writeStartElement(namespace, localName);
                        }

                    } else {
                        xmlWriter.writeStartElement(localName);
                    }

                    if (localGuidFault == null) {

                        throw new RuntimeException(
                                "testValue cannot be null !!");

                    } else {

                        xmlWriter
                                .writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                                        .convertToString(localGuidFault));

                    }

                    xmlWriter.writeEndElement();

                }

                /**
                 * Util method to write an attribute with the ns prefix
                 */
                private void writeAttribute(java.lang.String prefix,
                        java.lang.String namespace, java.lang.String attName,
                        java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (xmlWriter.getPrefix(namespace) == null) {
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }

                    xmlWriter.writeAttribute(namespace, attName, attValue);

                }

                /**
                 * Util method to write an attribute without the ns prefix
                 */
                private void writeAttribute(java.lang.String namespace,
                        java.lang.String attName, java.lang.String attValue,
                        javax.xml.stream.XMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    if (namespace.equals("")) {
                        xmlWriter.writeAttribute(attName, attValue);
                    } else {
                        registerPrefix(xmlWriter, namespace);
                        xmlWriter.writeAttribute(namespace, attName, attValue);
                    }
                }

                /**
                 * Register a namespace prefix
                 */
                private java.lang.String registerPrefix(
                        javax.xml.stream.XMLStreamWriter xmlWriter,
                        java.lang.String namespace)
                        throws javax.xml.stream.XMLStreamException {
                    java.lang.String prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = createPrefix();

                        while (xmlWriter.getNamespaceContext().getNamespaceURI(
                                prefix) != null) {
                            prefix = createPrefix();
                        }

                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);
                    }

                    return prefix;
                }

                /**
                 * Create a prefix
                 */
                private java.lang.String createPrefix() {
                    return "ns" + (int) Math.random();
                }
            };

            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
                    MY_QNAME, factory, dataSource);

        }

        /**
         * databinding method to get an XML representation of this object
         *
         */
        public javax.xml.stream.XMLStreamReader getPullParser(
                javax.xml.namespace.QName qName) {

            //We can safely assume an element has only one type associated with it
            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(
                    MY_QNAME,
                    new java.lang.Object[] {
                            org.apache.axis2.databinding.utils.reader.ADBXMLStreamReader.ELEMENT_TEXT,
                            org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(localGuidFault) }, null);

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
             * static method to create the object
             * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
             *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
             * Postcondition: If this object is an element, the reader is positioned at its end element
             *                If this object is a complex type, the reader is positioned at the end element of its outer element
             */
            public static GuidFault parse(
                    javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                GuidFault object = new GuidFault();
                int event;
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    boolean isReaderMTOMAware = false;

                    try {
                        isReaderMTOMAware = java.lang.Boolean.TRUE
                                .equals(reader
                                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
                    } catch (java.lang.IllegalArgumentException e) {
                        isReaderMTOMAware = false;
                    }

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName(
                                            "http://ndar.nih.gov/webservices/guid",
                                            "GuidFault").equals(reader
                                            .getName())) {

                                java.lang.String content = reader
                                        .getElementText();

                                object
                                        .setGuidFault(org.apache.axis2.databinding.utils.ConverterUtil
                                                .convertToString(content));

                            } // End of if for expected property start element

                            else {
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new java.lang.RuntimeException(
                                        "Unexpected subelement "
                                                + reader.getLocalName());
                            }

                        } else
                            reader.next();
                    } // end of while loop

                } catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    private org.apache.axiom.om.OMElement toOM(
            gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidWSConfigResponse param,
            boolean optimizeContent) {

        return param
                .getOMElement(
                        gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidWSConfigResponse.MY_QNAME,
                        org.apache.axiom.om.OMAbstractFactory.getOMFactory());

    }

    private org.apache.axiom.om.OMElement toOM(
            gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidFault param,
            boolean optimizeContent) {

        return param
                .getOMElement(
                        gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidFault.MY_QNAME,
                        org.apache.axiom.om.OMAbstractFactory.getOMFactory());

    }

    private org.apache.axiom.om.OMElement toOM(
            gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidRequest param,
            boolean optimizeContent) {

        return param
                .getOMElement(
                        gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidRequest.MY_QNAME,
                        org.apache.axiom.om.OMAbstractFactory.getOMFactory());

    }

    private org.apache.axiom.om.OMElement toOM(
            gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidResponse param,
            boolean optimizeContent) {

        return param
                .getOMElement(
                        gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidResponse.MY_QNAME,
                        org.apache.axiom.om.OMAbstractFactory.getOMFactory());

    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory,
            gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidRequest param,
            boolean optimizeContent) {
        org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory
                .getDefaultEnvelope();

        emptyEnvelope
                .getBody()
                .addChild(
                        param
                                .getOMElement(
                                        gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidRequest.MY_QNAME,
                                        factory));

        return emptyEnvelope;
    }

    /**
     *  get the default envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
            org.apache.axiom.soap.SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private java.lang.Object fromOM(org.apache.axiom.om.OMElement param,
            java.lang.Class type, java.util.Map extraNamespaces) {

        try {

            if (gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidWSConfigResponse.class
                    .equals(type)) {

                return gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidWSConfigResponse.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidFault.class
                    .equals(type)) {

                return gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidFault.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidRequest.class
                    .equals(type)) {

                return gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidRequest.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidResponse.class
                    .equals(type)) {

                return gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidResponse.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

            if (gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidFault.class
                    .equals(type)) {

                return gov.nih.ndar.webservices.guid.client.GuidWSStub.GuidFault.Factory
                        .parse(param.getXMLStreamReaderWithoutCaching());

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void setOpNameArray() {
        opNameArray = null;
    }

}
