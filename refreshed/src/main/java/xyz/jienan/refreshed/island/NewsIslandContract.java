package xyz.jienan.refreshed.island;

/**
 * Created by Jienan on 2018/1/25.
 */

public class NewsIslandContract {
    interface View {
    }
    interface Presenter {
        boolean ifTopicsExist(String topics);
        boolean addTopics(String topics);
    }
}
