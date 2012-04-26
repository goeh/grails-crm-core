/*
 *  Copyright 2012 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package grails.plugins.crm.core

/**
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
class CrmPluginService {
    static transactional = false

    private final Map registrations = [:]

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
    def registerView(controller, action, location, params) {
        def key = controller + '.' + action + '.' + location
        def list = registrations[key]
        if(list == null) {
            list = registrations[key] = []
        }
        log.info "registered view: $key"
        log.debug(params.toString())
        list << params
    }

    def hasView(controller, action, location, matchParams = null) {
        def key = controller + '.' + action + '.' + location
        def list = registrations[key]
        if(!list) {
            return false
        }
        if(matchParams) {
            def itor = list.iterator()
            while(itor.hasNext()) {
                def map = itor.next()
                def rval = true
                matchParams.each{k, v->
                    if(map[k] != v) {
                        rval = false
                    }
                }
                if(rval) {
                    return true
                }
            }
            return false
        }
        return true
    }

    def removeView(controller, action, location, matchParams) {
        def key = controller + '.' + action + '.' + location
        def list = registrations[key]
        if(list) {
            def itor = list.iterator()
            while(itor.hasNext()) {
                def map = itor.next()
                def rval = true
                if(matchParams) {
                    matchParams.each{k, v->
                        if(map[k] != v) {
                            rval = false
                        }
                    }
                }
                if(rval) {
                    itor.remove()
                }
            }
            if(list.isEmpty()) {
                registrations.remove(key)
            }
        }
    }

    List getViews(controller, action, location) {
        def list = getRegistrations(controller + '.' + action + '.' + location)
        if(action != '*') {
            list.addAll(getRegistrations(controller + '.*.' + location))
        }
        if(controller != '*') {
            list.addAll(getRegistrations('*.' + action + '.' + location))
        }
        if(controller != '*' && action != '*') {
            list.addAll(getRegistrations('*.*.' + location))
        }
        return list
    }

    private List getRegistrations(String key) {
        def list = registrations[key]
        if(list == null) {
            list = []
        } else {
            def copy = []
            copy.addAll(list)
            list = copy
        }
        return list
    }

}

