package grails.plugins.crm.core;

/**
 * Interface that defines contact information like name, telephone, email...
 */
public interface CrmContactInformation {
    String getFirstName();

    String getLastName();

    String getCompanyName();

    String getName();

    String getFullName();

    String getTitle();

    Object getFullAddress();

    String getTelephone();

    String getEmail();

    String getNumber();
}
