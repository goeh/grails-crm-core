/*
 * Copyright 2012 Goran Ehrsson.
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
package grails.plugins.crm.core

/**
 * Base class for domain classes that represents lookup information.
 * @author Goran Ehrsson
 * @since 0.1
 */
@TenantEntity
abstract class CrmLookupEntity {

    int orderIndex
    boolean enabled
    String name
    String param
    String icon
    String description

    static constraints = {
        name(maxSize: 80, blank: false, unique: 'tenantId')
        param(maxSize: 20, nullable: true)
        icon(maxSize: 100, nullable: true)
        description(maxSize: 2000, nullable: true, widget: 'textarea')
    }
    static mapping = {
        sort 'orderIndex'
        cache usage: 'nonstrict-read-write'
    }

    static searchable = [only: ['name', 'description']]

    static final BIND_WHITELIST = ['orderIndex', 'enabled', 'name', 'param', 'icon', 'description'].asImmutable()

    def beforeValidate() {
        if (orderIndex == 0) {
            def tenant = TenantUtils.getTenant()
            def mx
            withNewSession {
                mx = this.getClass().createCriteria().get {
                    projections {
                        max "orderIndex"
                    }
                    eq('tenantId', tenant)
                }
            }
            orderIndex = mx ? mx + 1 : 1
        }
    }

    @Override
    String toString() {
        return "$name"
    }

}
