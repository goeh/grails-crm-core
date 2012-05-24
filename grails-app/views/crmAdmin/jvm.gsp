<%@ page import="grails.plugins.crm.core.TenantUtils; java.lang.management.ManagementFactory" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><g:message code="crmAdmin.jvm.title" default="Java Virtual Machine Statistics"/></title>
</head>

<body>
<h1><g:message code="crmAdmin.jvm.title" default="Java Virtual Machine Statistics"/> <small>${new Date()}</small></h1>

<fieldset>
    <legend>System Information</legend>
    <b>Uptime:</b> ${grails.plugins.crm.core.DateUtils.formatDuration(new Date(ManagementFactory.getRuntimeMXBean().getUptime()))}<br/>
    <b>Grails version:</b> <g:meta name="app.grails.version"></g:meta><br/>
    <b>Groovy version:</b> ${org.codehaus.groovy.runtime.InvokerHelper.getVersion()}<br/>
    <b>JVM version:</b> ${System.getProperty('java.version')}<br/>
    <b>Tenant:</b> ${TenantUtils.tenant}<br/>
</fieldset>
<fieldset>
    <legend>Memory MXBean</legend>
    <b>Heap Memory Usage:</b> <%=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage()%><br/>
    <b>Non-Heap Memory Usage:</b><%=ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()%><br/>
</fieldset>

<fieldset>
    <legend>Memory Pool MXBeans</legend>
    <g:each in="${ManagementFactory.getMemoryPoolMXBeans()}" var="item">
        <div style="border-bottom: 1px solid #cccccc;padding:1em 0;">
            <b>Name:</b> <%=item.getName()%><br/>
            <b>Type:</b> <%=item.getType()%><br/>
            <b>Usage:</b> <%=item.getUsage()%><br/>
            <b>Peak Usage:</b> <%=item.getPeakUsage()%><br/>
            <b>Collection Usage:</b> <%=item.getCollectionUsage()%><br/>
        </div>
    </g:each>
</fieldset>

</body>
</html>
