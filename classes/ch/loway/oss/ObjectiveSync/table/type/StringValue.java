
package ch.loway.oss.ObjectiveSync.table.type;

import ch.loway.oss.ObjectiveSync.SqlTools;

/**
 *
 *
 * $Id$
 * @author lenz
 */
public class StringValue extends TableValue {

    private final String val;

    @Override
    public String embeddableValue() {
        return SqlTools.qq(val);
    }

    public StringValue(String val) {
        this.val = val;
    }

    



}

// $Log$
//
