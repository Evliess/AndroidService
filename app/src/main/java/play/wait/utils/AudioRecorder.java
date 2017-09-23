package play.wait.utils;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by guijj on 9/4/2017.
 */

public class AudioRecorder {
    public static final int DEFAULT_SAMPLE_RATE = 64000;
    public static final int DEFAULT_BIT_RATE = 64000;

    private static final int STOP_AUDIO_RECORD_DELAY_MILLIS = 300;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARED = 1;
    private static final int STATE_RECORDING = 2;

    private int mState = STATE_IDLE;
    private long mSampleStart = 0;
    private MediaRecorder mRecorder;



    public static AudioRecorder getInstance() {
        return new AudioRecorder();
    }


    public synchronized boolean prepareRecord(int audioSource, int outputFormat, int audioEncoder,
                                              File outputFile) {
        return prepareRecord(audioSource, outputFormat, audioEncoder, DEFAULT_SAMPLE_RATE,
                DEFAULT_BIT_RATE, outputFile);
    }

    public synchronized boolean prepareRecord(int audioSource, int outputFormat, int audioEncoder,
                                              int sampleRate, int bitRate, File outputFile) {
        stopRecord();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(audioSource);
        mRecorder.setOutputFormat(outputFormat);
        mRecorder.setAudioSamplingRate(sampleRate);
        mRecorder.setAudioEncodingBitRate(bitRate);
        mRecorder.setAudioEncoder(audioEncoder);
        mRecorder.setOutputFile(outputFile.getAbsolutePath());

        // Handle IOException
        try {
            mRecorder.prepare();
        } catch (IOException exception) {
           System.out.println( "startRecord fail, prepare fail: " + exception.getMessage());
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return false;
        }
        mState = STATE_PREPARED;
        return true;
    }

    public synchronized boolean startRecord() {
        if (mRecorder == null || mState != STATE_PREPARED) {
            return false;
        }
        // Handle RuntimeException if the recording couldn't start
        try {
            mRecorder.start();
        } catch (RuntimeException exception) {
            System.out.print("startRecord fail, start fail: " + exception.getMessage());
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return false;
        }
        mSampleStart = System.currentTimeMillis();
        mState = STATE_RECORDING;
        return true;
    }


    public synchronized boolean startRecord(int audioSource, int outputFormat, int audioEncoder,
                                            int sampleRate, int bitRate, File outputFile) {
        stopRecord();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(audioSource);
        mRecorder.setOutputFormat(outputFormat);
        mRecorder.setAudioSamplingRate(sampleRate);
        mRecorder.setAudioEncodingBitRate(bitRate);
        mRecorder.setAudioEncoder(audioEncoder);
        mRecorder.setOutputFile(outputFile.getAbsolutePath());

        // Handle IOException
        try {
            mRecorder.prepare();
        } catch (IOException | RuntimeException exception) {
            System.out.println( "startRecord fail, prepare fail: " + exception.getMessage());
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return false;
        }
        // Handle RuntimeException if the recording couldn't start
        try {
            mRecorder.start();
        } catch (RuntimeException exception) {
            System.out.println("startRecord fail, start fail: " + exception.getMessage());
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return false;
        }
        mSampleStart = System.currentTimeMillis();
        mState = STATE_RECORDING;
        return true;
    }

    public synchronized int stopRecord() {
        if (mRecorder == null) {
            mState = STATE_IDLE;
            return -1;
        }

        int length = -1;
        switch (mState) {
            case STATE_RECORDING:
                try {
                    Thread.sleep(STOP_AUDIO_RECORD_DELAY_MILLIS);
                    mRecorder.stop();
                    length = (int) ((System.currentTimeMillis() - mSampleStart) / 1000);
                } catch (RuntimeException e) {
                    System.out.println( "stopRecord fail, stop fail(no audio data recorded): " +
                            e.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("stopRecord fail, stop fail(InterruptedException): " + e.getMessage());
                }
                // fall down
            case STATE_PREPARED:
                // fall down
            case STATE_IDLE:
                // fall down
            default:
                try {
                    mRecorder.reset();
                } catch (RuntimeException e) {
                    System.out.println( "stopRecord fail, reset fail " + e.getMessage());
                }
                mRecorder.release();
                mRecorder = null;
                mState = STATE_IDLE;
                break;
        }
        return length;
    }
}
