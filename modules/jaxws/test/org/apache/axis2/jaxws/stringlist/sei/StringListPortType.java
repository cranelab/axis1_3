
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
package org.apache.axis2.jaxws.stringlist.sei;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.bind.annotation.XmlList;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0_01-b15-fcs
 * Generated source version: 2.0
 * 
 */
@WebService(name = "StringListPortType", targetNamespace = "http://org/test/StringList")
@SOAPBinding(parameterStyle = ParameterStyle.BARE)
public interface StringListPortType {


    /**
     * 
     * @param parameters
     * @return
     *     returns java.lang.String[]
     */
    @XmlList
    @WebMethod(operationName = "StringList")
    @WebResult(name = "StringListResponse", targetNamespace = "http://org/test/StringList", partName = "result")
    public String[] stringList(
        @XmlList
        @WebParam(name = "StringList", targetNamespace = "http://org/test/StringList", partName = "parameters")
        String[] parameters);

}
