
package ch.loway.oss.ObjectiveSync.maps;

/**
 *
 *
 * $Id$
 * @author lenz
 */
public class Person {

    public int id =0;
    public String name = "";
    public String surname = "";

    public static Person build( int id, String name, String surn) {
        Person p = new Person();
        p.id = 0;
        p.name = name;
        p.surname = surn;
        return p;
    }

}

// $Log$
//
