<%@ page import="grails.plugins.crm.core.TenantUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title><g:message code="crmAdmin.index.title" default="System Administration"/></title>
</head>

<body>

<header class="page-header">
    <h1><g:message code="crmAdmin.index.title"/> <small><g:message code="crmAdmin.index.subtitle"/></small></h1>
</header>

<div class="row-fluid">

    <div class="span4">

        <h3><g:message code="crmAdmin.framework.label" default="Framework"/></h3>

        <table class="table table-striped table-bordered table-condensed">
            <thead>
            <tr><th>Parameter</th><th>Value</th></tr>
            </thead>
            <tbody>
            <tr><td>App version</td><td><g:meta name="app.version"></g:meta></td></tr>
            <tr><td>Grails version</td><td><g:meta name="app.grails.version"></g:meta></td></tr>
            <tr><td>Groovy version</td><td>${groovy.lang.GroovySystem.getVersion()}</td></tr>
            <tr><td>JVM version</td><td>${System.getProperty('java.version')}</td></tr>
            <tr><td>Tenant</td><td>${TenantUtils.tenant}</td></tr>
            </tbody>
        </table>

    </div>

    <div class="span4">

        <h3><g:message code="crmAdmin.features.label" default="Installed Features"/></h3>

        <table class="table table-striped table-bordered table-condensed">
            <g:if test="${features}">
                <thead><tr><th>Feature</th><th>Description</th></tr></thead>
            </g:if>
            <tbody>
            <g:each var="f" in="${features}">
                <tr><td>${f.name}</td><td><crm:featureLink feature="${f.name}" enabled="true" nolink="true"/></td></tr>
            </g:each>
            <g:unless test="${features}">
                <tr><td>No features installed</td></tr>
            </g:unless>
            </tbody>
        </table>

    </div>

    <div class="span4">

        <h3><g:message code="crmAdmin.plugins.label" default="Installed Plugins"/></h3>

        <table class="table table-striped table-bordered table-condensed">
            <thead>
            <tr><th>Plugin</th><th>Version</th></tr>
            </thead>
            <tbody>
            <g:set var="pluginManager"
                   value="${applicationContext.getBean('pluginManager')}"></g:set>
            <g:each var="plugin" in="${pluginManager.allPlugins.sort{it.name} }">
                <tr><td>${plugin.name}</td><td>${plugin.version}</td></tr>
            </g:each>
            </tbody>
        </table>

    </div>
</div>

</body>
</html>
