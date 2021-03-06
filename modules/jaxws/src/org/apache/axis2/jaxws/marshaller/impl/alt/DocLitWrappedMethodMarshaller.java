/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis2.jaxws.marshaller.impl.alt;

import org.apache.axis2.jaxws.ExceptionFactory;
import org.apache.axis2.jaxws.description.EndpointDescription;
import org.apache.axis2.jaxws.description.EndpointInterfaceDescription;
import org.apache.axis2.jaxws.description.OperationDescription;
import org.apache.axis2.jaxws.description.ParameterDescription;
import org.apache.axis2.jaxws.i18n.Messages;
import org.apache.axis2.jaxws.marshaller.MethodMarshaller;
import org.apache.axis2.jaxws.message.Block;
import org.apache.axis2.jaxws.message.Message;
import org.apache.axis2.jaxws.message.Protocol;
import org.apache.axis2.jaxws.message.databinding.JAXBBlockContext;
import org.apache.axis2.jaxws.message.factory.JAXBBlockFactory;
import org.apache.axis2.jaxws.message.factory.MessageFactory;
import org.apache.axis2.jaxws.registry.FactoryRegistry;
import org.apache.axis2.jaxws.runtime.description.marshal.MarshalServiceRuntimeDescription;
import org.apache.axis2.jaxws.utility.ConvertUtils;
import org.apache.axis2.jaxws.wrapper.JAXBWrapperTool;
import org.apache.axis2.jaxws.wrapper.impl.JAXBWrapperToolImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jws.WebParam.Mode;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class DocLitWrappedMethodMarshaller implements MethodMarshaller {

    private static Log log = LogFactory.getLog(DocLitWrappedMethodMarshaller.class);


    public DocLitWrappedMethodMarshaller() {
        super();
    }

    public Object demarshalResponse(Message message, Object[] signatureArgs,
                                    OperationDescription operationDesc)
            throws WebServiceException {
        // Note all exceptions are caught and rethrown with a WebServiceException

        EndpointInterfaceDescription ed = operationDesc.getEndpointInterfaceDescription();
        EndpointDescription endpointDesc = ed.getEndpointDescription();

        try {
            // Sample Document message
            // ..
            // <soapenv:body>
            //    <m:operationResponse ... >
            //       <param>hello</param>
            //    </m:operationResponse>
            // </soapenv:body>
            //
            // Important points.
            //   1) There is no operation element in the message
            //   2) The data blocks are located underneath the body element. 
            //   3) The name of the data block (m:operationResponse) is defined by the schema.
            //      It matches the operation name + "Response", and it has a corresponding JAXB element.
            //      This element is called the wrapper element
            //   4) The parameters are (param) are child elements of the wrapper element.
            ParameterDescription[] pds = operationDesc.getParameterDescriptions();
            MarshalServiceRuntimeDescription marshalDesc =
                    MethodMarshallerUtils.getMarshalDesc(endpointDesc);
            TreeSet<String> packages = marshalDesc.getPackages();
            String packagesKey = marshalDesc.getPackagesKey();

            // Determine if a returnValue is expected.
            // The return value may be an child element
            // The wrapper element 
            // or null
            Object returnValue = null;
            Class returnType = operationDesc.getResultActualType();
            boolean isChildReturn = !operationDesc.isJAXWSAsyncClientMethod() &&
                    (operationDesc.getResultPartName() != null);
            boolean isNoReturn = (returnType == void.class);

            // In usage=WRAPPED, there will be a single JAXB block inside the body.
            // Get this block
            JAXBBlockContext blockContext = new JAXBBlockContext(packages, packagesKey);
            JAXBBlockFactory factory =
                    (JAXBBlockFactory)FactoryRegistry.getFactory(JAXBBlockFactory.class);
            Block block = message.getBodyBlock(blockContext, factory);
            Object wrapperObject = block.getBusinessObject(true);

            // The child elements are within the object that 
            // represents the type
            if (wrapperObject instanceof JAXBElement) {
                wrapperObject = ((JAXBElement)wrapperObject).getValue();
            }

            // Use the wrapper tool to get the child objects.
            JAXBWrapperTool wrapperTool = new JAXBWrapperToolImpl();

            // Get the list of names for the output parameters
            List<String> names = new ArrayList<String>();
            List<ParameterDescription> pdList = new ArrayList<ParameterDescription>();
            for (int i = 0; i < pds.length; i++) {
                ParameterDescription pd = pds[i];
                if (pd.getMode() == Mode.OUT ||
                        pd.getMode() == Mode.INOUT) {
                    names.add(pd.getParameterName());
                    pdList.add(pd);
                }
            }

            // The return name is added as the last name
            if (isChildReturn && !isNoReturn) {
                names.add(operationDesc.getResultPartName());
            }

            // Get the child objects
            Object[] objects = wrapperTool.unWrap(wrapperObject, names,
                                                  marshalDesc.getPropertyDescriptorMap(
                                                          wrapperObject.getClass()));

            // Now create a list of paramValues so that we can populate the signature
            List<PDElement> pvList = new ArrayList<PDElement>();
            for (int i = 0; i < pdList.size(); i++) {
                ParameterDescription pd = pdList.get(i);
                Object value = objects[i];
                // The object in the PDElement must be an element
                Element element = null;
                QName qName = new QName(pd.getTargetNamespace(), pd.getPartName());
                if (!marshalDesc.getAnnotationDesc(pd.getParameterActualType()).hasXmlRootElement())
                {
                    element = new Element(value, qName,
                                          pd.getParameterActualType());

                } else {
                    element = new Element(value, qName);
                }
                pvList.add(new PDElement(pd, element, null));
            }

            // Populate the response Holders in the signature
            MethodMarshallerUtils.updateResponseSignatureArgs(pds, pvList, signatureArgs);

            // Now get the return value
            if (isNoReturn) {
                returnValue = null;
            } else if (isChildReturn) {
                returnValue = objects[objects.length - 1];
                // returnValue may be incompatible with JAX-WS signature
                if (ConvertUtils.isConvertable(returnValue, returnType)) {
                    returnValue = ConvertUtils.convert(returnValue, returnType);
                } else {
                    String objectClass =
                            (returnValue == null) ? "null" : returnValue.getClass().getName();
                    throw ExceptionFactory.makeWebServiceException(
                            Messages.getMessage("convertProblem", objectClass,
                                                returnType.getName()));
                }
            } else {
                returnValue = wrapperObject;
            }

            return returnValue;
        } catch (Exception e) {
            throw ExceptionFactory.makeWebServiceException(e);
        }
    }

    public Object[] demarshalRequest(Message message, OperationDescription operationDesc)
            throws WebServiceException {

        EndpointInterfaceDescription ed = operationDesc.getEndpointInterfaceDescription();
        EndpointDescription endpointDesc = ed.getEndpointDescription();

        // Note all exceptions are caught and rethrown with a WebServiceException
        try {
            // Sample Document message
            // ..
            // <soapenv:body>
            //    <m:operation>
            //      <param>hello</param>
            //    </m:operation>
            // </soapenv:body>
            //
            // Important points.
            //   1) There is no operation element under the body.
            //   2) The data blocks are located underneath the body.  
            //   3) The name of the data block (m:operation) is defined by the schema and match the name of the operation.
            //      This is called the wrapper element.  The wrapper element has a corresponding JAXB element pojo.
            //   4) The parameters (m:param) are child elements of the wrapper element.
            ParameterDescription[] pds = operationDesc.getParameterDescriptions();
            MarshalServiceRuntimeDescription marshalDesc =
                    MethodMarshallerUtils.getMarshalDesc(endpointDesc);
            TreeSet<String> packages = marshalDesc.getPackages();
            String packagesKey = marshalDesc.getPackagesKey();

            // In usage=WRAPPED, there will be a single JAXB block inside the body.
            // Get this block
            JAXBBlockContext blockContext = new JAXBBlockContext(packages, packagesKey);
            JAXBBlockFactory factory =
                    (JAXBBlockFactory)FactoryRegistry.getFactory(JAXBBlockFactory.class);
            Block block = message.getBodyBlock(blockContext, factory);
            Object wrapperObject = block.getBusinessObject(true);

            // The child elements are within the object that 
            // represents the type
            if (wrapperObject instanceof JAXBElement) {
                wrapperObject = ((JAXBElement)wrapperObject).getValue();
            }

            // Use the wrapper tool to get the child objects.
            JAXBWrapperTool wrapperTool = new JAXBWrapperToolImpl();

            // Get the list of names for the input parameters
            List<String> names = new ArrayList<String>();
            List<ParameterDescription> pdList = new ArrayList<ParameterDescription>();
            for (int i = 0; i < pds.length; i++) {
                ParameterDescription pd = pds[i];
                if (pd.getMode() == Mode.IN ||
                        pd.getMode() == Mode.INOUT) {
                    names.add(pd.getParameterName());
                    pdList.add(pd);
                }

            }

            // Get the child objects
            Object[] objects = wrapperTool.unWrap(wrapperObject, names,
                                                  marshalDesc.getPropertyDescriptorMap(
                                                          wrapperObject.getClass()));

            // Now create a list of paramValues 
            List<PDElement> pvList = new ArrayList<PDElement>();
            for (int i = 0; i < pdList.size(); i++) {
                ParameterDescription pd = pdList.get(i);
                Object value = objects[i];
                // The object in the PDElement must be an element
                Element element = null;
                QName qName = new QName(pd.getTargetNamespace(),
                                        pd.getPartName());
                if (!marshalDesc.getAnnotationDesc(pd.getParameterActualType()).hasXmlRootElement())
                {
                    element = new Element(value, qName, pd.getParameterActualType());
                } else {
                    element = new Element(value, qName);
                }
                pvList.add(new PDElement(pd, element, null));
            }

            // Build the signature arguments
            Object[] sigArguments = MethodMarshallerUtils.createRequestSignatureArgs(pds, pvList);

            return sigArguments;
        } catch (Exception e) {
            throw ExceptionFactory.makeWebServiceException(e);
        }
    }

    public Message marshalResponse(Object returnObject, Object[] signatureArgs,
                                   OperationDescription operationDesc, Protocol protocol)
            throws WebServiceException {

        EndpointInterfaceDescription ed = operationDesc.getEndpointInterfaceDescription();
        EndpointDescription endpointDesc = ed.getEndpointDescription();
        MarshalServiceRuntimeDescription marshalDesc =
                MethodMarshallerUtils.getMarshalDesc(endpointDesc);
        TreeSet<String> packages = marshalDesc.getPackages();
        String packagesKey = marshalDesc.getPackagesKey();

        // We want to respond with the same protocol as the request,
        // It the protocol is null, then use the Protocol defined by the binding
        if (protocol == null) {
            protocol = Protocol.getProtocolForBinding(endpointDesc.getBindingType());
        }

        // Note all exceptions are caught and rethrown with a WebServiceException
        try {
            // Sample Document message
            // ..
            // <soapenv:body>
            //    <m:operationResponse ... >
            //       <param>hello</param>
            //    </m:operationResponse>
            // </soapenv:body>
            //
            // Important points.
            //   1) There is no operation element in the message
            //   2) The data blocks are located underneath the body element. 
            //   3) The name of the data block (m:operationResponse) is defined by the schema.
            //      It matches the operation name + "Response", and it has a corresponding JAXB element.
            //      This element is called the wrapper element
            //   4) The parameters are (param) are child elements of the wrapper element.

            // Get the operation information
            ParameterDescription[] pds = operationDesc.getParameterDescriptions();

            // Create the message 
            MessageFactory mf = marshalDesc.getMessageFactory();
            Message m = mf.create(protocol);

            // In usage=WRAPPED, there will be a single block in the body.
            // The signatureArguments represent the child elements of that block
            // The first step is to convert the signature arguments into a list
            // of parameter values
            List<PDElement> pdeList =
                    MethodMarshallerUtils.getPDElements(marshalDesc,
                                                        pds,
                                                        signatureArgs,
                                                        false,  // output
                                                        true, false);

            // Now we want to create a single JAXB element that contains the 
            // ParameterValues.  We will use the wrapper tool to do this.
            // Create the inputs to the wrapper tool
            ArrayList<String> nameList = new ArrayList<String>();
            Map<String, Object> objectList = new HashMap<String, Object>();

            for (PDElement pde : pdeList) {
                String name = pde.getParam().getParameterName();

                // The object list contains type rendered objects
                Object value = pde.getElement().getTypeValue();
                nameList.add(name);
                objectList.put(name, value);
            }

            // Add the return object to the nameList and objectList
            Class returnType = operationDesc.getResultActualType();
            if (returnType != void.class) {
                String name = operationDesc.getResultName();
                nameList.add(name);
                objectList.put(name, returnObject);
            }

            // Now create the single JAXB element
            String wrapperName = marshalDesc.getResponseWrapperClassName(operationDesc);
            Class cls = MethodMarshallerUtils.loadClass(wrapperName);
            JAXBWrapperTool wrapperTool = new JAXBWrapperToolImpl();
            Object object = wrapperTool.wrap(cls, nameList, objectList,
                                             marshalDesc.getPropertyDescriptorMap(cls));

            QName wrapperQName = new QName(operationDesc.getResponseWrapperTargetNamespace(),
                                           operationDesc.getResponseWrapperLocalName());

            // Make sure object can be rendered as an element
            if (!marshalDesc.getAnnotationDesc(cls).hasXmlRootElement()) {
                object = new JAXBElement(wrapperQName, cls, object);
            }

            // Put the object into the message
            JAXBBlockFactory factory =
                    (JAXBBlockFactory)FactoryRegistry.getFactory(JAXBBlockFactory.class);

            Block block = factory.createFrom(object,
                                             new JAXBBlockContext(packages, packagesKey),
                                             wrapperQName);
            m.setBodyBlock(block);

            return m;
        } catch (Exception e) {
            throw ExceptionFactory.makeWebServiceException(e);
        }
    }

    public Message marshalRequest(Object[] signatureArguments, OperationDescription operationDesc)
            throws WebServiceException {

        EndpointInterfaceDescription ed = operationDesc.getEndpointInterfaceDescription();
        EndpointDescription endpointDesc = ed.getEndpointDescription();
        Protocol protocol = Protocol.getProtocolForBinding(endpointDesc.getClientBindingID());
        MarshalServiceRuntimeDescription marshalDesc =
                MethodMarshallerUtils.getMarshalDesc(endpointDesc);
        TreeSet<String> packages = marshalDesc.getPackages();
        String packagesKey = marshalDesc.getPackagesKey();

        // Note all exceptions are caught and rethrown with a WebServiceException
        try {
            // Sample Document message
            // ..
            // <soapenv:body>
            //    <m:operation>
            //      <param>hello</param>
            //    </m:operation>
            // </soapenv:body>
            //
            // Important points.
            //   1) There is no operation element under the body.
            //   2) The data blocks are located underneath the body.  
            //   3) The name of the data block (m:operation) is defined by the schema and match the name of the operation.
            //      This is called the wrapper element.  The wrapper element has a corresponding JAXB element pojo.
            //   4) The parameters (m:param) are child elements of the wrapper element.

            // Get the operation information
            ParameterDescription[] pds = operationDesc.getParameterDescriptions();

            // Create the message 
            MessageFactory mf = marshalDesc.getMessageFactory();
            Message m = mf.create(protocol);

            // In usage=WRAPPED, there will be a single block in the body.
            // The signatureArguments represent the child elements of that block
            // The first step is to convert the signature arguments into list
            // of parameter values
            List<PDElement> pvList = MethodMarshallerUtils.getPDElements(marshalDesc,
                                                                         pds,
                                                                         signatureArguments,
                                                                         true,   // input
                                                                         true, false);

            // Now we want to create a single JAXB element that contains the 
            // ParameterValues.  We will use the wrapper tool to do this.
            // Create the inputs to the wrapper tool
            ArrayList<String> nameList = new ArrayList<String>();
            Map<String, Object> objectList = new HashMap<String, Object>();

            for (PDElement pv : pvList) {
                String name = pv.getParam().getParameterName();

                // The object list contains type rendered objects
                Object value = pv.getElement().getTypeValue();
                nameList.add(name);
                objectList.put(name, value);
            }

            // Now create the single JAXB element 
            String wrapperName = marshalDesc.getRequestWrapperClassName(operationDesc);
            Class cls = MethodMarshallerUtils.loadClass(wrapperName);
            JAXBWrapperTool wrapperTool = new JAXBWrapperToolImpl();
            Object object = wrapperTool.wrap(cls, nameList, objectList,
                                             marshalDesc.getPropertyDescriptorMap(cls));

            QName wrapperQName = new QName(operationDesc.getRequestWrapperTargetNamespace(),
                                           operationDesc.getRequestWrapperLocalName());

            // Make sure object can be rendered as an element
            if (!marshalDesc.getAnnotationDesc(cls).hasXmlRootElement()) {
                object = new JAXBElement(wrapperQName, cls, object);
            }

            // Put the object into the message
            JAXBBlockFactory factory =
                    (JAXBBlockFactory)FactoryRegistry.getFactory(JAXBBlockFactory.class);

            Block block = factory.createFrom(object,
                                             new JAXBBlockContext(packages, packagesKey),
                                             wrapperQName);
            m.setBodyBlock(block);

            return m;
        } catch (Exception e) {
            throw ExceptionFactory.makeWebServiceException(e);
        }
    }

    public Message marshalFaultResponse(Throwable throwable, OperationDescription operationDesc,
                                        Protocol protocol) throws WebServiceException {

        EndpointInterfaceDescription ed = operationDesc.getEndpointInterfaceDescription();
        EndpointDescription endpointDesc = ed.getEndpointDescription();
        MarshalServiceRuntimeDescription marshalDesc =
                MethodMarshallerUtils.getMarshalDesc(endpointDesc);
        TreeSet<String> packages = marshalDesc.getPackages();

        // We want to respond with the same protocol as the request,
        // It the protocol is null, then use the Protocol defined by the binding
        if (protocol == null) {
            protocol = Protocol.getProtocolForBinding(endpointDesc.getBindingType());
        }

        // Note all exceptions are caught and rethrown with a WebServiceException
        try {
            // Create the message 
            MessageFactory mf = (MessageFactory)FactoryRegistry.getFactory(MessageFactory.class);
            Message m = mf.create(protocol);

            // Put the fault onto the message
            MethodMarshallerUtils.marshalFaultResponse(throwable,
                                                       marshalDesc,
                                                       operationDesc,
                                                       m);
            return m;
        } catch (Exception e) {
            throw ExceptionFactory.makeWebServiceException(e);
        }
    }

    public Throwable demarshalFaultResponse(Message message, OperationDescription operationDesc)
            throws WebServiceException {

        EndpointInterfaceDescription ed = operationDesc.getEndpointInterfaceDescription();
        EndpointDescription endpointDesc = ed.getEndpointDescription();
        MarshalServiceRuntimeDescription marshalDesc =
                MethodMarshallerUtils.getMarshalDesc(endpointDesc);

        // Note all exceptions are caught and rethrown with a WebServiceException
        try {
            Throwable t = MethodMarshallerUtils.demarshalFaultResponse(operationDesc,
                                                                       marshalDesc,
                                                                       message);
            return t;
        } catch (Exception e) {
            throw ExceptionFactory.makeWebServiceException(e);
        }
    }

}
