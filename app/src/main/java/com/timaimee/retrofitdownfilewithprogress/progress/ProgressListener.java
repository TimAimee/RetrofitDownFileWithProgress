package com.timaimee.retrofitdownfilewithprogress.progress;

/**
 * Created by TimAimee on 2016/8/21.
 */
public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
