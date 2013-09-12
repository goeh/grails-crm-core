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
 * This controller handles the admin/index page, a start page for administrative tasks.
 *
 * @author Goran Ehrsson
 * @since 0.1
 */
class CrmAdminController {

    static navigation = [
            [group: 'admin',
                    order: 990,
                    title: 'crmAdmin.label',
                    action: 'index'
            ],
            [group: 'crmAdmin',
                    order: 90,
                    title: 'crmAdmin.label',
                    action: 'index'
            ],
            [group: 'crmAdmin',
                    order: 95,
                    title: 'crmAdmin.jvm.label',
                    action: 'jvm'
            ]
    ]

    def crmCoreService

    /**
     * Index page.
     *
     * @return features installed application features
     */
    def index() {
        [features: crmCoreService.installedFeatures]
    }

    /**
     * JVM metrics page.
     */
    def jvm() {
    }
}

