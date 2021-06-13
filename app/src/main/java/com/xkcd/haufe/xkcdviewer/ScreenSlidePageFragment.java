package com.xkcd.haufe.xkcdviewer;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.nodes.Document;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Comic;
import retrofit.IXkcdAPI;
import uk.co.senab.photoview.PhotoView;
import utils.Common;
import utils.PicassoImageLoadingService;


public class ScreenSlidePageFragment extends Fragment {
    String comicElement, title, altText, prevComicNumm;
    int comicNumber;
    TextView titleTv, comicNumTv;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IXkcdAPI iXkcdAPI;
    PhotoView mComicImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.screen_slide_page, container, false);

        mComicImage = rootView.findViewById(R.id.iv_comic);
        titleTv = rootView.findViewById(R.id.comicTitleTV);
        comicNumTv = rootView.findViewById(R.id.comicNumTv);
        mComicImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
                    builder.setCancelable(true);
                    builder.setMessage(altText);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iXkcdAPI = Common.getAPI();
        fetchComic(comicNumber);
    }

    private void fetchComic(int comicNumber) {

        compositeDisposable.add(iXkcdAPI.getComic(comicNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                               @Override
                               public void accept(Comic comics) throws Exception {
                                   new PicassoImageLoadingService().loadImage(comics.getImageUrl(), mComicImage);

                                   titleTv.setText(comics.getTitle());
                                   altText = comics.getSubTitle();
                                   comicNumTv.setText(comics.getNumber().toString());
                               }
                           }
                        , new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(getActivity().getApplicationContext(), "Error loading comics1", Toast.LENGTH_SHORT).show();
                            }
                        }));
    }

}
