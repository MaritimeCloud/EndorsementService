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

import net.maritimeconnectivity.endorsement.model.db.Endorsement;
import net.maritimeconnectivity.endorsement.services.EndorsementService;
import net.maritimeconnectivity.endorsement.validators.EndorsementValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value="oidc")
public class EndorseController {

    @Autowired
    private EndorsementValidator endorsementValidator;

    @Autowired
    private EndorsementService endorsementService;

    @InitBinder("endorsement")
    protected void initBinder(final ServletRequestDataBinder binder) {
        binder.addValidators(endorsementValidator);
    }


    @RequestMapping(
            value = "/endorsements",
            method = RequestMethod.POST,
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    @PreAuthorize("@accessControlUtil.hasAccessToOrg(#input.getOrgMrn())")
    public ResponseEntity<Endorsement> createEndorsement(HttpServletRequest request, @Validated @RequestBody Endorsement input) {
        Endorsement endorsement = this.endorsementService.getByOrgMrnAndServiceMrnAndServiceVersion(input.getOrgMrn(), input.getServiceMrn(), input.getServiceVersion());
        if (endorsement != null) {
            endorsement.setUserMrn(input.getUserMrn());
            endorsementService.saveEndorsement(endorsement);
        } else {
            endorsement = endorsementService.saveEndorsement(input);
        }
        return new ResponseEntity<>(endorsement, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/endorsements/{serviceMrn}/{serviceVersion}",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Page<Endorsement> getEndorsementsByServiceMrn(HttpServletRequest request, @PathVariable String serviceMrn, @PathVariable String serviceVersion, Pageable pageable) {
        return endorsementService.listByServiceMrnAndServiceVersion(serviceMrn, serviceVersion, pageable);
    }

    @RequestMapping(
            value = "/endorsements-by/{serviceLevel}/{orgMrn}",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Page<Endorsement> getEndorsementsByOrgMrn(HttpServletRequest request, @PathVariable String serviceLevel, @PathVariable String orgMrn, Pageable pageable) {
        return endorsementService.listByOrgMrnAndServiceLevel(orgMrn, serviceLevel, pageable);
    }

    @RequestMapping(
            value = "/endorsements/{serviceMrn}/{serviceVersion}/{orgMrn}",
            method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("@accessControlUtil.hasAccessToOrg(#orgMrn)")
    public ResponseEntity<?> deleteEndorsement(HttpServletRequest request, @PathVariable String serviceMrn, @PathVariable String serviceVersion, @PathVariable String orgMrn) {
        Endorsement endorsement = this.endorsementService.getByOrgMrnAndServiceMrnAndServiceVersion(orgMrn, serviceMrn, serviceVersion);
        if (endorsement == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        endorsementService.deleteEndorsement(endorsement);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(
            value = "/endorsement-by/{serviceMrn}/{serviceVersion}/{orgMrn}",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<?> getEndorsement(HttpServletRequest request, @PathVariable String serviceMrn, @PathVariable String serviceVersion, @PathVariable String orgMrn) {
        Endorsement endorsement = this.endorsementService.getByOrgMrnAndServiceMrnAndServiceVersion(orgMrn, serviceMrn, serviceVersion);
        if (endorsement == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(endorsement, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/endorsed-children/{parentMrn}/{parentVersion}",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Page<Endorsement> getEndorsedByParentMrn(HttpServletRequest request, @PathVariable String parentMrn, @PathVariable String parentVersion, Pageable pageable) {
        return endorsementService.listByParentMrnAndParentVersion(parentMrn, parentVersion, pageable);
    }

    @RequestMapping(
            value = "/endorsed-children/{parentMrn}/{parentVersion}/{orgMrn}",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Page<Endorsement> getEndorsedByParentMrnAndOrgMrn(HttpServletRequest request, @PathVariable String parentMrn, @PathVariable String parentVersion, @PathVariable String orgMrn, Pageable pageable) {
        return endorsementService.listByParentMrnAndOrgMrn(parentMrn, parentVersion, orgMrn, pageable);
    }

}
