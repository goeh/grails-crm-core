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
import java.util.regex.Pattern

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
            criteriaDelegate.lt(prop, format.parse(query.substring(1)).doubleValue())
        } else if (query[0] == '>') {
            criteriaDelegate.gt(prop, format.parse(query.substring(1)).doubleValue())
        } else if (query.contains('-')) {
            def (from, to) = query.split('-').toList()
            criteriaDelegate.between(prop, format.parse(from).doubleValue(), format.parse(to).doubleValue())
        } else if (query.contains(' ')) {
            def (from, to) = query.split(' ').toList()
            criteriaDelegate.between(prop, format.parse(from).doubleValue(), format.parse(to).doubleValue())
        } else {
            criteriaDelegate.eq(prop, format.parse(query).doubleValue())
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

    /*
     * NEW implementation 2016-05-19
     */
    private static final String QUERY_EMPTY = '='
    private static final String QUERY_NOT_EMPTY = '*'
    private static final String NEGATIVE_OFFSET = '-'
    private static final Pattern DATE_OFFSET_PATTERN = Pattern.compile(/([\+\-]?)(\d+)([dwvmy])/)

    private static Date parseDateQuery(String input, Calendar reference) {
        def m = DATE_OFFSET_PATTERN.matcher(input)
        Date date
        if (m.find()) {
            def (direction, offset, scope) = m[0][1..3]
            def calScope
            // TODO move d,v,w,m,y to Config.groovy
            switch (scope) {
                case 'd':
                    calScope = Calendar.DAY_OF_MONTH
                    break
                case 'w':
                case 'v':
                    calScope = Calendar.WEEK_OF_YEAR
                    break
                case 'm':
                    calScope = Calendar.MONTH
                    break
                case 'y':
                    calScope = Calendar.YEAR
                    break
                default:
                    throw new IllegalArgumentException("Invalid calendar scope: $scope")
            }

            offset = Integer.valueOf(offset)
            if (direction == NEGATIVE_OFFSET) {
                offset = -offset
            }

            reference.add(calScope, offset)

            date = reference.time
        } else if (input == QUERY_EMPTY || input == QUERY_NOT_EMPTY) {
            date = null
        } else {
            date = DateUtils.parseDate(input, reference?.getTimeZone())
        }
        date
    }

    private static Closure dateCriteria = { Object fromQuery, Object toQuery,
                                            String startProp, String endProp,
                                            TimeZone timezone ->
        Date fromDate
        Date toDate
        if (fromQuery) {
            if (fromQuery instanceof Date) {
                fromDate = fromQuery
            } else {
                fromDate = parseDateQuery(fromQuery.toString(), Calendar.getInstance(timezone))
            }
            if (fromDate) {
                fromDate = fromDate.clearTime() // 00:00:00
            } else if (fromQuery == QUERY_EMPTY) {
                isNull(startProp)
            } else if (fromQuery == QUERY_NOT_EMPTY) {
                isNotNull(startProp)
            }
        }
        if (toQuery) {
            if (toQuery instanceof Date) {
                toDate = toQuery
            } else {
                toDate = parseDateQuery(toQuery.toString(), Calendar.getInstance(timezone))
            }
            if (toDate) {
                toDate = DateUtils.getDateSpan(toDate)[1] // 23:59:59
            } else if (endProp && (toQuery == QUERY_EMPTY)) {
                isNull(endProp)
            } else if (endProp && (toQuery == QUERY_NOT_EMPTY)) {
                isNotNull(endProp)
            }
        }

        if (fromDate && toDate) {
            if (endProp) {
                or {
                    between(startProp, fromDate, toDate)
                    between(endProp, fromDate, toDate)
                }
            } else {
                between(startProp, fromDate, toDate)
            }
        } else if (fromDate) {
            if (endProp) {
                or {
                    ge(startProp, fromDate)
                    gt(endProp, fromDate)
                }
            } else {
                ge(startProp, fromDate)
            }
        } else if (toDate) {
            if (endProp) {
                or {
                    lt(startProp, toDate)
                    le(endProp, toDate)
                }
            } else {
                le(startProp, toDate)
            }
        }
    }

    public static void dateQuery(Object criteriaDelegate, Object fromQuery, Object toQuery,
                                 String propertyName, TimeZone timezone) {
        dateQuery(criteriaDelegate, fromQuery, toQuery, propertyName, null, timezone)
    }

    public static void dateQuery(Object criteriaDelegate, Object fromQuery, Object toQuery,
                                 String startProp, String endProp, TimeZone timezone) {
        Closure cl = dateCriteria.clone()
        cl.delegate = criteriaDelegate
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl(fromQuery, toQuery, startProp, endProp, timezone)
    }
}
