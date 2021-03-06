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
package org.apache.axis2.schema;

import org.apache.axis2.namespace.Constants;

import javax.xml.namespace.QName;

/**
 * Constants for the QNames of standard schema types
 */
public class SchemaConstants {


    public static final String URI_DEFAULT_SCHEMA_XSD = Constants.URI_2001_SCHEMA_XSD;
    public static final QName XSD_STRING = new QName(URI_DEFAULT_SCHEMA_XSD, "string");
    public static final QName XSD_BOOLEAN = new QName(URI_DEFAULT_SCHEMA_XSD, "boolean");
    public static final QName XSD_DOUBLE = new QName(URI_DEFAULT_SCHEMA_XSD, "double");
    public static final QName XSD_FLOAT = new QName(URI_DEFAULT_SCHEMA_XSD, "float");
    public static final QName XSD_INT = new QName(URI_DEFAULT_SCHEMA_XSD, "int");
    public static final QName XSD_INTEGER = new QName(URI_DEFAULT_SCHEMA_XSD, "integer");
    public static final QName XSD_LONG = new QName(URI_DEFAULT_SCHEMA_XSD, "long");
    public static final QName XSD_SHORT = new QName(URI_DEFAULT_SCHEMA_XSD, "short");
    public static final QName XSD_BYTE = new QName(URI_DEFAULT_SCHEMA_XSD, "byte");
    public static final QName XSD_DECIMAL = new QName(URI_DEFAULT_SCHEMA_XSD, "decimal");
    public static final QName XSD_BASE64 = new QName(URI_DEFAULT_SCHEMA_XSD, "base64Binary");
    public static final QName XSD_HEXBIN = new QName(URI_DEFAULT_SCHEMA_XSD, "hexBinary");
    public static final QName XSD_ANYSIMPLETYPE = new QName(URI_DEFAULT_SCHEMA_XSD, "anySimpleType");
    public static final QName XSD_ANYTYPE = new QName(URI_DEFAULT_SCHEMA_XSD, "anyType");
    public static final QName XSD_ANY = new QName(URI_DEFAULT_SCHEMA_XSD, "any");
    public static final QName XSD_QNAME = new QName(URI_DEFAULT_SCHEMA_XSD, "QName");
    public static final QName XSD_DATETIME = new QName(URI_DEFAULT_SCHEMA_XSD, "dateTime");
    public static final QName XSD_DATE = new QName(URI_DEFAULT_SCHEMA_XSD, "date");
    public static final QName XSD_TIME = new QName(URI_DEFAULT_SCHEMA_XSD, "time");


    public static final QName XSD_UNSIGNEDLONG = new QName(URI_DEFAULT_SCHEMA_XSD, "unsignedLong");
    public static final QName XSD_UNSIGNEDINT = new QName(URI_DEFAULT_SCHEMA_XSD, "unsignedInt");
    public static final QName XSD_UNSIGNEDSHORT = new QName(URI_DEFAULT_SCHEMA_XSD, "unsignedShort");
    public static final QName XSD_UNSIGNEDBYTE = new QName(URI_DEFAULT_SCHEMA_XSD, "unsignedByte");
    public static final QName XSD_POSITIVEINTEGER = new QName(URI_DEFAULT_SCHEMA_XSD, "positiveInteger");
    public static final QName XSD_NEGATIVEINTEGER = new QName(URI_DEFAULT_SCHEMA_XSD, "negativeInteger");
    public static final QName XSD_NONNEGATIVEINTEGER = new QName(URI_DEFAULT_SCHEMA_XSD, "nonNegativeInteger");
    public static final QName XSD_NONPOSITIVEINTEGER = new QName(URI_DEFAULT_SCHEMA_XSD, "nonPositiveInteger");

