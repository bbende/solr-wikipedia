package org.solr.wikipedia.model;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single revision of a page in the WikiMedia XML.
 *
 * @author bbende
 */
public class Revision {

    private final Date timestamp;
    
    private final List<Contributor> contributors;

    private final String text;

    private Revision(RevisionBuilder builder) {
        this.timestamp = builder.timestamp;
        this.text = builder.text;
        this.contributors = builder.contributors;
        Validate.notNull(this.timestamp);
        Validate.notNull(this.text);
        Validate.notEmpty(this.contributors);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }
    
    public List<Contributor> getContributors() {
        return this.contributors;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(text).
                append(timestamp).
                append(contributors).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Revision that = (Revision) obj;
        return new EqualsBuilder()
                .append(this.text, that.text)
                .append(this.timestamp, that.timestamp)
                .append(this.contributors, that.contributors)
                .isEquals();
    }


    public static class RevisionBuilder {
        private Date timestamp;
        private String text;
        private List<Contributor> contributors;

        public RevisionBuilder timestamp(Date date) {
            this.timestamp = date;
            return this;
        }

        public RevisionBuilder text(String text) {
            this.text = text;
            return this;
        }
        
        public RevisionBuilder contributor(Contributor contributor) {
            if (this.contributors == null) {
                this.contributors = new ArrayList<>();
            }
            this.contributors.add(contributor);
            return this;
        }

        public Revision build() {
            return new Revision(this);
        }
    }

}
