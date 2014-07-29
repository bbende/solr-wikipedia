package org.solr.wikipedia.model;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

/**
 * Represents a single revision of a page in the WikiMedia XML.
 *
 * @author bbende
 */
public class Revision {

    private final Date timestamp;

    private final String text;

    private Revision(RevisionBuilder builder) {
        this.timestamp = builder.timestamp;
        this.text = builder.text;
        Validate.notNull(this.timestamp);
        Validate.notNull(this.text);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).
                append(text).
                append(timestamp).
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
                .isEquals();
    }


    public static class RevisionBuilder {
        private Date timestamp;
        private String text;

        public RevisionBuilder timestamp(Date date) {
            this.timestamp = date;
            return this;
        }

        public RevisionBuilder text(String text) {
            this.text = text;
            return this;
        }

        public Revision build() {
            return new Revision(this);
        }
    }

}
