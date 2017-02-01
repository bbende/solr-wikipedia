package org.solr.wikipedia.handler;

public interface PageHandler<T> {

    public void startPage();

    public void title(String title);

    public void startRevision();

    public void timestamp(String timestamp);

    public void text(String text);

    public void endRevision();
    
    public void startContributor();

    public void id(String id);

    public void username(String username);

    public void endContributor();

    public T endPage();

}
