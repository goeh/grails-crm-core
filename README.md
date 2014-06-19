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
GR8 CRM plugins are allowed to have compile-time dependency on the *crm-core* plugin but should avoid dependency on other GR8 CRM plugins if possible.

## Services

The *crm-core* plugin provide the following services.

### CrmCoreService

**String getReferenceIdentifier(Object object)**

Return a string representation of a domain instance. This string contains both the domain type and the primary key.
The format is **"domainName@primaryKey"**, for example: "customerOrder@42".
This makes it possible to store the reference in a *String* property and re-create the domain instance later.
Because the domain type is stored in a readable form it is also possible to query the property to find
all objects of the same type or query for an exact match on a specific domain instance.

    SomeDomain.findAllByReferenceLike("customerOrder%") // Find all objects that contains a customer order
    SomeDomain.findByReference("customerOrder@42") // Find the object that contains customer order with id 42.
    
**def getReference(String identifier)**

*getReference* is the opposite of *getReferenceIdentifier()* above.
Given a reference identifier the method return a re-constructed domain instance.

    def order = crmCoreService.getReference("customerOrder@42")
    
### CrmPluginService

**void registerView(final String controller, final String action, final String location, final Map params)**

Inject a custom GSP view in an existing GSP view.

**List getViews(final String controller, final String action, final String location)**

Return a list of custom views injected in a GSP view with *registerView()*.

## Utilities

### TenantUtils

This utility class is the most used utility class in GR8 CRM.
It's used to set and get the current executing **tenant** in a multi-tenant environment.

Every plugin in the GR8 CRM suite is multi-tenant aware, this means that multiple users can work in the
same database but they will only see their own information. Every user work in a safe watertight compartment.
But multi-tenancy in GR8 CRM is not implemented at the database (Hibernate) layer. It's implemented in
application logic. This means that the developer is responsible for retrieving information about the current
executing tenant and restrict queries to a tenant.

The reason for this design is that the multi-tenancy support in GR8 CRM extends beyond simple one-one relationship
between a user and a tenant. One user can have access to multiple tenants simultaneously.
A user **always** execute in **one** tenant, but the user may have permission to view information in other tenants.
For example in a calendar view appointments/tasks from multiple tenants could be overlaid on top of each other.
Statistic reports and other "management" type of queries may span multiple tenants.
Therefore it's up to the developer of the application or plugin to decide how a query should be restricted.

**public static Long getTenant()**

Return the ID of current executing tenant.

**public static Object withTenant(Long tenantId, Closure work)**

Execute some work on behalf of a tenant. The tenant will be saved in a ThreadLocal.
The previous tenant will be restored after this method completes.
The return value is the return value of the Closure passed to the method.

**IMPORTANT!** If the Closure spawns a new thread, the tenant ID must be passed to the new thread and the new thread must
call *TenantUtils.withTenant()*. Otherwise the new thread will not execute in a tenant.

HTTP requests to Grails controller actions will automatically execute in a tenant because *CrmTenantFilters*
will intercept the request and set the correct tenant, based on information stored in the user's HTTP session.
So you don't need to use *TenantUtils.withTenant()* in normal controller/service code but for tasks executing
outside of a HTTP request you must, for example in Quartz background jobs.

### DateUtils

**static Date parseDate(String input, TimeZone tz = UTC)**

Parse a date string and return a date instance.

**static String formatDate(final Date date, TimeZone tz = UTC)**

Format a date instance as a String.

### SearchUtils

**static String wildcard(String q)**

Replaces asterisk (*) in the input string with '%'.

### WebUtils

**static void shortCache(final HttpServletResponse response)**

Cache a HTTP response for 2 minutes.

**static void defaultCache(final HttpServletResponse response)**

Cache a HTTP response for 10 minutes.

**static String bytesFormatted(final Number b)**

Returns a human friendly representation of number of bytes.

- 0-1024 is presented as is
- 1025-10240000 is presented as kB
- > 10240000 is presented as MB
     
## Abstract Domain Classes

**CrmLookupEntity**

Lookup entities are domain classes that hold reference/lookup information. For example Customer Type or Project Category.
All lookup entities in GR8 CRM plugins extend *CrmLookupEntity* and get the following properties:

    int orderIndex      // Used for sorting list
    boolean enabled     // False means disabled/do-not-use
    String name         // 80 chars
    String param        // 20 chars
    String icon         // 100 chars
    String description  // 2000 chars
