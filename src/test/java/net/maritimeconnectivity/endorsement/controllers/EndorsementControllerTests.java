/*
 * Copyright 2017 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.maritimeconnectivity.endorsement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.maritimeconnectivity.endorsement.model.db.Endorsement;
import net.maritimeconnectivity.endorsement.services.EndorsementService;
import net.maritimeconnectivity.endorsement.utils.AccessControlUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
     * Try to get a endorsement without being authenticated
     */
    @Test
    public void testAccessGetEndorsementWithoutAuthentication() {
        given(this.endorsementService.listByOrgMrnAndServiceLevel("urn:mrn:mcl:org:dma", "instance", null)).willReturn(new PageImpl<Endorsement>(Collections.emptyList()));
        try {
            mvc.perform(get("/oidc/endorsements-by/instance/urn:mrn:mcl:org:dma").header("Origin", "bla")).andExpect(status().isUnauthorized());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Try to get a endorsement with authentication
     */
    @Test
    public void testAccessGetEndorsementWithAuthentication() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken("urn:mrn:mcl:org:dma", "ROLE_USER", "");

        given(this.endorsementService.listByOrgMrnAndServiceLevel("urn:mrn:mcl:org:dma", "instance", null)).willReturn(new PageImpl<Endorsement>(Collections.emptyList()));
        try {
            mvc.perform(get("/oidc/endorsements-by/instance/urn:mrn:mcl:org:dma").with(authentication(auth)).header("Origin", "bla")).andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Try to create an endorsement with correct authentication
     */
    @Test
    public void testCreateEndorsement() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken("urn:mrn:mcl:org:dma", "ROLE_USER", "");

        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn("urn:mrn:mcl:org:dma");
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn("urn:mrn:mcl:service-instance:dma:nw-nv");
        validEndorsement.setServiceVersion("0.1.2");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn("urn:mrn:mcl:user:dma:tgc");
        validEndorsement.setParentMrn("urn:mrn:mcl:service-design:dma:nw-nv");
        validEndorsement.setParentVersion("0.3.2");
        String endorsementJson = serialize(validEndorsement);

        given(this.endorsementService.getByOrgMrnAndServiceMrnAndServiceVersion("urn:mrn:mcl:org:dma", "urn:mrn:mcl:service-instance:dma:nw-nv", "0.1.2")).willReturn(null);
        try {
            mvc.perform(post("/oidc/endorsements").with(authentication(auth))
                    .header("Origin", "bla")
                    .content(endorsementJson)
                    .contentType("application/json")
            ).andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Try to create an endorsement with incorrect authentication - mismatch between token org and endorsing org
     */
    @Test
    public void testCreateEndorsementInvalidOrg() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken("urn:mrn:mcl:org:sma", "ROLE_USER", "");

        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn("urn:mrn:mcl:org:dma");
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn("urn:mrn:mcl:service-instance:dma:nw-nv");
        validEndorsement.setServiceVersion("0.1.2");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn("urn:mrn:mcl:user:dma:tgc");
        validEndorsement.setParentMrn("urn:mrn:mcl:service-design:dma:nw-nv");
        validEndorsement.setParentVersion("0.3.2");
        String endorsementJson = serialize(validEndorsement);

        given(this.endorsementService.getByOrgMrnAndServiceMrnAndServiceVersion("urn:mrn:mcl:org:dma", "urn:mrn:mcl:service-instance:dma:nw-nv", "0.1.2")).willReturn(null);
        try {
            mvc.perform(post("/oidc/endorsements").with(authentication(auth))
                    .header("Origin", "bla")
                    .content(endorsementJson)
                    .contentType("application/json")
            ).andExpect(status().isForbidden());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Try to create an endorsement with invalid data
     */
    @Test
    public void testCreateInvalidEndorsement() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken("urn:mrn:mcl:org:dma", "ROLE_USER", "");

        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn("urn:mrn:mcl:org:dma");
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn("urn:mrn:mcl:service-instance:dma:nw-nv");
        validEndorsement.setServiceVersion("0.1.2");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn("urn:mrn:mcl:user:dma:tgc");
        validEndorsement.setParentMrn("urn:mrn:mcl:service-design:dma:nw-nv");
        String endorsementJson = serialize(validEndorsement);

        given(this.endorsementService.getByOrgMrnAndServiceMrnAndServiceVersion("urn:mrn:mcl:org:dma", "urn:mrn:mcl:service-instance:dma:nw-nv", "0.1.2")).willReturn(null);
        try {
            mvc.perform(post("/oidc/endorsements").with(authentication(auth))
                    .header("Origin", "bla")
                    .content(endorsementJson)
                    .contentType("application/json")
            ).andExpect(status().isBadRequest());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Try to get an endorsement list with authentication
     */
    @Test
    public void testAccessGetEndorsementListWithAuthentication() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken("urn:mrn:mcl:org:dma", "ROLE_USER", "");
        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn("urn:mrn:mcl:org:dma");
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn("urn:mrn:mcl:service-instance:dma:nw-nv");
        validEndorsement.setServiceVersion("0.3");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn("urn:mrn:mcl:user:dma:tgc");
        validEndorsement.setParentMrn("urn:mrn:mcl:service-design:dma:nw-nv");
        String endorsementJson = serialize(validEndorsement);

        given(this.endorsementService.listByServiceMrnAndServiceVersion(eq("urn:mrn:mcl:service:design:dma:nw-nm-rest"), eq("0.3"), any()))
                .willReturn(new PageImpl<>(Arrays.asList(validEndorsement)));
        try {
            mvc.perform(get("/oidc/endorsements/urn:mrn:mcl:service:design:dma:nw-nm-rest/0.3").with(authentication(auth)).header("Origin", "bla"))
                    .andExpect(status().isOk())
                    .andExpect(content().json("{\"content\":[{\"serviceMrn\":\"urn:mrn:mcl:service-instance:dma:nw-nv\",\"serviceVersion\":\"0.3\",\"orgMrn\":\"urn:mrn:mcl:org:dma\",\"orgName\":\"DMA\",\"userMrn\":\"urn:mrn:mcl:user:dma:tgc\",\"parentMrn\":\"urn:mrn:mcl:service-design:dma:nw-nv\",\"serviceLevel\":\"instance\"}]}", false));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }


    /**
     * Helper function to serialize an endorsement to json
     * @param endorsement
     * @return
     */
    private String serialize(Endorsement endorsement) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Convert object to JSON string and pretty print
            String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(endorsement);
            //System.out.println(jsonInString);

            return jsonInString;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
