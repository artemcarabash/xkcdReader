package com.xkcd.haufe.xkcdviewer.ui.comicsfragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.chrisbanes.photoview.PhotoView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.xkcd.haufe.xkcdviewer.callbacks.ResultFromCallback;
import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;
import com.xkcd.haufe.xkcdviewer.databinding.ComicLayoutBinding;
import com.xkcd.haufe.xkcdviewer.model.Comic;
import com.xkcd.haufe.xkcdviewer.retrofit.IXkcdAPI;
import com.xkcd.haufe.xkcdviewer.ui.favoritesfragment.FavoriteViewModel;
import com.xkcd.haufe.xkcdviewer.utils.Common;
import com.xkcd.haufe.xkcdviewer.utils.InjectorUtils;
import com.xkcd.haufe.xkcdviewer.utils.PicassoImageLoadingService;
import com.xkcd.haufe.xkcdviewer.viewmodel.FavoriteViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ComicFragment extends Fragment implements TextToSpeech.OnInitListener {
    private String altText, transcript;
    int mComicNumber;
    private TextView titleTv, comicNumTv, dateTv;
    private ImageButton playTextBtn;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IXkcdAPI iXkcdAPI;
    private PhotoView mComicImage;
    private TextToSpeech tts;
    private ComicLayoutBinding binding;
    private FavoriteViewModel viewModel;
    private FavoriteViewModelFactory viewModelFactory;
    private boolean isAddedToDb;
    private LikeButton likeButton;
    private Comic currentComic;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = ComicLayoutBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModelFactory = InjectorUtils.provideFavComicViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(this, viewModelFactory).get(FavoriteViewModel.class);

        mComicImage = binding.ivComic;
        titleTv = binding.comicTitleTV;
        comicNumTv = binding.comicNumTv;
        playTextBtn = binding.playTextBtn;
        likeButton = binding.likeButton;
        dateTv = binding.dateTV;
        playTextBtn.setEnabled(false);
        mComicImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    showAltTextDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addToFavs();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                deleteFromFavs();
            }
        });

        playTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut();
            }
        });

        tts = new TextToSpeech(requireActivity().getApplicationContext(), this);
        iXkcdAPI = Common.getAPI();
        fetchComic();
        Log.d("Comic Number", String.valueOf(mComicNumber));
        checkComicIsFav();

        return view;
    }

    private void addToFavs() {
        FavoriteComic favComic = new FavoriteComic(currentComic);
        viewModel.insertInDb(favComic);
        Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
    }

    private void deleteFromFavs() {
        viewModel.deleteItem(currentComic.getNumber().toString());
        // Show a message to the user
        Toast.makeText(requireContext(), "Comic removed from favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void checkComicIsFav() {
        viewModel.isAddedToDb(String.valueOf(mComicNumber), new ResultFromCallback() {
            @Override
            public void setResult(boolean isFav) {
                if (isFav) {
                    isAddedToDb = true;
                    likeButton.setLiked(true);
                    Log.d("ComicFragment", "Item is in the db:" + isAddedToDb);
                } else {
                    isAddedToDb = false;
                    likeButton.setLiked(false);
                    Log.d("ComicFragment", "Item is NOT in the db");
                }
            }
        });
    }

    private void fetchComic() {

        compositeDisposable.add(iXkcdAPI.getComic(mComicNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                               @Override
                               public void accept(Comic comic) throws Exception {
                                   currentComic = comic;
                                   new PicassoImageLoadingService().loadImage(comic.getImageUrl(), mComicImage);
                                   setComicDetails(comic);
                               }
                           }
                        , new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(requireActivity().getApplicationContext(), "Error loading comics", Toast.LENGTH_SHORT).show();
                            }
                        }));
    }


    private void setComicDetails(Comic comic) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(comic.getYear()), Integer.parseInt(comic.getMonth()) - 1,
                Integer.parseInt(comic.getDay()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.US);
        dateFormat.setTimeZone(cal.getTimeZone());
        dateTv.setText(dateFormat.format(cal.getTime()));

        titleTv.setText(comic.getTitle());
        altText = comic.getSubTitle();
        comicNumTv.setText(String.valueOf(comic.getNumber()));
        transcript = comic.getTranscript();
        playTextBtn.setEnabled(!transcript.isEmpty());
    }

    private void showAltTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireView().getContext());
        builder.setCancelable(true);
        builder.setMessage(altText);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
            }
        }, 3000);
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
                playTextBtn.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }

    private void speakOut() {
        tts.speak(transcript, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onPause() {
        if (tts != null) {
            tts.stop();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
