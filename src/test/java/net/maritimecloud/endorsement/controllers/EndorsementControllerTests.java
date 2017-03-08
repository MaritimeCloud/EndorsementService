/*
 * Copyright 2017 Danish Maritime Authority.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.maritimecloud.endorsement.controllers;


import net.maritimecloud.endorsement.model.Endorsement;
import net.maritimecloud.endorsement.services.EndorsementService;
import net.maritimecloud.endorsement.utils.AccessControlUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@WebAppConfiguration
@EnableSpringDataWebSupport
public class EndorsementControllerTests {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private AccessControlUtil accessControlUtil;

    @MockBean
    private EndorsementService endorsementService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                //.alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }


    /**
     * Try to get a service without being authenticated
     */
    @Test
    public void testAccessGetServiceWithoutAuthentication() {
        given(this.endorsementService.listByOrgMrnAndServiceLevel("urn:mrn:mcl:org:dma", "instance", null)).willReturn(new PageImpl<Endorsement>(Collections.emptyList()));
        try {
            mvc.perform(get("/oidc/endorsements/instance/urn:mrn:mcl:org:dma").header("Origin", "bla")).andExpect(status().isUnauthorized());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Try to get a service without being authenticated
     */
    @Test
    public void testAccessGetServiceWithAuthentication() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken("urn:mrn:mcl:org:dma", "ROLE_USER", "");

        given(this.endorsementService.listByOrgMrnAndServiceLevel("urn:mrn:mcl:org:dma", "instance", null)).willReturn(new PageImpl<Endorsement>(Collections.emptyList()));
        try {
            mvc.perform(get("/oidc/endorsements/instance/urn:mrn:mcl:org:dma").with(authentication(auth)).header("Origin", "bla")).andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

}
