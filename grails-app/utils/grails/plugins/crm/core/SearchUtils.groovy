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

import org.apache.commons.codec.language.Soundex
import org.apache.commons.codec.language.DoubleMetaphone

final class SearchUtils {

    private SearchUtils() {}

    static String wildcard(String q) {
        q = q.toLowerCase()
        if(q.contains('*')) {
            return q.replace('*', '%')
        } else if(q[0] == '=') { // Exact match.
            return q[1..-1]
        } else { // Starts with is default.
            return q + '%'
        }
    }

    private static Closure dateClosure = {field, criteria ->
        if(criteria[0] == '<') {
            lt(field, DateUtils.parseSqlDate(criteria[1..-1]))
        } else if(criteria[0] == '>') {
            gt(field, DateUtils.parseSqlDate(criteria[1..-1]))
        } else if(criteria.split('-').size() == 2) {
            def tmp = criteria.split('-')
            between(field, DateUtils.parseSqlDate(tmp[0]), DateUtils.parseSqlDate(tmp[1]))
        } else if(criteria == '!') {
            isNull(field)
        } else if(criteria == '*') {
            isNotNull(field)
        } else {
            eq(field, DateUtils.parseSqlDate(criteria))
        }
    }

    static void dateCriteria(String dateProperty, String dateCriteria, Object criteriaBuilder) {
        def closure = dateClosure.clone()
        closure.delegate = criteriaBuilder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call(dateProperty, dateCriteria)
    }

    static String soundexEncode(String s) {
        if(! s) {
            return null
        }
        String soundex
        try {
            soundex = new Soundex().encode(getNormalizedName(s))
        } catch(IllegalArgumentException e) {
            System.err.println("WARNING: Failed to soundex encode '${s}'")
            soundex = null
        }
        return soundex
    }

    static String doubleMetaphoneEncode(String s) {
        if(! s) {
            return null
        }
        String result
        try {
            result = new DoubleMetaphone().encode(getNormalizedName(s))
        } catch(IllegalArgumentException e) {
            System.err.println("WARNING: Failed to double metaphone encode '${s}'")
            result = null
        }
        return result
    }

    /**
     * Swedish hack! Remove company name prefix and suffix.
     * @todo remove method from this class.
     */
    static String getNormalizedName(String name) {
        def rx1 = /^(ab|hb|kb|aktiebolaget)\s+/
        def rx2 = /(ab|hb|kb|aktiebolag|oy|ltd|inc|llc)\.?$/
        return name.toLowerCase().replaceAll(rx1, '').replaceAll(rx2, '').trim()
    }
}
