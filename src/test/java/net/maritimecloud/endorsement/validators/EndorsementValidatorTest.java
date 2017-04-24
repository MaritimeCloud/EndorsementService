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

package net.maritimecloud.endorsement.validators;

import net.maritimecloud.endorsement.model.db.Endorsement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@WebAppConfiguration
public class EndorsementValidatorTest {

    @Autowired
    private EndorsementValidator endorsementValidator;

    @Test
    public void validateValidEndorsement() {
        Endorsement validEndorsement = new Endorsement();
        validEndorsement.setOrgMrn("urn:mrn:mcl:org:dma");
        validEndorsement.setOrgName("DMA");
        validEndorsement.setServiceMrn("urn:mrn:mcl:service-instance:dma:nw-nv");
        validEndorsement.setServiceVersion("0.1.2");
        validEndorsement.setServiceLevel("instance");
        validEndorsement.setUserMrn("urn:mrn:mcl:user:dma:tgc");
        validEndorsement.setParentMrn("urn:mrn:mcl:service-design:dma:nw-nv");
        validEndorsement.setParentVersion("0.3.2");

        Errors errors = new BeanPropertyBindingResult(validEndorsement, "validEndorsement");
        endorsementValidator.validate(validEndorsement, errors);
        System.out.println(errors.getErrorCount());
        assertEquals(0, errors.getErrorCount());

    }

    @Test
    public void validateInvalidEndorsement() {
        Endorsement invalidEndorsement = new Endorsement();
        invalidEndorsement.setOrgMrn("urn:mrn:mcl:org:dma");
        invalidEndorsement.setOrgName("DMA");
        invalidEndorsement.setServiceMrn("urn:mrn:mcl:service-instance:dma:nw-nv");
        invalidEndorsement.setServiceVersion("0.1.2");
        // Invalid service level - should be "instance"
        invalidEndorsement.setServiceLevel("instances");
        // Invalid user mrn
        invalidEndorsement.setUserMrn("urn:xxmrn:mcl:user:dma:tgc");
        invalidEndorsement.setParentMrn("urn:mrn:mcl:service-design:dma:nw-nv");
        // Invalid - parentMrn and parentVersion must be set for non-specification level
        invalidEndorsement.setParentVersion(null);

        Errors errors = new BeanPropertyBindingResult(invalidEndorsement, "validEndorsement");
        endorsementValidator.validate(invalidEndorsement, errors);
        System.out.println(errors.getErrorCount());
        assertEquals(3, errors.getErrorCount());

    }

}