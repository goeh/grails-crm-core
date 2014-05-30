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
final class DateUtils {
    private DateUtils() {}

    // Formatting
    static String DATE_FORMAT = 'yyyy-MM-dd'
    // Parsing
    static List<String> DATE_FORMATS = ['yyyy-MM-dd', 'yyyyMMdd', 'yyMMdd', 'M/d/yy']
    // Formatting
    static String DATETIME_FORMAT = 'yyyy-MM-dd HH:mm'
    // Parsing
    static List<String> DATETIME_FORMATS = ['yyyy-MM-ddHH:mm', 'yyyyMMddHHmm', 'yyMMddHHmm', 'yyyy-MM-dd', 'yyyyMMdd', 'yyMMdd', 'M/d/yyHH:mm', 'M/d/yy']

    protected static final TimeZone UTC = TimeZone.getTimeZone('UTC')
    protected static final Locale SWEDISH = new Locale('sv', 'SE')

    @CompileStatic
    static java.sql.Date parseSqlDate(final String input, TimeZone tz = UTC) {
        Date date = parseDate(input, tz)
        return date != null ? new java.sql.Date(date.clearTime().getTime()) : null
    }

    @CompileStatic
    @Deprecated
    static String format(final Date date, final String format, TimeZone tz = UTC) {
        final DateFormat fmt = new SimpleDateFormat(format, SWEDISH)
        fmt.timeZone = tz
        fmt.format(date)
    }

    @CompileStatic
    static String formatDate(final Date date, TimeZone tz = UTC) {
        DateFormat fmt = new SimpleDateFormat(DATE_FORMAT, SWEDISH)
        fmt.timeZone = tz
        fmt.format(date)
    }

    @CompileStatic
    static String formatDate(final String input, TimeZone tz = UTC) {
        String rval = ''
        if (input) {
            try {
                final Date d = parseDate(input, tz)
                rval = formatDate(d, tz)
            } catch (ParseException e) {
                rval = "${input}!"
            }
        }
        return rval
    }

    @CompileStatic
    static Date parseDate(String input, TimeZone tz = UTC) {
        Date date = null
        if (input) {
            input = input.replace(' ', ''); // Remove all spaces.
            Exception firstError = null
            DATE_FORMATS.each { final String fmt ->
                if (date == null) {
                    try {
                        DateFormat df = new SimpleDateFormat(fmt, SWEDISH)
                        df.timeZone = tz
                        df.setLenient(false)
                        date = df.parse(input)
                    } catch (ParseException e) {
                        if (!firstError) {
                            firstError = e
                        }
                    }
                }
            }
            if (date == null) {
                throw firstError
            }
        }
        return date
    }

    @CompileStatic
    static String formatDateTime(final Date date, TimeZone tz = UTC) {
        final DateFormat fmt = new SimpleDateFormat(DATETIME_FORMAT, SWEDISH)
        fmt.timeZone = tz
        fmt.format(date)
    }

    @CompileStatic
    static String formatDateTime(final String input, TimeZone tz = UTC) {
        String rval = ''
        if (input) {
            try {
                final Date d = parseDateTime(input, tz)
                rval = formatDateTime(d, tz)
            } catch (ParseException e) {
                rval = "${input}!"
            }
        }
        return rval
    }

    @CompileStatic
    static Date parseDateTime(String input, TimeZone tz = UTC) {
        Date date = null
        if (input) {
            input = input.replace(' ', ''); // Remove all spaces.
            Exception firstError = null
            DATETIME_FORMATS.each { String fmt ->
                if (date == null) {
                    try {
                        DateFormat df = new SimpleDateFormat(fmt, SWEDISH)
                        df.timeZone = tz
                        df.setLenient(false)
                        date = df.parse(input)
                    } catch (ParseException e) {
                        if (!firstError) {
                            firstError = e
                        }
                    }
                }
            }
            if (date == null) {
                throw firstError
            }
        }
        return date
    }

    static Date duration(final Date dateFrom, final Date dateTo) {
        Date d = null
        if (dateFrom && dateTo) {
            use(groovy.time.TimeCategory) {
                Duration dur = (Duration) (dateTo - dateFrom)
                d = new Date(dur.toMilliseconds())
            }
        }
        return d
    }

    @CompileStatic
    static String formatDuration(final Date dateFrom, final Date dateTo, boolean includeSeconds = false) {
        final d = duration(dateFrom, dateTo)
        formatDuration(d, includeSeconds)
    }

    @CompileStatic
    static String formatDuration(final Date date, boolean includeSeconds = false) {
        final StringBuilder buf = new StringBuilder()
        if (date != null) {
            int y = date.year - 70
            String d = format(date, "D")
            String h = format(date, "H")
            String m = format(date, "m")
            String s = format(date, "s")
            if (d != '1') {
                buf << "${Integer.parseInt(d) - 1 + (365 * y)} dygn"
            }
            if (h != '0') {
                if (buf.length() > 0) {
                    buf << ' '
                }
                buf << "$h tim"
            }
            if (m != '0') {
                if (buf.length() > 0) {
                    buf << ' '
                }
                buf << "$m min"
            }
            if (includeSeconds && (s != '0')) {
                if (buf.length() > 0) {
                    buf << ' '
                }
                buf << "$s sek"
            }
        }
        buf.toString()
    }