    public static final QName XSD_YEARMONTH = new QName(URI_DEFAULT_SCHEMA_XSD, "gYearMonth");
    public static final QName XSD_MONTHDAY = new QName(URI_DEFAULT_SCHEMA_XSD, "gMonthDay");
    public static final QName XSD_YEAR = new QName(URI_DEFAULT_SCHEMA_XSD, "gYear");
    public static final QName XSD_MONTH = new QName(URI_DEFAULT_SCHEMA_XSD, "gMonth");
    public static final QName XSD_DAY = new QName(URI_DEFAULT_SCHEMA_XSD, "gDay");
    public static final QName XSD_DURATION = new QName(URI_DEFAULT_SCHEMA_XSD, "duration");

    public static final QName XSD_NAME = new QName(URI_DEFAULT_SCHEMA_XSD, "Name");
    public static final QName XSD_NCNAME = new QName(URI_DEFAULT_SCHEMA_XSD, "NCName");
    public static final QName XSD_NMTOKEN = new QName(URI_DEFAULT_SCHEMA_XSD, "NMTOKEN");
    public static final QName XSD_NMTOKENS = new QName(URI_DEFAULT_SCHEMA_XSD, "NMTOKENS");
    public static final QName XSD_NOTATION = new QName(URI_DEFAULT_SCHEMA_XSD, "NOTATION");
    public static final QName XSD_ENTITY = new QName(URI_DEFAULT_SCHEMA_XSD, "ENTITY");
    public static final QName XSD_ENTITIES = new QName(URI_DEFAULT_SCHEMA_XSD, "ENTITIES");
    public static final QName XSD_IDREF = new QName(URI_DEFAULT_SCHEMA_XSD, "IDREF");
    public static final QName XSD_IDREFS = new QName(URI_DEFAULT_SCHEMA_XSD, "IDREFS");
    public static final QName XSD_ANYURI = new QName(URI_DEFAULT_SCHEMA_XSD, "anyURI");
    public static final QName XSD_LANGUAGE = new QName(URI_DEFAULT_SCHEMA_XSD, "language");
    public static final QName XSD_ID = new QName(URI_DEFAULT_SCHEMA_XSD, "ID");
    public static final QName XSD_SCHEMA = new QName(URI_DEFAULT_SCHEMA_XSD, "schema");

    public static final QName XSD_NORMALIZEDSTRING = new QName(URI_DEFAULT_SCHEMA_XSD, "normalizedString");
    public static final QName XSD_TOKEN = new QName(URI_DEFAULT_SCHEMA_XSD, "token");

    //bit patterns for the types
    public static final int ATTRIBUTE_TYPE = 0x0001;
    public static final int ELEMENT_TYPE = 0x0002;
    public static final int ARRAY_TYPE = 0x0004;
    public static final int ANY_TYPE = 0x0008;
    public static final int BINARY_TYPE = 0x0010;
    public static final int OPTIONAL_TYPE = 0x0020;
    public static final int SIMPLE_TYPE_OR_CONTENT = 0x0040;
    public static final int INNER_CHOICE_ELEMENT = 0x0080;
    public static final int PARTICLE_TYPE_ELEMENT = 0x0100;

    public static class SchemaPropertyNames{

        public static final String SCHEMA_COMPILER_PROPERTIES = "/org/apache/axis2/schema/schema-compile.properties";
        public static final String BEAN_WRITER_KEY = "schema.bean.writer.class";
        public static final String BEAN_WRITER_TEMPLATE_KEY = "schema.bean.writer.template";
        public static final String BEAN_WRITER_TYPEMAP_KEY = "schema.bean.typemap";
    }

     public static class SchemaCompilerArguments{

        public static final String WRAP_SCHEMA_CLASSES = "w";
        public static final String WRITE_SCHEMA_CLASSES = "r";
        public static final String STYLE = "s";
        public static final String PACKAGE = "p";
        public static final String MAPPER_PACKAGE = "mp";
        public static final String HELPER_MODE = "h";
        public static final String OFF_STRICT_VALIDATION = "osv";

    }

    public static class SchemaCompilerInfoHolder{
        public static final String CLASSNAME_KEY = "CLASS_NAME";
        public static final String CLASSNAME_PRIMITVE_KEY = "CLASS_NAME_PRIMITIVE";
        public static final String FAKE_QNAME = "Q_NAME";
    }
}
