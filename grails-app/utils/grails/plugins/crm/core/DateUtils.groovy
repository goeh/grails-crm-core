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

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.text.ParseException
import groovy.time.Duration

/**
 * Utility methods for dealing with dates.
 * @author goran
 * @since 0.1
 * @todo refactor this ugly date utility that is hard coded for Swedish locale.
 */
abstract class DateUtils {
    private DateUtils() {}

    static DATE_FORMAT = 'yyyy-MM-dd' // Formatting
    static DATE_FORMATS = ['yyyy-MM-dd', 'yyyyMMdd', 'yyMMdd'] // Parsing
    static DATETIME_FORMAT = 'yyyy-MM-dd HH:mm' // Formatting
    static DATETIME_FORMATS = ['yyyy-MM-ddHH:mm', 'yyyyMMddHHmm', 'yyMMddHHmm', 'yyyy-MM-dd', 'yyyyMMdd', 'yyMMdd'] // Parsing

    private static final TimeZone UTC = TimeZone.getTimeZone('UTC')
    private static final Locale SWEDISH = new Locale('sv', 'SE')

    static java.sql.Date parseSqlDate(String input, TimeZone tz = UTC) {
        Date date = parseDate(input, tz)
        return date != null ? new java.sql.Date(date.getTime()) : null
    }

    static String format(Date date, String format, TimeZone tz = UTC) {
        def fmt = new SimpleDateFormat(format, SWEDISH)
        fmt.timeZone = tz
        return fmt.format(date)
    }

    static String formatDate(Date date, TimeZone tz = UTC) {
        def fmt = new SimpleDateFormat(DATE_FORMAT, SWEDISH)
        fmt.timeZone = tz
        return fmt.format(date)
    }

    static String formatDate(String input, TimeZone tz = UTC) {
        def rval = ''
        if(input) {
            try {
                rval = formatDate(parseDate(input, tz), tz)
            } catch(ParseException e) {
                rval = "${input}!"
            }
        }
        return rval
    }

    static Date parseDate(String input, TimeZone tz = UTC) {
        Date date = null
        if(input) {
            input = input.replace(' ', ''); // Remove all spaces.
            Exception firstError = null
            DATE_FORMATS.each{fmt->
                if(date == null) {
                    try {
                        DateFormat df = new SimpleDateFormat(fmt, SWEDISH)
                        df.timeZone = tz
                        df.setLenient(false)
                        date = df.parse(input)
                    } catch(ParseException e) {
                        if(! firstError) {
                            firstError = e
                        }
                    }
                }
            }
            if(date == null) {
                throw firstError
            }
        }
        return date
    }

    static String formatDateTime(Date date, TimeZone tz = UTC) {
        def fmt = new SimpleDateFormat(DATETIME_FORMAT, SWEDISH)
        fmt.timeZone = tz
        return fmt.format(date)
    }

    static String formatDateTime(String input, TimeZone tz = UTC) {
        def rval = ''
        if(input) {
            try {
                rval = formatDateTime(parseDateTime(input, tz), tz)
            } catch(ParseException e) {
                rval = "${input}!"
            }
        }
        return rval
    }

    static Date parseDateTime(String input, TimeZone tz = UTC) {
        Date date = null
        if(input) {
            input = input.replace(' ', ''); // Remove all spaces.
            Exception firstError = null
            DATETIME_FORMATS.each{fmt->
                if(date == null) {
                    try {
                        DateFormat df = new SimpleDateFormat(fmt, SWEDISH)
                        df.timeZone = tz
                        df.setLenient(false)
                        date = df.parse(input)
                    } catch(ParseException e) {
                        if(! firstError) {
                            firstError = e
                        }
                    }
                }
            }
            if(date == null) {
                throw firstError
            }
        }
        return date
    }

    static Date duration(Date dateFrom, Date dateTo) {
        def d = null
        if(dateFrom && dateTo) {
            use( groovy.time.TimeCategory ){
                d = (dateTo - dateFrom).toMilliseconds()
            }
        }
        return d
    }

    static String formatDuration(Date dateFrom, Date dateTo, boolean includeSeconds = false) {
        formatDuration(duration(dateFrom, dateTo), includeSeconds)
    }

