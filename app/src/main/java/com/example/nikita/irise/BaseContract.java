package com.example.nikita.irise;

/**
 * Created by Nikita on 31.05.2018.
 */

public interface BaseContract {
    interface BaseView{
        void showMessageAboutSuccess();
        void showMessageAboutError();
    }
    interface BasePresenter{
        void onStart();
        void onStop();
    }
}
