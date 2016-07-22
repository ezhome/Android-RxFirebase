package com.ezhome.rxfirebasedemo;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ezhome.rxfirebasedemo.model.BlogPostEntity;

import static android.view.View.inflate;

/**
 * The view holder for an item
 */
public class BlogPostViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.postsTitle) TextView postsTitle;
  @BindView(R.id.postsAuthor) TextView postsAuthor;

  public BlogPostViewHolder(ViewGroup parent) {
    super(inflate(parent.getContext(), R.layout.row_post, null));
    ButterKnife.bind(this, itemView);
  }

  public void bindModel(BlogPostEntity entity) {
    if (entity == null) {
      throw new IllegalArgumentException("Entity cannot be null");
    }
    this.postsTitle.setText(entity.getTitle());
    this.postsAuthor.setText(entity.getAuthor());
  }
}
