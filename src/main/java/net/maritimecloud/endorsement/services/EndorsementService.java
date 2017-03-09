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


import net.maritimecloud.endorsement.model.db.Endorsement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EndorsementService {
    Page<Endorsement> listByOrgMrnAndServiceLevel(String orgMrn, String serviceLevel, Pageable pageable);
    Page<Endorsement> listByServiceMrn(String serviceMrn, Pageable pageable);
    List<Endorsement> listByServiceMrn(String serviceMrn);
    Page<Endorsement> listByParentMrn(String serviceMrn, Pageable pageable);
    Page<Endorsement> listByParentMrnAndOrgMrn(String serviceMrn, String orgMrn, Pageable pageable);
    Page<Endorsement> listByServiceMrns(List<String> serviceMrns, Pageable pageable);
    Endorsement saveEndorsement(Endorsement endorsement);
    void deleteEndorsement(Endorsement endorsement);
    Endorsement getByOrgMrnAndServiceMrn(String orgMrn, String serviceMrn);
}
