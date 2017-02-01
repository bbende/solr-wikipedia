package org.solr.wikipedia.model;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 * Represents a single revision of a page in the WikiMedia XML.
 *
 * @author johncall
 */
public class Contributor {

    private final int id;

    private final String username;

    private Contributor(ContributorBuilder builder) {
        this.id = builder.id;
        this.username = builder.username;
        // Username can be null because of ip only Contributors
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(id).
                append(username).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Contributor that = (Contributor) obj;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.username, that.username)
                .isEquals();
    }


    public static class ContributorBuilder {
        private int id;
        private String username;

        public ContributorBuilder id(int id) {
            this.id = id;
            return this;
        }

        public ContributorBuilder username(String username) {
            this.username = username;
            return this;
        }

        public Contributor build() {
            return new Contributor(this);
        }
    }

}
