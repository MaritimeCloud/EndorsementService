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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
//@RequestMapping(value={"oidc", "x509"})
@RequestMapping(value="oidc")
public class EndorseController {

    @Autowired
    private EndorsementService endorsementService;

    @RequestMapping(
            value = "/endorsements",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    @PreAuthorize("@accessControlUtil.hasAccessToOrg(#input.getOrgMrn())")
    public ResponseEntity<Endorsement> createEndorment(HttpServletRequest request, @Valid @RequestBody Endorsement input) {
        Endorsement endorsement = endorsementService.saveEndorsement(endorsementService.saveEndorsement(input));
        return new ResponseEntity<>(endorsement, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/endorsements/{serviceLevel}/{serviceMrn}",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<List<Endorsement>> getEndormentsByServiceMrn(HttpServletRequest request, @PathVariable String serviceLevel, @PathVariable String serviceMrn) {
        List<Endorsement> endorsements = endorsementService.listByServiceMrnAndServiceLevel(serviceMrn, serviceLevel);
        return new ResponseEntity<>(endorsements, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/endorsements-by/{serviceLevel}/{orgMrn}",
            method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<List<Endorsement>> getEndormentsByOrgMrn(HttpServletRequest request, @PathVariable String serviceLevel, @PathVariable String orgMrn) {
        List<Endorsement> endorsements = endorsementService.listByOrgMrnAndServiceLevel(orgMrn, serviceLevel);
        return new ResponseEntity<>(endorsements, HttpStatus.OK);
    }

    @RequestMapping(
            value = "/endorsements/{serviceLevel}/{serviceMrn}",
            method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("@accessControlUtil.hasAccessToOrg(#input.getOrgMrn())")
    public ResponseEntity<?> deleteEndorment(HttpServletRequest request, @PathVariable String serviceLevel, @PathVariable String serviceMrn) {
        String orgMrn = "";
        Endorsement endorsement = this.endorsementService.getByOrgMrnAndServiceMrn(orgMrn, serviceMrn);
        if (!serviceLevel.equals(endorsement.getServiceLevel())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        endorsementService.deleteEndorsement(endorsement);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
