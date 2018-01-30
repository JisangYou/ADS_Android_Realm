# ADS04 Android

## 수업 내용

- realm 학습 

## Code Review

### mainActivity

```Java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void create() {
        /* 동기로 데이터 입력 */
        // 1. 인스턴스 생성 - connection
        Realm realm = Realm.getDefaultInstance();
        // 2. 트랜잭션 시작
        realm.beginTransaction();
        // 테이블 처리
        // 자동증가 로직처럼 사용하기
        Number maxValue = realm.where(Bbs.class).max("no");
        int no = (maxValue!=null) ? maxValue.intValue() +1:1;
        // 프라이머리 키는 class 뒤에 설정해준다.
        Bbs bbs = realm.createObject(Bbs.class, no); // 레코드 한개 생성
        bbs.setTitle("제목 1");
        bbs.setContent("내용을 여기에넣는다. \n  ");
        bbs.setDate(System.currentTimeMillis());
        realm.commitTransaction();

        /* 비동기로 데이터 입력 */
        RealmAsyncTask transaction = realm.executeTransactionAsync(
                asyncRealm -> {
                    Bbs bbs2 = asyncRealm.createObject(Bbs.class);
                    bbs2.setNo(2);
                    bbs2.setTitle("제목 2");
                    bbs2.setContent("내용을 여기에넣는다. \n  ");
                    bbs2.setDate(System.currentTimeMillis());
                }
                , () -> { // 성공시 진행
                    afterCreation(); // 데이터 베이스 처리가 끝나고 호출될 함수를 지정
                }
                , error -> {// 에러처리

                }
        );

        RealmAsyncTask transaction2 = realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {

            }
        });
    }
    public void afterCreation() {

    }

    public void read() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<Bbs> query = realm.where(Bbs.class);
        query.equalTo("no",1);
        query.or();
        query.equalTo("title", "제목 1");
        // select * from bbs where no = 1 or title = '제목1'
        /* 동기로 질의 */
        RealmResults<Bbs> result1 = query.findAll(); // 결과는 array로 가져오게 된다.


        /* 비동기로 질의 */ // 비동기인 경우는 먼저 찍히기 때문에 콜백이 필요
        query.findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<Bbs>>() {
            @Override
            public void onChange(RealmResults<Bbs> bbs) {
                // 결과처리
            }
        });
    }

    public void update() { //read 후에 update를 실행한다.

        // 동기로 하는 경우
        Realm realm3 = Realm.getDefaultInstance();
        RealmQuery<Bbs> query2 = realm3.where(Bbs.class);
        query2.equalTo("no",1);
        query2.or();
        query2.equalTo("title", "제목 1");
        // select * from bbs where no = 1 or title = '제목1'
        /* 동기로 질의 */
        RealmResults<Bbs> result2 = query2.findAll();
        Bbs bbsFirst = result2.first();
        realm3.beginTransaction();
        bbsFirst.setTitle("수정된 제목");
        realm3.commitTransaction();

        // 비동기1
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

        // 비동기2
        Realm realm2 = Realm.getDefaultInstance();
        RealmQuery<Bbs> query = realm2.where(Bbs.class);
        query.equalTo("no",1);
        query.findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<Bbs>>() {
            @Override
            public void onChange(RealmResults<Bbs> bbs) {
                Bbs bbs1 = bbs.first();
                bbs1.setTitle("수정");
            }
        });
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Bbs> result = realm.where(Bbs.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteFirstFromRealm(); // 검색결과의 첫번째 삭제

                Bbs bbs = result.get(2); // 특정 행 삭제
                bbs.deleteFromRealm();
                // result.deleteFromRealm(2); // 위와 동일

                result.deleteAllFromRealm(); // 검색결과 전체삭제
            }
        });
    }
}
```

### realmApp

```Java

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

```

### Bbs

```Java

public class Bbs extends RealmObject { // RealmObject를 상속받는다.

    @PrimaryKey
    private int no;
    private String title;
    private String Content;
    private String user;
    private long date;

    @Ignore // 이놈은 테이블의 컬럼으로 사용되지 않는다.
    private String test;


    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}

```

