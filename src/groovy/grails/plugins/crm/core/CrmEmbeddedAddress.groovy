package grails.plugins.crm.core

/**
 * Address class to be used as 'embedded' address in other domain classes.
 */
class CrmEmbeddedAddress extends CrmAddress {

    String addressee

    static constraints = {
        addressee(maxSize: 80, nullable: true, blank: false)
    }

    static transients = ['name'] + CrmAddress.transients

    static final List BIND_WHITELIST = (['addressee'] + CrmAddress.BIND_WHITELIST).asImmutable()

    public CrmEmbeddedAddress() {
    }

    public CrmEmbeddedAddress(CrmAddress copyFrom) {
        copyFrom.copyTo(this)
    }


    transient String getName() {
        addressee
    }

    void setName(String arg) {
        addressee = arg
    }

    transient String getAddress(boolean includePostalCode = true, String delimiter = ', ') {
        def s = new StringBuilder()
        if (addressee) {
            s << addressee
            s << delimiter
        }
        s << super.getAddress(includePostalCode, delimiter)
        s.toString()
    }

    transient Map getDao() {
        def map = super.getDao()
        if(addressee) {
            map.addressee = addressee
        }
        map
    }
}
