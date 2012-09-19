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

import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import java.text.SimpleDateFormat
import org.apache.commons.lang.StringUtils
import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.web.util.WebUtils as GWU

/**
 * Utility methods for the web layer.
 * @author Goran Ehrsson
 * @since 0.1
 */
class WebUtils {
    private WebUtils() {}

    static void noCache(HttpServletResponse response) {
        def fmt = new SimpleDateFormat("EEE, d MMM yyyy '12:00:00 GMT'", Locale.US)
        response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0")
        response.setHeader("Expires", fmt.format(new Date() - 1)) // Expired yesterday
    }

    /* By default, Tomcat will set headers on any SSL content to deny
     * caching. This will cause downloads to Internet Explorer to fail. So
     * we override Tomcat's default behavior here.
     */

    static void shortCache(HttpServletResponse response) {
        response.setHeader("Pragma", "")
        response.setHeader("Cache-Control", "private,no-store,max-age=60")
        Calendar cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 2)
        response.setDateHeader("Expires", cal.getTimeInMillis())
    }

    static void attachmentHeaders(HttpServletResponse response, String mimetype, String filename) {
        response.setHeader("Content-disposition", "attachment; filename=${filename}")
        response.contentType = mimetype
        //response.characterEncoding = "ISO-8859-1"
        shortCache(response)
    }

    static void inlineHeaders(HttpServletResponse response, String mimetype, String filename) {
        response.setHeader("Content-disposition", "inline; filename=${filename}")
        response.contentType = mimetype
        //response.characterEncoding = "ISO-8859-1"
        shortCache(response)
    }

    static void renderFile(def response, File file, String encoding = 'UTF-8') {
        response.setContentLength(file.length().intValue())
        response.setCharacterEncoding(encoding)
        file.withInputStream {is ->
            def out = response.outputStream
            out << is
            out.flush()
        }
    }

    static void withResponseWriter(response, encoding, closure = null) {
        if (closure == null && encoding instanceof Closure) {
            closure = encoding
            encoding = 'UTF-8'
        }
        def tempFile = File.createTempFile('response', '.tmp')
        tempFile.deleteOnExit()
        def outs = new OutputStreamWriter(new FileOutputStream(tempFile), encoding)
        try {
            closure.call(outs)
            outs.flush()
            outs.close()
            outs = null
            response.setContentLength(tempFile.length().intValue())
            response.setCharacterEncoding(encoding)
            tempFile.withInputStream {is ->
                def out = response.outputStream
                out << is
                out.flush()
            }
        } finally {
            outs?.close()
            tempFile.delete()
        }
    }

    /**
     * Returns a human friendly representation of number of bytes.
     * 0-1024 is presented as is
     * 1025-10240000 is presented as kB
     * > 10240000 is presented as MB
     *
     * @param b number of bytes
     * @return human friendly representation (kB, MB)
     */
    static String bytesFormatted(Number b) {
        if (b < 1024) {
            return b.toString()
        } else if (b > (1024 * 10000)) {
            return "${(b / 1024000 + 0.512).intValue()} MB"
        }
        return "${(b / 1024 + 0.512).intValue()} kB"
    }

    static String decorateText(String text, int maxLen = 0) {
        if (text == null) {
            return ''
        }
        if (maxLen > 0 && text.length() > maxLen) {
            text = StringUtils.abbreviate(text, maxLen)
        }
        def decorators = [] // TODO How to add decorators? Grails artifacts!? Answer decorator plugin!!!
        for (d in decorators) {
            text = d.decorateText(text)
        }
        return text.replace('\n', '<br/>\n')
    }

    /**
     * Delete a cookie.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param cookieName name of cookie to delete
     * @return true if the cookie was present before deleting it, false if it was not present
     */
    static boolean deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        def cookie = request.cookies.find {it.name == cookieName}
        if (cookie) {
            cookie.maxAge = 0
            cookie.value = ''
            response.addCookie(cookie)
            return true
        }
        return false
    }

    /**
     * Construct a security message to be displaye din security logs, etc.
     *
     * @param request offending HTTP request
     * @param prefix message prefix (default = "SECURITY")
     * @return String containing requested URL, client IP address, requested tenant and session id
     */
    static String securityMessage(HttpServletRequest request, String prefix = null) {
        if (!prefix) {
            prefix = "SECURITY"
        }
        "$prefix [uri=${GWU.getForwardURI(request)}, ip=${request.remoteAddr}, tenant=${TenantUtils.tenant}, session=${request.session?.id}]"
    }

    /**
     * Return session data associated with a tenant.
     *
     * @param request HTTP request
     * @param key key under which data is stored
     * @param tenant tenant or null/omitted for current tenant
     * @return data referenced by the specified key
     */
    static Serializable getTenantData(HttpServletRequest request, String key, Long tenant = null) {
        if (tenant == null) {
            tenant = TenantUtils.tenant
        }
        HttpSession session = request.getSession(true)
        Map<Long, Map<String, Serializable>> tenants = session.TENANTS
        if (!tenants) {
            return null
        }
        return tenants[tenant]?.get(key)
    }

    /**
     * Set session data associated with a tenant.
     *
     * @param request HTTP request
     * @param key key under which data is to be stored
     * @param data data to store (must be serializable)
     * @param tenant tenant or null/omitted for current tenant
     */
    static void setTenantData(HttpServletRequest request, String key, Serializable data, Long tenant = null) {
        if (tenant == null) {
            tenant = TenantUtils.tenant
        }
        HttpSession session = request.getSession(true)
        Map<Long, Map<String, Serializable>> tenants = session.TENANTS
        if (!tenants) {
            tenants = session.TENANTS = [:]
        }
        Map tenantData = tenants[tenant]
        if (tenantData) {
            if (data != null) {
                tenantData.put(key, data)
            } else {
                tenantData.remove(key) // Don't store null values
            }
        } else if (data != null) {
            tenants.get(tenant, [:]).put(key, data)
        }
    }
}