    @CompileStatic
    static BigDecimal getExcelDuration(final Date date) {
        if (date == null) {
            return null
        }
        def y = date.year - 70
        def d = format(date, "D")
        def h = format(date, "H")
        def m = format(date, "m")
        def days = d == '1' ? 0 : Integer.valueOf(d) - 1 + (365 * y)
        def hours = Integer.valueOf(h) / 24 + Integer.valueOf(m) / 1440
        return days + hours
    }

    @CompileStatic
    static String formatDuration(final String input) {
        String rval
        try {
            Date date = parseDuration(input)
            rval = date ? formatDuration(date) : ''
        } catch (Exception e) {
            System.err.println(e.message)
            rval = "${input}!"
        }
        return rval
    }

    static Date parseDuration(String input) {
        Date rval = null
        if (input != null) {
            input = input.trim()
            if (input.length() == 0 || input == '0') {
                return rval
            }
            def arr = [0, 0, 0]
            def rx = /(\d+)\s*([ydthm])/
            def m = input =~ rx
            def found = false
            while (m.find()) {
                def match = m.group(2)
                if (match == 'd') {
                    arr[0] += Integer.valueOf(m.group(1))
                    found = true
                } else if (match == 't' || m.group(2) == 'h') {
                    arr[1] = Integer.valueOf(m.group(1))
                    found = true
                } else if (match == 'm') {
                    arr[2] = Integer.valueOf(m.group(1))
                    found = true
                } else if (match == 'y') {
                    arr[0] += (Integer.valueOf(m.group(1)) * 365)
                    found = true
                }
            }
            if (!found) {
                arr = input.split(/\D+/).collect { String part -> Integer.valueOf(part) }
                while (arr.size() < 3) {
                    arr.add(0, 0)
                }
            }
            def y = 0
            if (arr[0] > 364) {
                y = (arr[0] / 365).intValue()
                arr[0] = arr[0] % 365
            }
            if ((arr[0] + arr[1] + arr[2]) > 0) {
                use(groovy.time.TimeCategory) {
                    rval = new Date(new Duration(arr[0], arr[1], arr[2], 0, 0).toMilliseconds())
                }
            }
        }
        return rval
    }

    @CompileStatic
    static String formatDuration(final Duration d, Locale locale = null, boolean includeSeconds = false) {
        final StringBuilder s = new StringBuilder()
        if (d != null) {
            if (locale == null) {
                locale = SWEDISH // TODO handle i18n
            }
            if (d.hours) {
                s << d.hours.intValue().toString()
                s << 't'
            }
            if (d.minutes) {
                if (s.length()) {
                    s << ' '
                }
                s << d.minutes.intValue().toString()
                s << 'm'
            }
            if (includeSeconds && d.seconds) {
                if (s.length()) {
                    s << ' '
                }
                s << d.seconds.intValue().toString()
                s << 's'
            }
        }
        s.toString()
    }

    @CompileStatic
    static List<Date> getDateSpan(final Date date) {
        final Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        final Date date1 = cal.getTime()
        cal.add(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.SECOND, -1); // TODO This is a test, -1 MILLISECOND works on MySQL but not on MSSQL.

        [date1, cal.getTime()]
    }

    @CompileStatic
    static boolean isBefore(final Date date1, final Date date2) {
        if (!date1) {
            return false
        }
        date2 && date1.before(date2)
    }

    @CompileStatic
    static Date startOfWeek(Integer dayOffset = 0, Date date = new Date()) {
        final Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        def firstDayOfWeek = cal.getFirstDayOfWeek()
        if (dayOffset != 0) {
            cal.add(Calendar.DAY_OF_MONTH, dayOffset)
        }
        while (cal.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    @CompileStatic
    static Date endOfWeek(Integer dayOffset = 0, Date date = new Date()) {
        Calendar cal = Calendar.getInstance()
        cal.setTime(date)
        def firstDayOfWeek = cal.getFirstDayOfWeek()
        if (dayOffset != 0) {
            cal.add(Calendar.DAY_OF_MONTH, dayOffset)
        }
        if (cal.get(Calendar.DAY_OF_WEEK) == firstDayOfWeek) {
            // If today is first day of week, start our search algorithm tomorrow.
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        while (cal.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        cal.add(Calendar.DAY_OF_MONTH, -1) // Day before first day of week
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }

    static int getFirstDayOfWeek(Locale locale, TimeZone timezone) {
        Calendar.getInstance(timezone, locale).getFirstDayOfWeek()
    }

    static List<String> getMonthNames(Locale locale, TimeZone timezone, boolean shortFormat = false) {
        def calendar = Calendar.getInstance(timezone, locale)
        (0..11).collect {
            calendar.set(Calendar.MONTH, it);
            calendar.getDisplayName(Calendar.MONTH, shortFormat ? Calendar.SHORT : Calendar.LONG, locale)
        }
    }

    static List<String> getDayNames(Locale locale, TimeZone timezone, boolean shortFormat = false) {
        def calendar = Calendar.getInstance(timezone, locale)
        [1, 2, 3, 4, 5, 6, 0].collect {
            calendar.set(Calendar.DAY_OF_WEEK, it);
            calendar.getDisplayName(Calendar.DAY_OF_WEEK, shortFormat ? Calendar.SHORT : Calendar.LONG, locale)
        }
    }
}

