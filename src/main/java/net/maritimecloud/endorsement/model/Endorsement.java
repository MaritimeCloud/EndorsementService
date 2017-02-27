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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="endorsements")
public class Endorsement extends TimestampModel {

    public Endorsement() {
    }

    @Column(name = "service_mrn")
    private String serviceMrn;

    @Column(name = "org_mrn")
    private String orgMrn;

    @Column(name = "org_name")
    private String orgName;

    @Column(name = "user_mrn")
    private String userMrn;

    @Column(name = "service_level")
    private String serviceLevel;

    public String getServiceMrn() {
        return serviceMrn;
    }

    public void setServiceMrn(String serviceMrn) {
        this.serviceMrn = serviceMrn;
    }

    public String getServiceLevel() {
        return serviceLevel;
    }

    public void setServiceLevel(String serviceLevel) {
        this.serviceLevel = serviceLevel;
    }

    public String getOrgMrn() {
        return orgMrn;
    }

    public void setOrgMrn(String orgMrn) {
        this.orgMrn = orgMrn;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getUserMrn() {
        return userMrn;
    }

    public void setUserMrn(String userMrn) {
        this.userMrn = userMrn;
    }
}
