/*
 * Copyright (c) 2015 Goran Ehrsson.
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

import spock.lang.Specification

/**
 * Test UuidEntity annotation.
 */
class UuidEntitySpec extends Specification {

    def guid(arg) {
        arg
    }

    def "test generation of uuid"() {
        expect:
        new TestUuidEntity(name: "test").toString().length() == 36 // UUID i.e. c8bd691a-93cb-4bfe-8c95-526ad276e903
        TestUuidEntity.mapping instanceof Closure

        when:
        def mapping = TestUuidEntity.mapping.clone()
        mapping.delegate = this
        mapping.resolveStrategy = Closure.DELEGATE_ONLY
        def result = mapping.call()

        then:
        result instanceof Map
        result.index == 'test_uuid_entity_guid_idx'
    }
}
