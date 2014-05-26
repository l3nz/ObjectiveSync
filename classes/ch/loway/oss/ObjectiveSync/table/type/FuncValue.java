
package ch.loway.oss.ObjectiveSync.table.type;

/**
 *
 *
 * $Id$
 * @author lenz
 */
public class FuncValue extends TableValue {

    private final String func;

    @Override
    public String embeddableValue() {
        return func;
    }

    public FuncValue(String func) {
        this.func = func;
    }
}

// $Log$
//
