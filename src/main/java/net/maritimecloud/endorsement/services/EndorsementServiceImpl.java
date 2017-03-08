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

package net.maritimecloud.endorsement.services;

import net.maritimecloud.endorsement.model.Endorsement;
import net.maritimecloud.endorsement.repositories.EndorsementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EndorsementServiceImpl implements EndorsementService {
    @Autowired
    private EndorsementRepository endorsementRepository;

    public Page<Endorsement> listByOrgMrnAndServiceLevel(String orgMrn, String serviceLevel, Pageable pageable) {
        return this.endorsementRepository.findByOrgMrnAndServiceLevel(orgMrn, serviceLevel, pageable);
    }

    public Page<Endorsement> listByServiceMrnAndServiceLevel(String serviceMrn, String serviceLevel, Pageable pageable) {
        return this.endorsementRepository.findByServiceMrnAndServiceLevel(serviceMrn, serviceLevel, pageable);
    }

    @Transactional
    public Endorsement saveEndorsement(Endorsement endorsement) {
        return this.endorsementRepository.save(endorsement);
    }

    @Transactional
    public void deleteEndorsement(Endorsement endorsement) {
        this.endorsementRepository.delete(endorsement);
    }

    public Endorsement getByOrgMrnAndServiceMrn(String orgMrn, String serviceMrn) {
        return this.endorsementRepository.findByOrgMrnAndServiceMrn(orgMrn, serviceMrn);
    }
}
