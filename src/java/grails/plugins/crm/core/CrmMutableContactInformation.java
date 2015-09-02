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

package grails.plugins.crm.core;

/**
 * Contact information that can be changed (using setters).
 */
public interface CrmMutableContactInformation extends CrmContactInformation {
    void setFirstName(String arg);

    void setLastName(String arg);

    void setCompanyName(String arg);

    void setCompanyId(Long arg);

    void setTitle(String arg);

    void setAddressInformation(CrmAddressInformation arg);

    void setTelephone(String arg);

    void setEmail(String arg);

    void setNumber(String arg);
}
