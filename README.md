# GR8 CRM - Core Plugin

CRM = [Customer Relationship Management](http://en.wikipedia.org/wiki/Customer_relationship_management)

GR8 CRM is a set of [Grails Web Application Framework](http://www.grails.org/)
plugins that makes it easy to develop web application with CRM functionality.
With CRM we mean features like:

- Contact Management
- Task/Todo Lists
- Project Management
- Document Management

The GR8 CRM "Ecosystem" currently contains over 40 Grails plugins. For a complete list of plugins see http://gr8crm.github.io

Each GR8 CRM plugin defines a [Bounded Context](http://martinfowler.com/bliki/BoundedContext.html)
that focus on one specific domain, for example *contact*, *project* or *document*.
A GR8 CRM plugin have minimal dependencies on other GR8 CRM plugins. However there are some common features that most plugins need.
Like working with date/time, caching and multi-tenancy features. Such common features are provided by the *crm-core* plugin.

Read more about the *crm-core* plugin at [gr8crm.github.io](http://gr8crm.github.io/plugins/crm-core/crm-core.html)