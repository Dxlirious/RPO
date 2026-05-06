package com.traintickets.mvp.presenter;

import android.content.Context;

import com.traintickets.models.Train;
import com.traintickets.mvp.contract.SearchContract;
import com.traintickets.mvp.model.SearchModel;

import java.util.List;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View view;
    private final SearchModel model;

    public SearchPresenter(SearchContract.View view, Context context) {
        this.view  = view;
        this.model = new SearchModel(context);
    }

    @Override
    public void searchTrains(String from, String to, String date, String passengers) {
        if (view == null) return;
        view.showLoading(true);
        view.showOfflineBanner(false);

        model.loadTrains(from, to, date, new SearchModel.Callback() {

            @Override
            public void onSuccess(List<Train> trains, boolean fromCache) {
                if (view == null) return;
                view.showLoading(false);
                if (trains.isEmpty()) {
                    view.showEmpty();
                } else {
                    view.showOfflineBanner(fromCache);
                    view.showTrains(trains);
                }
            }

            @Override
            public void onError(String message) {
                if (view == null) return;
                view.showLoading(false);
                view.showError(message);
            }
        });
    }

    @Override
    public void detachView() {
        view = null;
        model.release();
    }
}