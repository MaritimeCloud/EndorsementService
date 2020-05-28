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

package net.maritimeconnectivity.endorsement.utils;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("accessControlUtil")
@Slf4j
public class AccessControlUtil {
    
    public static final String ORG_PROPERTY_NAME = "org";
    public static final String PERMISSIONS_PROPERTY_NAME = "permissions";

    public static boolean hasAccessToOrg(String orgMrn) {
        if (orgMrn == null || orgMrn.trim().isEmpty()) {
            log.debug("The orgMrn was empty!");
            return false;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is part of the organization
        if (auth instanceof KeycloakAuthenticationToken) {
            log.debug("OIDC authentication in process");
            // Keycloak authentication
            KeycloakAuthenticationToken kat = (KeycloakAuthenticationToken) auth;
            KeycloakSecurityContext ksc = (KeycloakSecurityContext) kat.getCredentials();
            Map<String, Object> otherClaims = ksc.getToken().getOtherClaims();
            if (otherClaims.containsKey(AccessControlUtil.ORG_PROPERTY_NAME) &&
                    ((String) otherClaims.get(AccessControlUtil.ORG_PROPERTY_NAME)).equalsIgnoreCase(orgMrn)) {
                log.debug("Entity from org: " + otherClaims.get(AccessControlUtil.ORG_PROPERTY_NAME) + " is in " + orgMrn);
                return true;
            }
            log.debug("Entity from org: " + otherClaims.get(AccessControlUtil.ORG_PROPERTY_NAME) + " is not in " + orgMrn);
        } else {
            if (auth != null) {
                log.debug("Unknown authentication method: " + auth.getClass());
            }
        }
        return false;
    }

    public static boolean hasPermission(String permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof KeycloakAuthenticationToken) {
            log.debug("OIDC permission lookup");
            // Keycloak authentication
            KeycloakAuthenticationToken kat = (KeycloakAuthenticationToken) auth;
            KeycloakSecurityContext ksc = (KeycloakSecurityContext) kat.getCredentials();
            Map<String, Object> otherClaims = ksc.getToken().getOtherClaims();
            if (otherClaims.containsKey(AccessControlUtil.PERMISSIONS_PROPERTY_NAME)) {
                String usersPermissions = (String) otherClaims.get(AccessControlUtil.PERMISSIONS_PROPERTY_NAME);
                String[] permissionList = usersPermissions.split(",");
                for (String per : permissionList) {
                    if (per.equalsIgnoreCase(permission)) {
                        return true;
                    }
                }
            }
        } else {
            if (auth != null) {
                log.debug("Unknown authentication method: " + auth.getClass());
            }
        }
        return false;
    }
}
