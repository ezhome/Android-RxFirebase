package com.ezhome.rxfirebasedemo;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.ezhome.rxfirebasedemo.model.BlogPostEntity;
import java.util.List;

/**
 * The {@link RecyclerView.Adapter} with a {@link List} of {@link BlogPostEntity}
 */
class BlogPostsAdapter extends RecyclerView.Adapter<BlogPostViewHolder> {

  private List<BlogPostEntity> blogPostEntities;

  public BlogPostsAdapter(List<BlogPostEntity> blogPostEntities) {
    this.blogPostEntities = blogPostEntities;
  }

  @Override public BlogPostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new BlogPostViewHolder(parent);
  }

  @Override public void onBindViewHolder(BlogPostViewHolder holder, int position) {
    BlogPostEntity blogPostEntity = blogPostEntities.get(position);
    holder.bindModel(blogPostEntity);
  }

  @Override public int getItemCount() {
    return blogPostEntities.size();
  }

  /**
   * Sets the data for adapter
   *
   * @param blogPostEntities a {@link List} of {@link BlogPostEntity}
   */
  public void setData(List<BlogPostEntity> blogPostEntities) {
    this.validateData(blogPostEntities);
    this.blogPostEntities = blogPostEntities;
    this.notifyDataSetChanged();
  }

  /**
   * Validates the data
   *
   * @param blogPostEntities a {@link List} of {@link BlogPostEntity}
   */
  public void validateData(List<BlogPostEntity> blogPostEntities) {
    if (blogPostEntities == null) {
      throw new IllegalArgumentException("The list cannot be null");
    }
  }
}
