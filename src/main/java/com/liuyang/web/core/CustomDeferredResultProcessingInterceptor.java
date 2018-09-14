package com.liuyang.web.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

public class CustomDeferredResultProcessingInterceptor implements DeferredResultProcessingInterceptor {
    private final Logger logger = LoggerFactory.getLogger(CustomDeferredResultProcessingInterceptor.class);

    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        logger.debug("CustomDeferredResultProcessingInterceptor>>beforeConcurrentHandling");
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        logger.debug("CustomDeferredResultProcessingInterceptor>>preProcess");
    }

    @Override
    public <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, Object concurrentResult) throws Exception {
        logger.debug("CustomDeferredResultProcessingInterceptor>>postProcess");
    }

    @Override
    public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        logger.debug("CustomDeferredResultProcessingInterceptor>>handleTimeout");
        return false;
    }

    @Override
    public <T> boolean handleError(NativeWebRequest request, DeferredResult<T> deferredResult, Throwable t) throws Exception {
        logger.debug("CustomDeferredResultProcessingInterceptor>>handleError");
        return false;
    }

    @Override
    public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        logger.debug("CustomDeferredResultProcessingInterceptor>>afterCompletion");
    }
}
