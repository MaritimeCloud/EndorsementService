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

package net.maritimecloud.endorsement.model.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.maritimecloud.endorsement.model.JsonSerializable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class TimestampModel implements JsonSerializable {
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id")
    protected Long id;

    @Getter
    @Setter
    @Column(name = "created_at", updatable=false)
    protected Date createdAt;

    @Getter
    @Setter
    @Column(name = "updated_at")
    protected Date updatedAt;

    /** Called at creation, set created_at and updated_at timestamp */
    @PrePersist
    void createdAt() {
        this.createdAt = this.updatedAt = new Date();
    }

    /** Called on update, set updated_at timestamp */
    @PreUpdate
    void updatedAt() {
        this.updatedAt = new Date();
    }

}
