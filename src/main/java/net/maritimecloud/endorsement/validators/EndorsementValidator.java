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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Component
public class EndorsementValidator implements Validator, InitializingBean {

    private javax.validation.Validator validator;

    public void afterPropertiesSet() throws Exception {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }
    @Override
    public boolean supports(Class<?> clazz) {
        return Endorsement.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Endorsement endorsement = (Endorsement) target;
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(target);
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            String propertyPath = constraintViolation.getPropertyPath().toString();
            String message = constraintViolation.getMessage();
            errors.rejectValue(propertyPath, "", message);
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serviceMrn", "serviceMrn.empty", "serviceMrn  is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serviceVersion", "serviceVersion.empty", "serviceVersion  is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orgMrn", "orgMrn.empty", "orgMrn  is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orgName", "orgName.empty", "orgName  is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userMrn", "userMrn.empty", "userMrn  is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serviceLevel", "serviceLevel.empty", "serviceLevel  is required.");
        if (!"specification".equals(endorsement.getServiceLevel())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "parentMrn", "parentMrn.empty", "parentMrn and parentVersion is dependant on each other.");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "parentVersion", "parentVersion.empty", "parentMrn and parentVersion is dependant on each other.");
        }

    }
}