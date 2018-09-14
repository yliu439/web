package com.liuyang.web.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;

import java.util.concurrent.Callable;

public class CustomCallableProcessingInterceptor implements CallableProcessingInterceptor {
    private final Logger logger = LoggerFactory.getLogger(CustomCallableProcessingInterceptor.class);

    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
        logger.debug("CustomCallableProcessingInterceptor>>beforeConcurrentHandling");

    }

    @Override
    public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
        logger.debug("CustomCallableProcessingInterceptor>>preProcess");

    }

    @Override
    public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
        logger.debug("CustomCallableProcessingInterceptor>>postProcess");
    }

    @Override
    public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        logger.debug("CustomCallableProcessingInterceptor>>handleTimeout");
        return null;
    }

    @Override
    public <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) throws Exception {
        logger.debug("CustomCallableProcessingInterceptor>>handleError");
        return null;
    }

    @Override
    public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
        logger.debug("CustomCallableProcessingInterceptor>>afterCompletion");
    }
}
