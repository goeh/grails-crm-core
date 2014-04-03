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
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;

import java.text.SimpleDateFormat;

/**
 *
 * @author Goran Ehrsson
 */
@CompileStatic
public final class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {

    public void registerCustomEditors(PropertyEditorRegistry registry) {

        // We want all strings to be trimmed and empty strings converted to null.
        registry.registerCustomEditor(String.class, new StringTrimmerEditor(true));

        registry.registerCustomEditor(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
        registry.registerCustomEditor(java.sql.Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, true));

        // you could register as many custom property editors as are required here...
    }
}
