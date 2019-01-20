package com.ct7ct7ct7.androidvimeoplayer.view;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerErrorListener;
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerReadyListener;
import com.ct7ct7ct7.androidvimeoplayer.model.PlayerState;
import com.ct7ct7ct7.androidvimeoplayer.utils.JsBridge;
import com.ct7ct7ct7.androidvimeoplayer.R;
import com.ct7ct7ct7.androidvimeoplayer.utils.Utils;
import com.ct7ct7ct7.androidvimeoplayer.model.VimeoOptions;
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerStateListener;
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerTextTrackListener;
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerTimeListener;
import com.ct7ct7ct7.androidvimeoplayer.listeners.VimeoPlayerVolumeListener;

public class VimeoPlayerView extends FrameLayout implements LifecycleObserver {
    private JsBridge jsBridge;
    private VimeoPlayer vimeoPlayer;
    private ProgressBar progressBar;
    private VimeoOptions defaultOptions;

    public VimeoPlayerView(Context context) {
        this(context, null);
    }

    public VimeoPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VimeoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.jsBridge = new JsBridge(new VimeoPlayerReadyListener() {
            @Override
            public void onReady() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onInitFailed() {
                progressBar.setVisibility(View.GONE);
            }
        });

        defaultOptions = generateDefaultVimeoOptions(context, attrs);
        this.vimeoPlayer = new VimeoPlayer(context);
        this.addView(vimeoPlayer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        this.progressBar = new ProgressBar(context);
        FrameLayout.LayoutParams progressLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        progressLayoutParams.gravity = Gravity.CENTER;
        this.addView(progressBar, progressLayoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //16:9
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            int sixteenNineHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) * 9 / 16, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, sixteenNineHeight);
        } else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addStateListener(VimeoPlayerStateListener stateListener) {
        jsBridge.addStateListener(stateListener);
    }

    public void addTextTrackListener(VimeoPlayerTextTrackListener textTrackListener) {
        jsBridge.addTextTrackListener(textTrackListener);
    }

    public void addTimeListener(VimeoPlayerTimeListener timeListener) {
        jsBridge.addTimeListener(timeListener);
    }

    public void addVolumeListener(VimeoPlayerVolumeListener volumeListener) {
        jsBridge.addVolumeListener(volumeListener);
    }

    public void addErrorListener(VimeoPlayerErrorListener errorListener) {
        jsBridge.addErrorListener(errorListener);
    }

    public void loadVideo(int videoId) {
        vimeoPlayer.loadVideo(videoId);
    }

    public void play() {
        vimeoPlayer.play();
    }

    public void pause() {
        vimeoPlayer.pause();
    }

    public void seekTo(float time) {
        vimeoPlayer.seekTo(time);
    }

    /**
     * @param volume 0.0 to 1.0
     */
    public void setVolume(float volume) {
        if (volume > 1) volume = 1;
        vimeoPlayer.setVolume(volume);
    }

    public void setTopicColor(int color) {
        vimeoPlayer.setTopicColor(Utils.colorToHex(color));
    }

    public void setLoop(boolean loop) {
        vimeoPlayer.setLoop(loop);
    }

    /**
     * @param playbackRate 0.5 to 2
     */
    public void setPlaybackRate(float playbackRate) {
        if (playbackRate > 2) playbackRate = 2;
        if (playbackRate < 0.5) playbackRate = 0.5f;

        vimeoPlayer.setPlaybackRate(playbackRate);
    }

    public float getCurrentTimeSeconds() {
        return jsBridge.currentTimeSeconds;
    }

    public PlayerState getPlayerState() {
        return jsBridge.playerState;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        vimeoPlayer.destroyPlayer();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        vimeoPlayer.pause();
    }

    private VimeoOptions generateDefaultVimeoOptions(Context context, AttributeSet attrs) {
        VimeoOptions options = new VimeoOptions();

        if (!isInEditMode()) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.VimeoPlayerView);

            boolean autoPlay = attributes.getBoolean(R.styleable.VimeoPlayerView_autoPlay, false);
            boolean byline = attributes.getBoolean(R.styleable.VimeoPlayerView_showByline, true);
            boolean loop = attributes.getBoolean(R.styleable.VimeoPlayerView_loop, false);
            boolean muted = attributes.getBoolean(R.styleable.VimeoPlayerView_muted, false);
            boolean playSinline = attributes.getBoolean(R.styleable.VimeoPlayerView_playSinline, true);
            boolean portrait = attributes.getBoolean(R.styleable.VimeoPlayerView_showPortrait, true);
            boolean speed = attributes.getBoolean(R.styleable.VimeoPlayerView_showSpeed, false);
            boolean title = attributes.getBoolean(R.styleable.VimeoPlayerView_showTitle, true);
            boolean transparent = attributes.getBoolean(R.styleable.VimeoPlayerView_transparent, true);
            int color = attributes.getColor(R.styleable.VimeoPlayerView_topicColor, Color.rgb(0, 172, 240));

            options.autoPlay = autoPlay;
            options.byline = byline;
            options.loop = loop;
            options.muted = muted;
            options.playSinline = playSinline;
            options.portrait = portrait;
            options.speed = speed;
            options.title = title;
            options.transparent = transparent;
            options.color = color;
        }

        return options;
    }

    /**
     * @param videoId the video id.
     * @param hashKey if your video is private, you MUST pass the private video hash key.
     * @param baseUrl settings embedded url. e.g. https://yourdomain
     * */
    public void initialize(int videoId, String hashKey, String baseUrl) {
        vimeoPlayer.initialize(jsBridge, defaultOptions, videoId, hashKey, baseUrl);
    }

    /**
     * @param videoId the video id.
     * @param baseUrl settings embedded url. e.g. https://yourdomain
     * */
    public void initialize(int videoId, String baseUrl) {
        vimeoPlayer.initialize(jsBridge, defaultOptions, videoId, null, baseUrl);
    }

    /**
     * @param videoId the video id.
     * */
    public void initialize(int videoId) {
        this.initialize(videoId, null, null);
    }
}
