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
package grails.plugins.crm.core

/**
 * Test CrmPluginService
 */
class CrmPluginServiceSpec extends grails.test.spock.IntegrationSpec {

    def crmPluginService

    def "register a plugin view"() {
        when:
        crmPluginService.registerView('crmContact', 'show', 'tabs',
                [id: "friends", label: "Friends", template: '/crmContact/friends', model: {
                    [result: [[name: "Joe Adams"], [name: "Liza Brown"], [name: "Alex Sherman"]], totalCount: 3]
                }]
        )
        then:
        crmPluginService.hasView('crmContact', 'show', 'tabs')
        !crmPluginService.hasView('crmContact', 'create', 'tabs')

        when:
        crmPluginService.removeView('crmContact', 'show', 'tabs')

        then:
        !crmPluginService.hasView('crmContact', 'show', 'tabs')
    }
}
