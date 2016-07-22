package com.ezhome.rxfirebasedemo.model;

/**
 * The entity for blog posts
 */
public class BlogPostEntity {

  private String author;

  private String title;

  public BlogPostEntity() {
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override public String toString() {
    final StringBuilder sb = new StringBuilder("BlogPost{");
    sb.append("author='");
    sb.append(author).append('\'');
    sb.append(", title='");
    sb.append(title);
    sb.append('\'');
    sb.append('}');
    return sb.toString();
  }
}
