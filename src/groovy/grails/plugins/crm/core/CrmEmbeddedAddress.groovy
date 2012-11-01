package grails.plugins.crm.core

/**
 * Address class to be used as 'embedded' address in other domain classes.
 */
class CrmEmbeddedAddress extends CrmAddress {

    public CrmEmbeddedAddress() {
    }

    public CrmEmbeddedAddress(CrmAddress copyFrom) {
        copyFrom.copyTo(this)
    }
}
