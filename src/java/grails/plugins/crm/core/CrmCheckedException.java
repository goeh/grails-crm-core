package grails.plugins.crm.core;

import java.util.List;

/**
 * Checked exception for use in transactional contexts whe rollback is not wanted.
 */
public class CrmCheckedException extends Exception {

    private List args;

    public CrmCheckedException(String s) {
        super(s);
    }

    public CrmCheckedException(String s, List args) {
        super(s);
        this.args = args;
    }

    public CrmCheckedException(String s, List args, Throwable throwable) {
        super(s, throwable);
        this.args = args;
    }

    public List getArgs() {
        return args;
    }
}
