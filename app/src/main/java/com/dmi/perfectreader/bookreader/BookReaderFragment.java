package com.dmi.perfectreader.bookreader;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.dmi.perfectreader.R;
import com.dmi.perfectreader.app.App;
import com.dmi.perfectreader.book.BookFragment;
import com.dmi.perfectreader.bookcontrol.BookControlFragment;
import com.dmi.perfectreader.menu.MenuFragment;
import com.dmi.util.base.BaseFragment;
import com.dmi.util.layout.HasLayout;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.ObjectGraph;
import dagger.Provides;
import me.tatarka.simplefragment.SimpleFragmentIntent;

import static com.google.common.base.Preconditions.checkState;

@HasLayout(R.layout.fragment_book_reader)
public class BookReaderFragment extends BaseFragment {
    @Inject
    protected BookReaderPresenter presenter;

    public static SimpleFragmentIntent<BookReaderFragment> intent(File requestedBookFile) {
        return SimpleFragmentIntent.of(BookReaderFragment.class).putExtra("requestedBookFile", requestedBookFile);
    }

    @Override
    protected ObjectGraph createObjectGraph(ObjectGraph parentGraph) {
        return parentGraph.plus(new Module());
    }

    @Override
    public BookReaderPresenter presenter() {
        return presenter;
    }

    @Override
    public void onViewCreated(@NonNull View view) {
        super.onViewCreated(view);
        if (findChild(R.id.bookContainer) == null) {
            presenter.openBook();
        }
    }

    public void openBook(File bookFile) {
        checkState(findChild(R.id.bookContainer) == null);
        addChild(BookFragment.intent(bookFile), R.id.bookContainer);
        addChild(BookControlFragment.class, R.id.bookControlContainer);
    }

    public void toggleMenu() {
        if (findChild(R.id.menuContainer) == null) {
            addChild(MenuFragment.class, R.id.menuContainer);
        } else {
            removeChild(R.id.menuContainer);
        }
    }

    public void exit() {
        getActivity().finish();
    }

    public BookFragment book() {
        return findChild(R.id.bookContainer);
    }

    public void showNeedOpenThroughFileManager() {
        Toast.makeText(getActivity(), R.string.bookNotLoaded, Toast.LENGTH_SHORT).show();
    }

    @dagger.Module(library = true, addsTo = App.Module.class, injects = {
            BookReaderFragment.class,
            BookReaderPresenter.class,
    })
    public class Module {
        @Provides
        public BookReaderFragment view() {
            return BookReaderFragment.this;
        }

        @Provides
        @Named("requestedBookFile")
        public File presenter() {
            return (File) getIntent().getSerializableExtra("requestedBookFile");
        }
    }
}
