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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.chrisbanes.photoview.PhotoView;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.xkcd.haufe.xkcdviewer.callbacks.ResultFromCallback;
import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;
import com.xkcd.haufe.xkcdviewer.databinding.ComicLayoutBinding;
import com.xkcd.haufe.xkcdviewer.model.Comic;
import com.xkcd.haufe.xkcdviewer.viewmodel.FavoriteViewModel;
import com.xkcd.haufe.xkcdviewer.utils.PicassoImageLoadingService;
import com.xkcd.haufe.xkcdviewer.viewmodel.ComicViewModel;
import com.xkcd.haufe.xkcdviewer.viewmodel.ComicViewModelFactory;
import com.xkcd.haufe.xkcdviewer.viewmodel.FavoriteViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ComicFragment extends Fragment implements TextToSpeech.OnInitListener {
    private String altText, transcript;
    int mComicNumber;
    private TextView titleTv, comicNumTv, dateTv;
    private ImageButton playTextBtn;
    private PhotoView mComicImage;
    private TextToSpeech tts;
    private ComicLayoutBinding binding;
    private FavoriteViewModel favoriteViewModel;
    private ComicViewModel comicViewModel;
    private boolean isAddedToDb;
    private LikeButton likeButton;
    private Comic currentComic;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = ComicLayoutBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        favoriteViewModel = new ViewModelProvider(this, new FavoriteViewModelFactory(requireActivity().getApplication())).get(FavoriteViewModel.class);
        comicViewModel = new ViewModelProvider(this, new ComicViewModelFactory(requireActivity().getApplication())).get(ComicViewModel.class);
        mComicImage = binding.ivComic;
        titleTv = binding.comicTitleTV;
        comicNumTv = binding.comicNumTv;
        playTextBtn = binding.playTextBtn;
        likeButton = binding.likeButton;
        dateTv = binding.dateTV;
        tts = new TextToSpeech(requireActivity().getApplicationContext(), this);
        playTextBtn.setEnabled(false);

        comicViewModel.getComicByNumber(mComicNumber).observe(getViewLifecycleOwner(), new Observer<Comic>() {
            @Override
            public void onChanged(Comic comic) {
                Log.d("TAG", "LiveData onChanged: " + comic);
                if (comic != null) {
                    setComicDetails(comic);
                }
            }
        });

        Log.d("Comic Number", String.valueOf(mComicNumber));
        checkComicIsFavorite();

        return view;
    }

    private void addToFavorites() {
        FavoriteComic favComic = new FavoriteComic(currentComic);
        favoriteViewModel.insertInDb(favComic);
        Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
    }

    private void deleteFromFavorites() {
        favoriteViewModel.deleteItem(currentComic.getNumber().toString());
        Toast.makeText(requireContext(), "Comic removed from favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void checkComicIsFavorite() {
        favoriteViewModel.isAddedToDb(String.valueOf(mComicNumber), new ResultFromCallback() {
            @Override
            public void setResult(boolean isFavorite) {
                if (isFavorite) {
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


    private void setComicDetails(Comic comic) {
        currentComic = comic;
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(comic.getYear()), Integer.parseInt(comic.getMonth()) - 1,
                Integer.parseInt(comic.getDay()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy", Locale.US);
        dateFormat.setTimeZone(cal.getTimeZone());
        dateTv.setText(dateFormat.format(cal.getTime()));
        new PicassoImageLoadingService().loadImage(comic.getImageUrl(), mComicImage);

        titleTv.setText(comic.getTitle());
        altText = comic.getSubTitle();
        comicNumTv.setText(String.valueOf(comic.getNumber()));
        transcript = comic.getTranscript();
        playTextBtn.setEnabled(!transcript.isEmpty());

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
                addToFavorites();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                deleteFromFavorites();
            }
        });

        playTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut();
            }
        });
    }

    private void showAltTextDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireView().getContext());
        builder.setCancelable(true);
        builder.setMessage(altText);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss();
                timer.cancel(); //this will cancel the timer of the system
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
