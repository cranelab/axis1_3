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
package org.apache.axis2.schema.populate.derived;

import org.apache.axis2.databinding.types.PositiveInteger;
import org.apache.axis2.databinding.utils.ConverterUtil;

public class DerivedTypePositveIntegerPopulateTest extends AbstractDerivedPopulater{

     private String values[]= {
            "18444",
            "1",
            "5636332" ,
            "0" ,
            "-3453434"

    };

    private String xmlString[] = {
            "<DerivedPositiveInteger xmlns=\"http://soapinterop.org/xsd\">"+values[0]+"</DerivedPositiveInteger>",
            "<DerivedPositiveInteger xmlns=\"http://soapinterop.org/xsd\">"+values[1]+"</DerivedPositiveInteger>",
            "<DerivedPositiveInteger xmlns=\"http://soapinterop.org/xsd\">"+values[2]+"</DerivedPositiveInteger>",
            "<DerivedPositiveInteger xmlns=\"http://soapinterop.org/xsd\">"+values[3]+"</DerivedPositiveInteger>",
            "<DerivedPositiveInteger xmlns=\"http://soapinterop.org/xsd\">"+values[4]+"</DerivedPositiveInteger>"
    };




    protected void setUp() throws Exception {
        className = "org.soapinterop.xsd.DerivedPositiveInteger";
        propertyClass = PositiveInteger.class;
    }

    // force others to implement this method
    public void testPopulate() throws Exception {

        for (int i = 0; i < 3; i++) {
            checkValue(xmlString[i],values[i]);
        }
        for (int i = 3; i < values.length; i++) {
            try {
                checkValue(xmlString[i],values[i]);
                fail();
            } catch (Exception e) {

            }
        }
    }

    protected String convertToString(Object o) {
        return ConverterUtil.convertToString((PositiveInteger)o);
    }
}
