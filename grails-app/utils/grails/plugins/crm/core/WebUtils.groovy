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

import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.web.util.WebUtils as GWU

/**
 * Utility methods for the web layer.
 * @author Goran Ehrsson
 * @since 0.1
 */
@CompileStatic
final class WebUtils {
    private WebUtils() {}

    static void noCache(HttpServletResponse response) {
        final DateFormat fmt = new SimpleDateFormat("EEE, d MMM yyyy '12:00:00 GMT'", Locale.US)
        response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0")
        response.setHeader("Expires", fmt.format(new Date() - 1)) // Expired yesterday
    }

    /* By default, Tomcat will set headers on any SSL content to deny
     * caching. This will cause downloads to Internet Explorer to fail. So
     * we override Tomcat's default behavior here.
     */
    static void shortCache(final HttpServletResponse response) {
        response.setHeader("Pragma", "")
        response.setHeader("Cache-Control", "private,no-store,max-age=60")
        final Calendar cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 2)
        response.setDateHeader("Expires", cal.getTimeInMillis())
    }

    static void defaultCache(final HttpServletResponse response) {
        response.setHeader("Pragma", "")
        response.setHeader("Cache-Control", "public,max-age=600")
        final Calendar cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 10)
        response.setDateHeader("Expires", cal.getTimeInMillis())
    }

    static void attachmentHeaders(final HttpServletResponse response, final String mimetype, final String filename) {
        response.setHeader("Content-disposition", "attachment; filename=${filename}")
        response.contentType = mimetype
        shortCache(response)
    }

    static void inlineHeaders(HttpServletResponse response, final String mimetype, final String filename) {
        response.setHeader("Content-disposition", "inline; filename=${filename}")
        response.contentType = mimetype
        shortCache(response)
    }

    static void renderFile(final HttpServletResponse response, final File file, String encoding = 'UTF-8') {
        response.setContentLength(file.length().intValue())
        response.setCharacterEncoding(encoding)
        file.withInputStream {InputStream is ->
            OutputStream out = response.outputStream
            out << is
            out.flush()
        }
    }

    static void withResponseWriter(final HttpServletResponse response, Object encodingOrClosure, Closure closure = null) {
        String encoding
        if (closure == null && encodingOrClosure instanceof Closure) {
            closure = (Closure)encodingOrClosure
            encoding = 'UTF-8'
        } else {
            encoding = encodingOrClosure.toString()
        }
        final File tempFile = File.createTempFile('response', '.tmp')
        tempFile.deleteOnExit()
        Writer outs = new OutputStreamWriter(new FileOutputStream(tempFile), encoding)
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
    static String bytesFormatted(final Number b) {
        if (b < 1024) {
            return b.toString() + ' B'
        } else if (b > (1024 * 10000)) {
            return "${(b / 1024000 + 0.512).intValue()} MB"
        }
        return "${(b / 1024 + 0.512).intValue()} kB"
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
        final Cookie cookie = request.cookies.find {Cookie c-> c.name == cookieName}
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
        final HttpSession session = request.getSession(true)
        Map<Long, Map<String, Serializable>> tenants = (Map<Long, Map<String, Serializable>>)session.getAttribute('TENANTS')
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
        final HttpSession session = request.getSession(true)
        final Map<Long, Map<String, Serializable>> tenants = (Map<Long, Map<String, Serializable>>)session.getAttribute('TENANTS')
        if (!tenants) {
            tenants = [:]
            session.setAttribute('TENANTS', tenants)
        }
        final Map tenantData = tenants[tenant]
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

