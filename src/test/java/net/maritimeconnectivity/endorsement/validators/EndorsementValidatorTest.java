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

package net.maritimeconnectivity.endorsement.validators;

import lombok.extern.slf4j.Slf4j;
import net.maritimeconnectivity.endorsement.model.db.Endorsement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WebAppConfiguration
@Slf4j
public class EndorsementValidatorTest {

    @Autowired
    private EndorsementValidator endorsementValidator;

    @Test
    public void validateValidEndorsement() {
        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn("urn:mrn:mcp:org:idp1:dma");
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn("urn:mrn:mcp:service:idp1:dma:instance:nw-nm");
        validEndorsement.setServiceVersion("0.1.2");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn("urn:mrn:mcp:user:idp1:dma:tgc");
        validEndorsement.setParentMrn("urn:mrn:mcp:service:idp1:dma:design:nw-nm");
        validEndorsement.setParentVersion("0.3.2");

        Errors errors = new BeanPropertyBindingResult(validEndorsement, "validEndorsement");
        endorsementValidator.validate(validEndorsement, errors);
        assertEquals(0, errors.getErrorCount());

    }

    @Test
    public void validateInvalidEndorsement() {
        Endorsement invalidEndorsement = new Endorsement();
        invalidEndorsement.setOrgMrn("urn:mrn:mcp:org:idp1:dma");
        invalidEndorsement.setOrgName("DMA");
        invalidEndorsement.setServiceMrn("urn:mrn:mcp:service:idp1:dma:instance:nw-nm");
        invalidEndorsement.setServiceVersion("0.1.2");
        // Invalid service level - should be "instance"
        invalidEndorsement.setServiceLevel("instances");
        // Invalid user mrn
        invalidEndorsement.setUserMrn("urn:xxmrn:mcl:user:dma:tgc");
        invalidEndorsement.setParentMrn("urn:mrn:mcp:service:idp1:dma:design:nw-nm");
        // Invalid - parentMrn and parentVersion must be set for non-specification level
        invalidEndorsement.setParentVersion(null);

        Errors errors = new BeanPropertyBindingResult(invalidEndorsement, "validEndorsement");
        endorsementValidator.validate(invalidEndorsement, errors);
        assertEquals(3, errors.getErrorCount());

    }

}
