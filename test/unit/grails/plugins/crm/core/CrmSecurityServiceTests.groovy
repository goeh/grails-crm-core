package grails.plugins.crm.core



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(CrmSecurityService)
class CrmSecurityServiceTests {

    void testGetCurrentUser() {
        def user = service.getCurrentUser()
        assert user.username == 'nobody'
    }

    void testUserIsAuthenticated() {
        assert service.isAuthenticated()
    }

    void testGetCurrentTenant() {
        def tenant = service.getCurrentTenant()
        assert tenant.id == 0
        assert tenant.name == 'Default Tenant'
        assert tenant.owner == 'nobody'
    }

    void testGetTenants() {
        def list = service.getTenants()
        assert list.isEmpty() == false
        def tenant = list.find{it}
        assert tenant != null
        assert tenant.name == 'Default Tenant'
    }
}
