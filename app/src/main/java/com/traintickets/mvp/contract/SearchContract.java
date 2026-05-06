package com.traintickets.mvp.contract;

import com.traintickets.models.Train;
import java.util.List;

public interface SearchContract {

    interface View {
        void showTrains(List<Train> trains);
        void showError(String message);
        void showLoading(boolean isLoading);
        void showOfflineBanner(boolean isOffline);
        void showEmpty();
    }

    interface Presenter {
        void searchTrains(String from, String to, String date, String passengers);
        void detachView();
    }
}