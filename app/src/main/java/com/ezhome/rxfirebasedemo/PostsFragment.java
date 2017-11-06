package com.ezhome.rxfirebasedemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.ezhome.rxfirebase2.database.RxFirebaseDatabase;
import com.ezhome.rxfirebasedemo.model.BlogPostEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Subscriber;

/**
 * The {@link Fragment} to show a list with posts
 */
public class PostsFragment extends Fragment {

  //For example purposes
  private final DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();

  //Adapter
  private BlogPostsAdapter blogPostsAdapter;

  @BindView(R.id.rvPostsList) RecyclerView rvPostsList;
  @BindView(R.id.progressBar) ProgressBar progressBar;

  /**
   * Factory method to instantiate Fragment
   *
   * @return {@link PostsFragment}
   */
  public static PostsFragment newInstance() {
    return new PostsFragment();
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View fragmentView = inflater.inflate(R.layout.fragment_posts, container, false);
    ButterKnife.bind(this, fragmentView);
    return fragmentView;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.blogPostsAdapter = new BlogPostsAdapter(Collections.<BlogPostEntity>emptyList());
    this.rvPostsList.setLayoutManager(new LinearLayoutManager(getContext()));
    this.rvPostsList.setAdapter(blogPostsAdapter);
    this.loadPosts();
  }

  /**
   * Load the posts
   */
  private void loadPosts() {
    PostsFragment.this.showProgress(true);
    RxFirebaseDatabase.getInstance().observeValueEvent(firebaseRef.child("fireblog")).subscribe(new GetPostsSubscriber());
  }

  /**
   * Renders in UI the available {@link BlogPostEntity}
   *
   * @param blogPostEntities {@link List} of {@link BlogPostEntity}
   */
  private void renderBlogPosts(List<BlogPostEntity> blogPostEntities) {
    this.showProgress(false);
    this.blogPostsAdapter.setData(blogPostEntities);
  }

  /**
   * Shows the progress bar
   *
   * @param isVisible {@link Boolean} VISIBLE: true | INVISIBLE: false
   */
  private void showProgress(boolean isVisible) {
    this.progressBar.clearAnimation();
    this.progressBar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    this.getActivity().setProgressBarIndeterminateVisibility(isVisible);
  }

  /**
   * Shows the error in UI
   *
   * @param message {@link String} the error message
   */
  private void showError(String message) {
    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
  }

  /**
   * Subscriber for {@link RxFirebaseDatabase} query
   */
  private final class GetPostsSubscriber extends Subscriber<DataSnapshot> {
    @Override public void onCompleted() {
      PostsFragment.this.showProgress(false);
    }

    @Override public void onError(Throwable e) {
      PostsFragment.this.showProgress(false);
      PostsFragment.this.showError(e.getMessage());
    }

    @SuppressWarnings("unchecked") @Override public void onNext(DataSnapshot dataSnapshot) {
      List<BlogPostEntity> blogPostEntities = new ArrayList<>();
      for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
        blogPostEntities.add(childDataSnapshot.getValue(BlogPostEntity.class));
      }
      PostsFragment.this.renderBlogPosts(blogPostEntities);
    }
  }
}
