
package ch.loway.oss.ObjectiveSync.maps;

import java.util.HashSet;
import java.util.Set;

/**
 * This class matches an organization that might include one or more
 * persons.
 *
 * @author lenz
 */
public class Organization {

    public int id = 0;
    public String name = "";
    public Set<Person> members = new HashSet<Person>();

    /**
     * Builds this object.
     * 
     * @param id
     * @param name
     * @return
     */

    public static Organization build( int id, String name ) {
        Organization o = new Organization();
        o.id = id;
        o.name = name;
        return o;
    }

    /**
     * Add a member to this org.
     * 
     * @param p
     */
    public void addMember( Person p ) {
        members.add(p);
    }

}

