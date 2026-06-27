package com.example.airplaneludo.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.airplaneludo.R

class GameAudioManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private var clickSoundId: Int = 0
    private var homeSoundId: Int = 0
    private var spawnSoundId: Int = 0
    private var tokenSoundId: Int = 0
    private var killSoundId: Int = 0
    private var diceSoundId: Int = 0
    private var bgMediaPlayer: MediaPlayer? = null

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool?.let { pool ->
            clickSoundId = pool.load(context, R.raw.click, 1)
            homeSoundId = pool.load(context, R.raw.home, 1)
            spawnSoundId = pool.load(context, R.raw.spawn, 1)
            tokenSoundId = pool.load(context, R.raw.token, 1)
            killSoundId = pool.load(context, R.raw.kill, 1)
            diceSoundId = pool.load(context, R.raw.dice, 1)
        }

        try {
            bgMediaPlayer = MediaPlayer.create(context, R.raw.background).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playClick() {
        soundPool?.play(clickSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playHome() {
        soundPool?.play(homeSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playSpawn() {
        soundPool?.play(spawnSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playToken() {
        soundPool?.play(tokenSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playKill() {
        soundPool?.play(killSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun playDice() {
        soundPool?.play(diceSoundId, 1f, 1f, 1, 0, 1f)
    }

    fun startBackgroundMusic() {
        if (bgMediaPlayer != null && !bgMediaPlayer!!.isPlaying) {
            bgMediaPlayer?.start()
        }
    }

    fun pauseBackgroundMusic() {
        if (bgMediaPlayer != null && bgMediaPlayer!!.isPlaying) {
            bgMediaPlayer?.pause()
        }
    }

    fun stopBackgroundMusic() {
        try {
            bgMediaPlayer?.stop()
            bgMediaPlayer?.prepare()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        bgMediaPlayer?.stop()
        bgMediaPlayer?.release()
        bgMediaPlayer = null
    }
}