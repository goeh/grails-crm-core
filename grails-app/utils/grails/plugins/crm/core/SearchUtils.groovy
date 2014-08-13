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

import groovy.transform.CompileStatic

import java.text.DecimalFormat

final class SearchUtils {

    private SearchUtils() {}

    @CompileStatic
    static String wildcard(String q) {
        q = q.toLowerCase()
        if (q.contains('*')) {
            return q.replace('*', '%')
        } else if (q[0] == '=') { // Exact match.
            return q[1..-1]
        } else { // Starts with is default.
            return q + '%'
        }
    }

    private static Closure dateClosure = { final String field, final String criteria ->
        if (criteria[0] == '<') {
            lt(field, DateUtils.parseSqlDate(criteria[1..-1]))
        } else if (criteria[0] == '>') {
            gt(field, DateUtils.parseSqlDate(criteria[1..-1]))
        } else if (criteria.split('-').size() == 2) {
            def tmp = criteria.split('-')
            between(field, DateUtils.parseSqlDate(tmp[0]), DateUtils.parseSqlDate(tmp[1]))
        } else if (criteria == '!') {
            isNull(field)
        } else if (criteria == '*') {
            isNotNull(field)
        } else {
            eq(field, DateUtils.parseSqlDate(criteria))
        }
    }

    /**
     * Apply date query on a date property.
     *
     * @deprecated use {@link #sqlDateQuery(Object, String, String, Locale, TimeZone)} instead.
     *
     * @param dateProperty
     * @param dateCriteria
     * @param criteriaBuilder
     */
    @Deprecated
    @CompileStatic
    static void dateCriteria(final String dateProperty, final String dateCriteria, Object criteriaBuilder) {
        Closure closure = (Closure) dateClosure.clone()
        closure.delegate = criteriaBuilder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call(dateProperty, dateCriteria)
    }

    /**
     * Parse a query string and apply criteria for a Double property.
     *
     * @param criteriaDelegate
     * @param prop the property to query
     * @param query the query string, can contain &lt; &gt and -
     * @param locale the locale to use for number parsing
     */
    static void doubleQuery(Object criteriaDelegate, String prop, String query, Locale locale) {
        if (!query) {
            return
        }
        final DecimalFormat format = DecimalFormat.getNumberInstance(locale)
        if (query[0] == '<') {
            criteriaDelegate.lt(prop, format.parse(query.substring(1)))
        } else if (query[0] == '>') {
            criteriaDelegate.gt(prop, format.parse(query.substring(1)))
        } else if (query.contains('-')) {
            def (from, to) = query.split('-').toList()
            criteriaDelegate.between(prop, format.parse(from), format.parse(to))
        } else {
            criteriaDelegate.eq(prop, format.parse(query))
        }
    }

    /**
     * Parse a query string and apply criteria for a java.sql.Date property.
     *
     * @param criteriaDelegate
     * @param prop the property to query
     * @param query the query string, can contain &lt &gt and -
     * @param locale the locale to use for date parsing
     * @param timezone the timezone to use for date parsing
     */
    static void sqlDateQuery(Object criteriaDelegate, String prop, String query, Locale locale, TimeZone timezone) {
        if (!query) {
            return
        }
        if (query[0] == '<') {
            criteriaDelegate.lt(prop, DateUtils.parseSqlDate(query.substring(1), timezone))
        } else if (query[0] == '>') {
            criteriaDelegate.gt(prop, DateUtils.parseSqlDate(query.substring(1), timezone))
        } else if (query.contains(' ')) {
            def (from, to) = query.split(' ').toList()
            criteriaDelegate.between(prop, DateUtils.parseSqlDate(from, timezone), DateUtils.parseSqlDate(to, timezone))
        } else {
            criteriaDelegate.eq(prop, DateUtils.parseSqlDate(query, timezone))
        }
    }

}
