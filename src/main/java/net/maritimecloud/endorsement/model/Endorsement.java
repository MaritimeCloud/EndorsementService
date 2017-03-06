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
package net.maritimecloud.endorsement.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.maritimecloud.endorsement.validators.MRN;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(exclude={"id"})
@ToString
@Entity
@Table(name="endorsements")
public class Endorsement extends TimestampModel {

    public Endorsement() {
    }

    @Getter
    @Setter
    @MRN
    @NotBlank
    @Column(name = "service_mrn")
    private String serviceMrn;

    @Getter
    @Setter
    @MRN
    @NotBlank
    @Column(name = "org_mrn")
    private String orgMrn;

    @Getter
    @Setter
    @NotBlank
    @Column(name = "org_name")
    private String orgName;

    @Getter
    @Setter
    @MRN
    @NotBlank
    @Column(name = "user_mrn")
    private String userMrn;

    @Getter
    @Setter
    @NotBlank
    @Column(name = "service_level")
    private String serviceLevel;

}
