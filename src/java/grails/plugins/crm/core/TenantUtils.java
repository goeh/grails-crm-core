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

import groovy.lang.Closure;

/**
 * @author Goran Ehrsson
 */
public class TenantUtils {

    private static final ThreadLocal<Long> contextHolder = new ThreadLocal<Long>();

    public static void setTenant(Long tenant) {
        contextHolder.set(tenant);
    }

    public static Long getTenant() {
        Long tenant = contextHolder.get();
        if (tenant == null) {
            tenant = Long.valueOf(0);
        }
        return tenant;
    }

    static void clear() {
        contextHolder.remove();
    }

    public static Object withTenant(Long tenantId, Closure work) {
        Object rval = null;
        final Long previousTenantId = TenantUtils.getTenant();
        if ((previousTenantId != null) && !previousTenantId.equals(tenantId)) {
            try {
                TenantUtils.setTenant(tenantId);
                rval = work.call();
            } finally {
                TenantUtils.setTenant(previousTenantId);
            }
        } else {
            rval = work.call();
        }
        return rval;
    }
}
