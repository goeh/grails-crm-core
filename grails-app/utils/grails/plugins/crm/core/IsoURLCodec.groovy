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

import groovy.transform.CompileStatic

/**
 * Convert URL using ISO-8859-1 encoding.
 *
 * It encodes characters like ÅÄÖ into their hex representations (%C5%C4%D6).
 *
 * Credits to Vivek Krishna for the blog post:
 *   http://www.intelligrape.com/blog/2011/04/11/iso-8859-based-url-encoding-in-grails/
 */
@CompileStatic
class IsoURLCodec {
    static encode = { obj ->
        URLEncoder.encode(obj.toString(), "ISO-8859-1")
    }
    static decode = { obj ->
        URLDecoder.decode(obj.toString(), "ISO-8859-1")
    }
}
