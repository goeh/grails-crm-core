/*
 * Copyright 2014 Goran Ehrsson.
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
package grails.plugins.crm.core;

import groovy.transform.CompileStatic;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author Goran Ehrsson
 */
@CompileStatic
public class CustomDateEditor extends org.springframework.beans.propertyeditors.CustomDateEditor {

    private boolean sql;

    public static String[] DATE_FORMATS = new String[]{"yyyy-MM-dd", "yyyyMMdd", "yyMMdd"};

    public CustomDateEditor(final DateFormat dateFormat, final boolean allowEmpty, final int exactDateLength) {
        super(dateFormat, allowEmpty, exactDateLength);
    }

    public CustomDateEditor(final DateFormat dateFormat, final boolean allowEmpty) {
        super(dateFormat, allowEmpty);
    }

    public CustomDateEditor(final DateFormat dateFormat, final boolean allowEmpty, final boolean sqlDate) {
        super(dateFormat, allowEmpty);
        sql = sqlDate;
    }

    @Override
    public void setAsText(final String text) throws IllegalArgumentException {
        if (!StringUtils.hasText(text)) {
            setValue(null); // Treat empty String as null value.
        } else {
            Exception error = null;
            java.util.Date date = null;
            for (int i = 0; i < DATE_FORMATS.length; i++) {
                try {
                    DateFormat df = new SimpleDateFormat(DATE_FORMATS[i]);
                    df.setLenient(false);
                    date = df.parse(text);
                } catch (ParseException ex) {
                    if (error == null) {
                        error = ex;
                    }
                }
                if(date != null) {
                    setValue(sql ? new java.sql.Date(date.getTime()) : date);
                    error = null;
                    break; // We've found a valid date.
                }
            }
            if (error != null) {
                throw new IllegalArgumentException("Could not parse date: " + error.getMessage());
            }
        }
    }
}
