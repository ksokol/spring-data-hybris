package org.springframework.data.hybris.dao.support;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;

/**
 * @author Kamill Sokol
 */
public class HybrisPersistenceExceptionTranslator implements PersistenceExceptionTranslator {
    @Override
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        //TODO implemented me. this is just a stub right now
        return new UndefinedDataAccessException(ex.getMessage(), ex);
    }

    public class UndefinedDataAccessException extends DataAccessException {
        public UndefinedDataAccessException(String msg, Throwable t) {
            super(msg);
        }
    }
}
