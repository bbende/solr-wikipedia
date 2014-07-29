package org.solr.wikipedia.handler;

import org.solr.wikipedia.model.Page;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.solr.wikipedia.model.Page.PageBuilder;
import static org.solr.wikipedia.model.Revision.RevisionBuilder;

public class DefaultPageHandler implements PageHandler<Page> {

    public static DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'");

    private PageBuilder pageBuilder;

    private RevisionBuilder revisionBuilder;

    @Override
    public void startPage() {
        pageBuilder = new PageBuilder();
    }

    @Override
    public void title(String title) {
        pageBuilder.title(title);
    }

    @Override
    public void startRevision() {
        revisionBuilder = new RevisionBuilder();
    }

    @Override
    public void timestamp(String timestamp) {
        synchronized (TIMESTAMP_FORMAT) {
            try {
                revisionBuilder.timestamp(
                        TIMESTAMP_FORMAT.parse(timestamp));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void text(String text) {
        revisionBuilder.text(text);
    }

    @Override
    public void endRevision() {
        pageBuilder.revision(revisionBuilder.build());
    }

    @Override
    public Page endPage() {
        return pageBuilder.build();
    }
}
