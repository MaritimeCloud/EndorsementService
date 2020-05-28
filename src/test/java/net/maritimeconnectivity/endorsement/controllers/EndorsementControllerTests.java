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
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration
@WebAppConfiguration
@EnableSpringDataWebSupport
@Slf4j
public class EndorsementControllerTests {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private AccessControlUtil accessControlUtil;

    @MockBean
    private EndorsementService endorsementService;
    
    private static final String ORG_MRN = "urn:mrn:mcp:org:idp1:dma";
    private static final String INSTANCE_MRN = "urn:mrn:mcp:service:idp1:dma:instance:nw-nm";
    private static final String USER_MRN = "urn:mrn:mcp:user:idp1:dma:tgc";
    private static final String DESIGN_MRN = "urn:mrn:mcp:service:idp1:dma:design:nw-nm";

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
        given(this.endorsementService.listByOrgMrnAndServiceLevel(ORG_MRN, "instance", null)).willReturn(new PageImpl<Endorsement>(Collections.emptyList()));
        try {
            mvc.perform(get("/oidc/endorsements-by/instance/" + ORG_MRN).header("Origin", "bla")).andExpect(status().isUnauthorized());
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    /**
     * Try to get a endorsement with authentication
     */
    @Test
    public void testAccessGetEndorsementWithAuthentication() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken(ORG_MRN, "ROLE_USER", "");

        given(this.endorsementService.listByOrgMrnAndServiceLevel(ORG_MRN, "instance", null)).willReturn(new PageImpl<Endorsement>(Collections.emptyList()));
        try {
            mvc.perform(get("/oidc/endorsements-by/instance/" + ORG_MRN).with(authentication(auth)).header("Origin", "bla")).andExpect(status().isOk());
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    /**
     * Try to create an endorsement with correct authentication
     */
    @Test
    public void testCreateEndorsement() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken(ORG_MRN, "ROLE_USER", "");

        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn(ORG_MRN);
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn(INSTANCE_MRN);
        validEndorsement.setServiceVersion("0.1.2");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn(USER_MRN);
        validEndorsement.setParentMrn(DESIGN_MRN);
        validEndorsement.setParentVersion("0.3.2");
        String endorsementJson = serialize(validEndorsement);

        given(this.endorsementService.getByOrgMrnAndServiceMrnAndServiceVersion(ORG_MRN, INSTANCE_MRN, "0.1.2")).willReturn(null);
        try {
            mvc.perform(post("/oidc/endorsements").with(authentication(auth))
                    .header("Origin", "bla")
                    .content(endorsementJson)
                    .contentType("application/json")
            ).andExpect(status().isOk());
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    /**
     * Try to create an endorsement with incorrect authentication - mismatch between token org and endorsing org
     */
    @Test
    public void testCreateEndorsementInvalidOrg() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken("urn:mrn:mcp:org:idp1:sma", "ROLE_USER", "");

        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn(ORG_MRN);
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn(INSTANCE_MRN);
        validEndorsement.setServiceVersion("0.1.2");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn(USER_MRN);
        validEndorsement.setParentMrn(DESIGN_MRN);
        validEndorsement.setParentVersion("0.3.2");
        String endorsementJson = serialize(validEndorsement);

        given(this.endorsementService.getByOrgMrnAndServiceMrnAndServiceVersion(ORG_MRN, INSTANCE_MRN, "0.1.2")).willReturn(null);
        try {
            mvc.perform(post("/oidc/endorsements").with(authentication(auth))
                    .header("Origin", "bla")
                    .content(endorsementJson)
                    .contentType("application/json")
            ).andExpect(status().isForbidden());
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    /**
     * Try to create an endorsement with invalid data
     */
    @Test
    public void testCreateInvalidEndorsement() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken(ORG_MRN, "ROLE_USER", "");

        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn(ORG_MRN);
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn(INSTANCE_MRN);
        validEndorsement.setServiceVersion("0.1.2");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn(USER_MRN);
        validEndorsement.setParentMrn(DESIGN_MRN);
        String endorsementJson = serialize(validEndorsement);

        given(this.endorsementService.getByOrgMrnAndServiceMrnAndServiceVersion(ORG_MRN, INSTANCE_MRN, "0.1.2")).willReturn(null);
        try {
            mvc.perform(post("/oidc/endorsements").with(authentication(auth))
                    .header("Origin", "bla")
                    .content(endorsementJson)
                    .contentType("application/json")
            ).andExpect(status().isBadRequest());
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    /**
     * Try to get an endorsement list with authentication
     */
    @Test
    public void testAccessGetEndorsementListWithAuthentication() {
        KeycloakAuthenticationToken auth = TokenGenerator.generateKeycloakToken(ORG_MRN, "ROLE_USER", "");
        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn(ORG_MRN);
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn(INSTANCE_MRN);
        validEndorsement.setServiceVersion("0.3");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn(USER_MRN);
        validEndorsement.setParentMrn(DESIGN_MRN);
        String endorsementJson = serialize(validEndorsement);
        endorsementJson = String.format("{\"content\":[%s]}", endorsementJson);
        log.debug(endorsementJson);

        given(this.endorsementService.listByServiceMrnAndServiceVersion(eq(ORG_MRN), eq("0.3"), any()))
                .willReturn(new PageImpl<>(Collections.singletonList(validEndorsement)));
        try {
            mvc.perform(get(String.format("/oidc/endorsements/%s/0.3", ORG_MRN)).with(authentication(auth)).header("Origin", "bla"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(endorsementJson));
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
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
            return mapper.writeValueAsString(endorsement);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
