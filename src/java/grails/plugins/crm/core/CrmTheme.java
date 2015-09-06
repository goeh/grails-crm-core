/*
 * Copyright 2013 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.crm.core;

import java.io.Serializable;

/**
 * This class holds a theme name and a tenant ID.
 */
public class CrmTheme implements Serializable {

    private static final long serialVersionUID = 139732013571L;

    private String name;
    private Long tenant;

    CrmTheme(String name, Long tenant) {
        if (name == null) {
            throw new ExceptionInInitializerError("theme name must be specified");
        }
        if (tenant == null) {
            throw new ExceptionInInitializerError("theme tenant must be specified");
        }
        this.name = name;
        this.tenant = tenant;
    }

    public String getName() {
        return name;
    }

    public Long getTenant() {
        return tenant;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append(name);
        s.append('@');
        s.append(tenant.toString());
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrmTheme crmTheme = (CrmTheme) o;

        if (!name.equals(crmTheme.name)) return false;
        if (!tenant.equals(crmTheme.tenant)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + tenant.hashCode();
        return result;
    }
}
