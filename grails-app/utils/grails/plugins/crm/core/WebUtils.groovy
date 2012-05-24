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
import java.text.SimpleDateFormat
import org.apache.commons.lang.StringUtils
import javax.servlet.http.HttpServletRequest

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
        response.characterEncoding = "ISO-8859-1"
        WebUtils.shortCache(response)
    }

    static void inlineHeaders(HttpServletResponse response, String mimetype, String filename) {
        response.setHeader("Content-disposition", "inline; filename=${filename}")
        response.contentType = mimetype
        response.characterEncoding = "ISO-8859-1"
        WebUtils.shortCache(response)
    }

    static String bytesFormatted(b) {
        if (b < 1024) {
            return b.toString()
        } else if (b > (1024 * 10000)) {
            return "${(b / 1024000 + 0.512).intValue()} MB"
        }
        return "${(b / 1024 + 0.512).intValue()} kB"
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

    static String decorateText(String text, int maxLen = 0) {
        if (text == null) {
            return ''
        }
        if (maxLen > 0 && text.length() > maxLen) {
            text = StringUtils.abbreviate(text, maxLen)
        }
        def decorators = [] // TODO How to add decorators?
        for (d in decorators) {
            text = d.decorateText(text)
        }
        return text.replace('\n', '<br/>\n')
    }

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
}

