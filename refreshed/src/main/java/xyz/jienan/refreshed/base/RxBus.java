package xyz.jienan.refreshed.base;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by jienanzhang on 23/01/2018.
 */

public class RxBus {
    public RxBus() {

    }

    private PublishSubject<Object> bus = PublishSubject.create();

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return bus;
    }
}
