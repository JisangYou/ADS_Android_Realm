package com.practice.realm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.practice.realm.realm.Bbs;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        create();
        read();
        update();
        delete();
    }


    public void create() {
        /* 동기로 데이터 입력 */
        // 1. 인스턴스 생성 - connection
        Realm realm = Realm.getDefaultInstance();
        //2. 트랜젝션 시작
        realm.beginTransaction();


        Number maxValue = realm.where(Bbs.class).max("no");
        int no = (maxValue != null) ? maxValue.intValue() + 1 : 1;
        Bbs bbs = realm.createObject(Bbs.class, no); // 레코드 한개 생성
        bbs.setNo(no);
        bbs.setTitle("제목 1");
        bbs.setContent("내용을 여기\n 아싸라비아~~~");
        bbs.setDate(System.currentTimeMillis());
        realm.commitTransaction();
        //테이블에 한개의 레코드셋이 들어간다.

//        /* 비동기로 데이터 입력 */
//        RealmAsyncTask transaction = realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm asyncrealm) {
//                Bbs bbs = asyncrealm.createObject(Bbs.class);
//                bbs.setNo(2);
//                bbs.setTitle("제목 2");
//                bbs.setContent("내용2을 여기\n 아싸라비아~~~");
//                bbs.setDate(System.currentTimeMillis());
//            }
//        }, new Realm.Transaction.OnSuccess(){
//            @Override
//            public void onSuccess() {
//                afterCreation();
//            }
//        });
//    }


    /* 비동기로 데이터 입력 */
        RealmAsyncTask transaction = realm.executeTransactionAsync(
                asyncRealm -> {
                    Bbs bbs2 = asyncRealm.createObject(Bbs.class);
                    bbs.setNo(2);
                    bbs.setTitle("제목 2");
                    bbs.setContent("내용2을 여기\n 아싸라비아~~~");
                    bbs.setDate(System.currentTimeMillis());

                }
                , () -> {
                    afterCreation();
                }

        );
    }

    private void afterCreation() {
        Toast.makeText(this, "데이터가 입력되었습니다.", Toast.LENGTH_SHORT).show();
    }

    public void read() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Bbs> query = realm.where(Bbs.class);
        query.equalTo("no", 1);
        query.or();
        query.equalTo("title", "제목1");
        // select * from bbs where no = 1 or title = '제목 1'
        /* 동기로 질의 */
        RealmResults<Bbs> result1 = query.findAll();
        Bbs bbsFirst = result1.first();
        //질의 후 데이터 수정
        realm.beginTransaction();
        bbsFirst.setTitle("수정된 제목");

        realm.commitTransaction();

        /* 비동기로 질의 */
        query.findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<Bbs>>() {
            @Override
            public void onChange(RealmResults<Bbs> bbs) {

                Bbs bbsFirst = bbs.first();
                bbsFirst.setTitle("수정된 제목");
            }
        });


    }

    public void update() {

        // 1. 수정할 객체를 가져오거나 생성
        Bbs bbs = new Bbs();
        bbs.setNo(1);
        bbs.setTitle("제목");
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(bbs);
            }
        });

    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Bbs> result = realm.where(Bbs.class).findAll();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteAllFromRealm();// 검색결과의 첫번째 삭제

                Bbs bbs = result.get(2); //특정 행 삭제
                bbs.deleteFromRealm();
                result.deleteFromRealm(2);

                result.deleteAllFromRealm(); // 검색결과 전체 삭제제
            }
        });
    }
}
