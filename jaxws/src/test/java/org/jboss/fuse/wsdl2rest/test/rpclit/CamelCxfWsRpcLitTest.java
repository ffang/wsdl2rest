package org.jboss.fuse.wsdl2rest.test.rpclit;
/*
 * Copyright (c) 2008 SL_OpenSource Consortium
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


import java.net.URL;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.jboss.fuse.wsdl2rest.jaxws.Item;
import org.jboss.fuse.wsdl2rest.jaxws.ItemBuilder;
import org.jboss.fuse.wsdl2rest.jaxws.rpclit.Address;
import org.jboss.fuse.wsdl2rest.util.SpringCamelContextFactory;
import org.junit.Assert;
import org.junit.Test;

public class CamelCxfWsRpcLitTest {

    static String CONTEXT_URL = "http://0.0.0.0:8081/jaxrs";
    
    @Test
    public void testJavaClient() throws Exception {

        URL resourceUrl = getClass().getResource("/rpclit/rpclit-camel-context.xml");
        CamelContext camelctx = SpringCamelContextFactory.createSingleCamelContext(resourceUrl, null);
        camelctx.start();
        try {
            Assert.assertEquals(ServiceStatus.Started, camelctx.getStatus());

            QName qname = new QName("http://rpclit.jaxws.wsdl2rest.fuse.jboss.org/", "AddressService");
            Service service = Service.create(new URL("http://localhost:8080/rpclit/AddressService?wsdl"), qname);
            Address port = service.getPort(Address.class);
            Assert.assertNotNull("Address not null", port);

            Date dob = Item.DATE_FORMAT.parse("11.11.1968");
            Item kermit = new ItemBuilder().id(100).name("Kermit").dateOfBirth(dob).build();
            Item frog = new ItemBuilder().id(100).name("Frog").dateOfBirth(dob).build();
            
            Assert.assertNull(port.listAddresses());
            Assert.assertEquals(100, (int) port.addAddress(kermit));
            Assert.assertEquals("Kermit", port.getAddress(100).getName());
            Assert.assertEquals(100, (int) port.updAddress(frog));
            Assert.assertEquals("Frog", port.delAddress(100).getName());
        } finally {
            camelctx.stop();
        }
    }
}