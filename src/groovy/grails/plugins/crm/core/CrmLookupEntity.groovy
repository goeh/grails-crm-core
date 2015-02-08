/*
 * Copyright (c) 2014 Goran Ehrsson.
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

    static transients = ['dao']

    static searchable = [only: ['name', 'description']]

    static List BIND_WHITELIST = ['orderIndex', 'enabled', 'name', 'param', 'icon', 'description'].asImmutable()

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

    transient Map<String, Object> getDao() {
        BIND_WHITELIST.inject([:]) {map, p ->
            map[p] = this[p]
            map
        }
    }

    @Override
    String toString() {
        return "$name"
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        //if (getClass() != o.class) return false
        // TODO How can we handle Hibernate proxies here without importing org.hibernate?
        // grails.plugins.crm.order.CrmOrderStatus_$$_javassist_7
        // grails.plugins.crm.order.CrmOrderStatus

        CrmLookupEntity that = (CrmLookupEntity) o

        if (this.id != that.id) return false
        if (this.orderIndex != that.orderIndex) return false
        if (this.name != that.name) return false
        if (this.param != that.param) return false

        return true
    }

    @Override
    int hashCode() {
        int result
        result = id != null ? id.intValue() : 0
        result = 31 * result + (orderIndex != null ? orderIndex.intValue() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        result = 31 * result + (param != null ? param.hashCode() : 0)
        return result
    }
}
