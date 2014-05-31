
package ch.loway.oss.ObjectiveSync.updater.deferred;

import ch.loway.oss.ObjectiveSync.updater.deferred.DeferredLoader;
import ch.loway.oss.ObjectiveSync.ObjectiveFetch;

/**
 *
 *
 * @author lenz
 */
public abstract class DeferredLoadByParent<T> extends DeferredLoader<T> {

    String whereClause = "";

    public DeferredLoadByParent<T> setup( ObjectiveFetch<T> fetcher, int pkId ) {
        this.of = fetcher;

        String field = fetcher.table().getParentPk().name;
        whereClause = "WHERE " + field + " = " + pkId;
        return this;
    }

    @Override
    public String getWhereClause() {
        return whereClause;
    }


}

// $Log$
//
