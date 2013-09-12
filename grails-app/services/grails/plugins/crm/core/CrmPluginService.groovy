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

import groovy.transform.CompileStatic

/**
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
class CrmPluginService {

    static transactional = false

    private final Map<String, List> registrations = [:]

    def pluginManager

/* TODO DSL!
views {
  crmProject.show.content {
        template '/relationsGrid'
        plugin 'crm-relation'
        model {
            [bean: crmProject]
        }
  }
}

exclude {
  crmProject.show.summary {
    plugin: 'crm-project'
  }
}
*/
    @CompileStatic
    void registerView(final String controller, final String action, final String location, final Map params) {
        final String key = controller + '.' + action + '.' + location
        List list = registrations[key]
        if (list == null) {
            list = registrations[key] = []
        }
        log.info "registered view: $key"
        log.debug(params.toString())
        list << params
    }

    @CompileStatic
    boolean hasView(final String controller, final String action, final String location, Map matchParams = null) {
        final String key = controller + '.' + action + '.' + location
        final List<Map> list = registrations[key]
        if (!list) {
            return false
        }
        if (matchParams) {
            Iterator<Map> itor = list.iterator()
            while (itor.hasNext()) {
                Map map = itor.next()
                boolean rval = true
                matchParams.each {k, v ->
                    if (map[k] != v) {
                        rval = false
                    }
                }
                if (rval) {
                    return true
                }
            }
            return false
        }
        return true
    }

    @CompileStatic
    void removeView(final String controller, final String action, final String location, Map matchParams = null) {
        final String key = controller + '.' + action + '.' + location
        final List<Map> list = registrations[key]
        if (list) {
            Iterator<Map> itor = list.iterator()
            while (itor.hasNext()) {
                Map map = itor.next()
                boolean rval = true
                if (matchParams) {
                    matchParams.each {k, v ->
                        if (map[k] != v) {
                            rval = false
                        }
                    }
                }
                if (rval) {
                    itor.remove()
                }
            }
            if (list.isEmpty()) {
                registrations.remove(key)
            }
        }
    }

    @CompileStatic
    List getViews(final String controller, final String action, final String location) {
        final List<Map> list = getRegistrations(controller + '.' + action + '.' + location)
        if (action != '*') {
            list.addAll(getRegistrations(controller + '.*.' + location))
        }
        if (controller != '*') {
            list.addAll(getRegistrations('*.' + action + '.' + location))
        }
        if (controller != '*' && action != '*') {
            list.addAll(getRegistrations('*.*.' + location))
        }
        return list
    }

    @CompileStatic
    private List<Map> getRegistrations(final String key) {
        final List<Map> list = registrations[key]
        if (list == null) {
            list = []
        } else {
            final List<Map> copy = []
            copy.addAll(list)
            list = copy
        }
        return list
    }

}

