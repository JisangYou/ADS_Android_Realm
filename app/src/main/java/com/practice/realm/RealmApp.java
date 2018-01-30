package com.practice.realm;

import android.app.Application;

import io.realm.Realm;

/**
 *
 *액티비티마다 필요한 기능들을
 * 이렇게 application을
 * 상속받아 공통적으로 사용할 수 있다.
 * <manifest>에 등록하기
 * SQLLite의 경우 사용하는 자원이 달라서 여기서 사용할 수 없음.
 */

public class RealmApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
