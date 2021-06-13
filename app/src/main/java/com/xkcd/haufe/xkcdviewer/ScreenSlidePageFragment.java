package com.xkcd.haufe.xkcdviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

import com.github.chrisbanes.photoview.PhotoView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Comic;
import retrofit.IXkcdAPI;
import utils.Common;
import utils.PicassoImageLoadingService;


public class ScreenSlidePageFragment extends Fragment implements TextToSpeech.OnInitListener {
    private String altText, transcript;
    int comicNumber;
    private TextView titleTv, comicNumTv, dateTv;
    private ImageButton playTextBtn;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IXkcdAPI iXkcdAPI;
    private PhotoView mComicImage;
    private TextToSpeech tts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.screen_slide_page, container, false);
        mComicImage = rootView.findViewById(R.id.iv_comic);
        titleTv = rootView.findViewById(R.id.comicTitleTV);
        comicNumTv = rootView.findViewById(R.id.comicNumTv);
        playTextBtn = rootView.findViewById(R.id.playTextBtn);
        dateTv = rootView.findViewById(R.id.dateTV);
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

        playTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut();
            }
        });

        tts = new TextToSpeech(requireActivity().getApplicationContext(), this);
        iXkcdAPI = Common.getAPI();
        fetchComic(comicNumber);
        Log.d("Comic Number", String.valueOf(comicNumber));

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void fetchComic(int comicNumber) {

        compositeDisposable.add(iXkcdAPI.getComic(comicNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                               @Override
                               public void accept(Comic comic) throws Exception {
                                   new PicassoImageLoadingService().loadImage(comic.getImageUrl(), mComicImage);
                                   setComicDetails(comic);
                               }
                           }
                        , new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(requireActivity().getApplicationContext(), "Error loading comics1", Toast.LENGTH_SHORT).show();
                            }
                        }));
    }

    private void setComicDetails(Comic comic) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(comic.getYear()), Integer.parseInt(comic.getMonth()) - 1,
                Integer.parseInt(comic.getDay()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd yyyy");
        dateFormat.setTimeZone(cal.getTimeZone());
        dateTv.setText(dateFormat.format(cal.getTime()));

        titleTv.setText(comic.getTitle());
        altText = comic.getSubTitle();
        comicNumTv.setText(String.valueOf(comic.getNumber()));
        transcript = comic.getTranscript();
        playTextBtn.setEnabled(!comic.getTranscript().isEmpty());
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
        }, 3000); // the timer will count 5 seconds....
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