## 보충설명

### Realm이란?  

- Realm사에서 만든 Mobile Database
- 가벼운 객체 컨테이너
- RDBMS처럼 쿼리, 필터링, 관계형(상호 연결)이 가능하고 저장이 됨
- 라이브 오브젝트이며 반응형 객체임
- ※ Realm에 저장된 객체의 인스턴스가 있고 다른곳에서 해당 인스턴스 업데이트 시 기존 인스터스도 업데이트
- 크로스 플랫폼 
- 기기와 애플리케이션 사이에서 매끄럽게 동기화 되며 스레드에서 안전하게 접근 가능
- String Key 보안체계 확립
- Android, iOS, Xamarin, React Native에서 이용가능
- 항상 오프라인 우선 방식으로 동작

### realm 성능 및 세팅

![성능]()

### 공식문서 예제 코드

```Java
// Define your model class by extending RealmObject
public class Dog extends RealmObject {
    private String name;
    private int age;

    // ... Generated getters and setters ...
}

public class Person extends RealmObject {
    @PrimaryKey
    private long id;
    private String name;
    private RealmList<Dog> dogs; // Declare one-to-many relationships

    // ... Generated getters and setters ...
}

// Use them like regular java objects
Dog dog = new Dog();
dog.setName("Rex");
dog.setAge(1);

// Initialize Realm (just once per application)
Realm.init(context);

// Get a Realm instance for this thread
Realm realm = Realm.getDefaultInstance();

// Query Realm for all dogs younger than 2 years old
final RealmResults<Dog> puppies = realm.where(Dog.class).lessThan("age", 2).findAll();
puppies.size(); // => 0 because no dogs have been added to the Realm yet

// Persist your data in a transaction
realm.beginTransaction();
final Dog managedDog = realm.copyToRealm(dog); // Persist unmanaged objects
Person person = realm.createObject(Person.class); // Create managed objects directly
person.getDogs().add(managedDog);
realm.commitTransaction();

// Listeners will be notified when data changes
puppies.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Dog>>() {
    @Override
    public void onChange(RealmResults<Dog> results, OrderedCollectionChangeSet changeSet) {
        // Query results are updated in real time with fine grained notifications.
        changeSet.getInsertions(); // => [0] is added.
    }
});

// Asynchronously update objects on a background thread
realm.executeTransactionAsync(new Realm.Transaction() {
    @Override
    public void execute(Realm bgRealm) {
        Dog dog = bgRealm.where(Dog.class).equalTo("age", 1).findFirst();
        dog.setAge(3);
    }
}, new Realm.Transaction.OnSuccess() {
    @Override
    public void onSuccess() {
        // Original queries and Realm objects are automatically updated.
        puppies.size(); // => 0 because there are no more puppies younger than 2 years old
        managedDog.getAge();   // => 3 the dogs age is updated
    }
});
```





### 출처

- 출처: http://cescjuno.tistory.com/entry/Realm [개발주노]
- 출처 : https://realm.io/docs/java/latest [relam 공식문서]
- 참조하면 좋은 사이트 : http://dktfrmaster.blogspot.kr/2016/10/orm-realm.html
- 출처 : https://github.com/Lee-KyungSeok/Study/tree/master/Android/Contents/Realm

## TODO

- 추후에 모바일 데이터 베이스를 사용할 일이 있을 때, 한번 사용해 볼것
- Sqllite와 ORM Library와 비교해보기

## Retrospect

- 모바일 데이터베이스에도 여러가지가 있다는 것, 다양한 라이브러리가 있다는 것을 알게되었다.
- 모바일 데이터베이스들을 비교,분석 해서 적합한 기술을 선택하는 안목도 개발에 굉장히 큰 이슈인 것 같다.
- Trendy한 기술들을 계속해서 찾아보고, 학습하는 습관 필요!

## Output
- 생략
