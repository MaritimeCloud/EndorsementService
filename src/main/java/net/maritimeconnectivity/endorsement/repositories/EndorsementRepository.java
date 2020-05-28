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

package net.maritimeconnectivity.endorsement.repositories;

import net.maritimeconnectivity.endorsement.model.db.Endorsement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface EndorsementRepository extends PagingAndSortingRepository<Endorsement, Long> {
    Page<Endorsement> findByOrgMrnAndServiceLevel(String orgMrn, String serviceLevel, Pageable pageable);
    Page<Endorsement> findByServiceMrnAndServiceVersion(String serviceMrn, String serviceVersion, Pageable pageable);
    List<Endorsement> findByServiceMrnAndServiceVersion(String serviceMrn, String serviceVersion);
    Endorsement findByOrgMrnAndServiceMrnAndServiceVersion(String orgMrn, String serviceMrn, String serviceVersion);
    Page<Endorsement> findByParentMrnAndParentVersion(String parentMrn, String parentVersion, Pageable pageable);
    Page<Endorsement> findByParentMrnAndParentVersionAndOrgMrn(String parentMrn, String parentVersion, String orgMrn, Pageable pageable);
    Page<Endorsement> findByServiceMrnIn(List<String> serviceMrns, Pageable pageable);

}
