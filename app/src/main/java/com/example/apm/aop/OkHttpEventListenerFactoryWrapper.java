package com.example.apm.aop;

import okhttp3.Call;
import okhttp3.EventListener;

/**
 * PROXY Http EventListenerFactory
 *
 * Created by Nelson on 2019-11-26.
 */
public class OkHttpEventListenerFactoryWrapper implements EventListener.Factory {

    public static EventListener.Factory wrap(EventListener.Factory factory) {
        return new OkHttpEventListenerFactoryWrapper(factory);
    }

    final EventListener.Factory oldEventFactory;

    OkHttpEventListenerFactoryWrapper(EventListener.Factory oldEventFactory) {
        this.oldEventFactory = oldEventFactory;
    }

    @Override
    public EventListener create(Call call) {
        EventListener oldEventListener = oldEventFactory.create(call);
        return OkHttpEventListenerWrapper.wrap(oldEventListener);
    }
}
