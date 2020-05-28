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

import java.util.regex.Pattern;

/**
 * Utility class to create, validate and extract certain info from MRNs
 */
public class MrnUtil {

    private MrnUtil() {
        // empty so it cannot be instantiated
    }

    public static final Pattern MRN_PATTERN = Pattern.compile("^urn:mrn:mcp:(device|org|user|vessel|service|mms):([a-z0-9]([a-z0-9]|-){0,20}[a-z0-9]):((([-._a-z0-9]|~)|%[0-9a-f][0-9a-f]|([!$&'()*+,;=])|:|@)((([-._a-z0-9]|~)|%[0-9a-f][0-9a-f]|([!$&'()*+,;=])|:|@)|/)*)$", Pattern.CASE_INSENSITIVE);
    public static final Pattern MRN_SERVICE_PATTERN = Pattern.compile("^urn:mrn:mcp:service:([a-z0-9]([a-z0-9]|-){0,20}[a-z0-9]):((([-._a-z0-9]|~)|%[0-9a-f][0-9a-f]|([!$&'()*+,;=])|:|@)((([-._a-z0-9]|~)|%[0-9a-f][0-9a-f]|([!$&'()*+,;=])|:|@)|/)*)$", Pattern.CASE_INSENSITIVE);

    public static boolean validateMrn(String mrn) {
        if (mrn == null || mrn.trim().isEmpty()) {
            throw new IllegalArgumentException("MRN is empty");
        }
        if (!MRN_PATTERN.matcher(mrn).matches()) {
            throw new IllegalArgumentException("MRN is not in a valid format");
        }
        // validate mrn based on the entity type
        String[] parts = mrn.split(":");
        if (parts[3].equals("service")) {
            Pattern serviceTypes = Pattern.compile("^(specification|design|instance)$", Pattern.CASE_INSENSITIVE);
            if (!MRN_SERVICE_PATTERN.matcher(mrn).matches() || parts.length < 8 || !serviceTypes.matcher(parts[6]).matches()) {
                throw new IllegalArgumentException("MRN is not in a valid format for a service");
            }
        }
        return true;
    }
}