    static String formatDuration(Date date, boolean includeSeconds = false){
        StringBuilder buf = new StringBuilder()
        if(date != null) {
            def y = date.year - 70
            def d = format(date, "D")
            def h = format(date, "H")
            def m = format(date, "m")
            def s = format(date, "s")
            if(d != '1') {
                buf << "${Integer.parseInt(d) - 1 + (365*y)} dygn"
            }
            if(h != '0') {
                if(buf.length() > 0) {
                    buf << ' '
                }
                buf << "${h} tim"
            }
            if(m != '0') {
                if(buf.length() > 0) {
                    buf << ' '
                }
                buf << "${m} min"
            }
            if(includeSeconds && (s != '0')) {
                if(buf.length() > 0) {
                    buf << ' '
                }
                buf << "${s} sek"
            }
        }
        return buf.toString()
    }

    static BigDecimal getExcelDuration(Date date) {
        if(date == null) {
            return null
        }
        def y = date.year - 70
        def d = format(date, "D")
        def h = format(date, "H")
        def m = format(date, "m")
        def days = d == '1' ? 0 : Integer.valueOf(d) - 1 + (365*y)
        def hours = Integer.valueOf(h) / 24 + Integer.valueOf(m) / 1440
        return days + hours
    }

    static String formatDuration(String input) {
        String rval
        try {
            Date date = parseDuration(input)
            rval = date ? formatDuration(date) : ''
        } catch(Exception e) {
            System.err.println(e.message)
            rval = "${input}!"
        }
        return rval
    }

    static Date parseDuration(String input) {
        Date rval = null
        if(input != null) {
            input = input.trim()
            if(input.length() == 0 || input == '0') {
                return rval
            }
            def arr = [0, 0, 0]
            def rx = /(\d+)\s*([ydthm])/
            def m = input =~ rx
            def found = false
            while(m.find()) {
                def match = m.group(2)
                if(match == 'd') {
                    arr[0] += Integer.valueOf(m.group(1))
                    found = true
                } else if(match == 't' || m.group(2) == 'h') {
                    arr[1] = Integer.valueOf(m.group(1))
                    found = true
                } else if(match == 'm') {
                    arr[2] = Integer.valueOf(m.group(1))
                    found = true
                } else if(match == 'y') {
                    arr[0] += (Integer.valueOf(m.group(1)) * 365)
                    found = true
                }
            }
            if(! found) {
                arr = input.split(/\D+/).collect{Integer.valueOf(it)}
                while(arr.size() < 3) {
                    arr.add(0, 0)
                }
            }
            def y = 0
            if(arr[0] > 364) {
                y = (arr[0] / 365).intValue()
                arr[0] = arr[0] % 365
            }
            if((arr[0] + arr[1] + arr[2]) > 0) {
                use( groovy.time.TimeCategory ){
                    rval = new Date(new Duration(arr[0], arr[1], arr[2], 0, 0 ).toMilliseconds())
                }
            }
        }
        return rval
    }

    static def getDateSpan(date) {
        def cal = Calendar.getInstance()
        cal.setTime(date)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        def date1 = cal.getTime()
        cal.add(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.SECOND, -1); // TODO This is a test, -1 MILLISECOND works on MySQL but not on MSSQL.
        def date2 = cal.getTime()

        return [date1, date2]
    }

    static boolean isBefore(date1, date2) {
        if(! date1) {
            return false
        }
        return date2 && date1.before(date2)
    }

    static Date startOfWeek(Integer dayOffset = 0, Date date = new Date()) {
        def cal = Calendar.getInstance()
        cal.setTime(date)
        def firstDayOfWeek = cal.getFirstDayOfWeek()
        if(dayOffset != 0) {
            cal.add(Calendar.DAY_OF_MONTH, dayOffset)
        }
        while(cal.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek ) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    static Date endOfWeek(Integer dayOffset = 0, Date date = new Date()) {
        def cal = Calendar.getInstance()
        cal.setTime(date)
        def firstDayOfWeek = cal.getFirstDayOfWeek()
        if(dayOffset != 0) {
            cal.add(Calendar.DAY_OF_MONTH, dayOffset)
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == firstDayOfWeek) {
            // If today is first day of week, start our search algorithm tomorrow.
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        while(cal.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        cal.add(Calendar.DAY_OF_MONTH, -1) // Day before first day of week
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }
}

