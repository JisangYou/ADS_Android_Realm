package com.practice.realm;

import android.app.Application;

import io.realm.Realm;

/**
 *
 *액티비티마다 필요한 기능들을
 * 이렇게 application을
 * 상속받아 공통적으로 사용할 수 있다.
 * <manifest>에 등록하기
 *
 */

public class RealmApp extends Application { //
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
