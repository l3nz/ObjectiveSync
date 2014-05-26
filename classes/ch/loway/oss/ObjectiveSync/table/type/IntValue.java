
package ch.loway.oss.ObjectiveSync.table.type;

/**
 *
 *
 * $Id$
 * @author lenz
 */
public class IntValue extends TableValue {
    private final int val;

    @Override
    public String embeddableValue() {
        return Integer.toString(val);
    }

    public IntValue(int val) {
        this.val = val;
    }
}

// $Log$
//
