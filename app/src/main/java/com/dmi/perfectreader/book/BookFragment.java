package com.dmi.perfectreader.book;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.bookreader.BookReaderFragment;
import com.dmi.util.base.BaseFragment;
import com.dmi.util.layout.HasLayout;

import java.io.File;

import javax.inject.Inject;

import butterknife.Bind;
import dagger.ObjectGraph;
import dagger.Provides;
import me.tatarka.simplefragment.SimpleFragmentIntent;

@HasLayout(R.layout.fragment_book)
public class BookFragment extends BaseFragment {
    private static final float TIME_FOR_ONE_SLIDE_IN_SECONDS = 0.4F;

    protected File bookFile;

    @Bind(R.id.pageBookView)
    protected PageBookView pageBookView;

    @Inject
    protected BookPresenter presenter;

    public static SimpleFragmentIntent<BookFragment> intent(File bookFile) {
        return SimpleFragmentIntent.of(BookFragment.class).putExtra("bookFile", bookFile);
    }

    @Override
    protected ObjectGraph createObjectGraph(ObjectGraph parentGraph) {
        return parentGraph.plus(new Module());
    }

    @Override
    public BookPresenter presenter() {
        return presenter;
    }

    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        super.onCreate(context, state);
        bookFile = (File) getIntent().getSerializableExtra("bookFile");
        presenter.setBookFile(bookFile);
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        pageBookView.setPageAnimation(new SlidePageAnimation(TIME_FOR_ONE_SLIDE_IN_SECONDS));
        presenter.requestBook();
    }

    public void init(PageBook pageBook) {
        pageBookView.init(pageBook);
    }

    @Override
    public void onResume() {
        presenter().resume();
        pageBookView.onResume();
    }

    @Override
    public void onPause() {
        pageBookView.onPause();
        presenter().pause();
    }

    public void queueEvent(Runnable runnable) {
        pageBookView.queueEvent(runnable);
    }

    public void refresh() {
        if (pageBookView != null) {
            pageBookView.refresh();
        }
    }

    public void goPercent(int percent) {
        pageBookView.goPercent(percent);
    }

    public void goNextPage() {
        pageBookView.goNextPage();
    }

    public void goPreviewPage() {
        pageBookView.goPreviewPage();
    }

    public void showBookLoadingError() {
        Toast.makeText(getActivity(), R.string.bookOpenError, Toast.LENGTH_SHORT).show();
    }

    @dagger.Module(addsTo = BookReaderFragment.Module.class, injects = {
            BookFragment.class,
            BookPresenter.class,
    })
    public class Module {
        @Provides
        public BookFragment view() {
            return BookFragment.this;
        }
    }
}
