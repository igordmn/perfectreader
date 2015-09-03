package com.dmi.perfectreader.book;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.book.animation.SlidePageAnimation;
import com.dmi.perfectreader.book.pagebook.PageBookView;
import com.dmi.perfectreader.bookreader.BookReaderFragment;
import com.dmi.util.base.BaseFragment;
import com.dmi.util.layout.HasLayout;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import dagger.ObjectGraph;
import dagger.Provides;
import me.tatarka.simplefragment.SimpleFragmentIntent;

@HasLayout(R.layout.fragment_book)
public class BookFragment extends BaseFragment {
    private static final float TIME_FOR_ONE_SLIDE_IN_SECONDS = 0.4F;

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
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        pageBookView.setClient(new PageBookView.Client() {
            @Override
            public void resize(int width, int height) {
                presenter.resize(width, height);
            }

            @Override
            public int synchronizeCurrentPage(int currentPageRelativeIndex) {
                return presenter.synchronizeCurrentPage(currentPageRelativeIndex);
            }
        });
        pageBookView.setPageAnimation(new SlidePageAnimation(TIME_FOR_ONE_SLIDE_IN_SECONDS));
        pageBookView.setRenderer(presenter.createRenderer());
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

    public void refresh() {
        if (pageBookView != null) {
            pageBookView.refresh();
        }
    }

    public int currentPageRelativeIndex() {
        return pageBookView.currentPageRelativeIndex();
    }

    public void reset(Runnable resetter) {
        pageBookView.reset(resetter);
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

        @Provides
        @Named("bookFile")
        public File presenter() {
            return (File) getIntent().getSerializableExtra("bookFile");
        }
    }
}
