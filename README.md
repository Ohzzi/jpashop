## 실전! 스프링 부트와 JPA 활용 코드
김영한님의 인프런 강의  
[강의 링크](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1/)  
[로드맵 링크](https://www.inflearn.com/roadmaps/149)

### 강의와 다른 점
- Junit4 -> Junit5 사용  
기존에 Junit4를 사용해본 적이 없고 Junit5로 테스트를 진행했었기 때문에 더 익숙하고 최신 버전인 Junit5로 테스트 코드를 작성하기로 결정
  
### 사용 기술 스택 / 프레임워크 / 라이브러리
- Java 11
- SpringBoot 2.4.2
- Gradle
- Junit 5
- h2 database

### Note
강의를 들으면서 메모할 부분들

#### 연관 관계  
연관 관계에는 일대다, 다대일, 다대다 관계가 있으며 단방향과 양방향 관계가 존재한다. 이 중 다대다 관계는 관계형 데이터베이스는 물론이고 엔티티에서도 거의 사용하지 않는다.
따라서 다대다 관계의 가운데에 엔티티를 추가해서 다대다 관계를 다대일, 일대다 관계로 풀어낸다.

**연관 관계의 주인**  
연관 관계의 주인은 비즈니스 상 우위에 있다고 정하면 안된다. 연관관계의 문제는 단순히 외래 키를 누가 관리하느냐의 문제다.
일대다 연관관계에서 일 쪽에 연관 관계의 주인으로 정하면 유지 보수가 어렵고 별도의 업데이트 쿼리가 발생하는 성능 문제가 있다.

#### 엔티티 클래스 개발

**엔티티의 Setter 사용을 지양할 것**  
이론적으로는 Getter, Setter 모두 사용하지 않고 별도의 메소드를 사용하는 것이 이상적이다.
하지만 Getter는 쓸 상황이 많고, 사용한다고 엔티티가 변하지 않으니 사용하는 것이 편리하다.
그러나 Setter를 호출하면 데이터가 변한다. Setter를 막 열어두면 엔티티가 왜 변경되는지 추적하기 점점 힘들어진다.
따라서 엔티티의 변경 지점이 명확한 별도의 비즈니스 메소드를 작성한다.

예제에서는 편리함을 위해 Setter를 사용했지만, 이번 학습에서는 Setter 대신 builder 패턴을 사용하고, 
중간에 엔티티의 값이 변경되어야 하는 로직은 별도의 비즈니스 메소드를 작성해서 Setter의 사용을 지양하도록 한다.

builder 패턴을 사용할 때는 lombok @Builder를 사용한다. 이 때, @NoArgsConstructor 와 @AllArgsConstructor를 사용한다.
@NoArgsConstructor는 접근 제한을 protected로 걸어서 혹시 모를 빈 객체 생성을 방지한다.

**상속 관계 매핑**  
관계형 데이터베이스에는 상속이라는 개념이 없다. 대신 슈퍼타입 서브타입 관계라는 유사한 모델링 기법이 있다.
슈퍼타입 서브타임 관계를 테이블로 구현할 때는 세 가지 방식을 사용할 수 있다. (다음의 세 가지 전략의 이름은 JPA에서 지칭하는 이름이다.)
- 조인 전략: 각각을 테이블로 만들고 조회할 때 조인한다.
- 단일 테이블 전략: 슈퍼타입 테이블이 서브타입 테이블의 필드를 모두 가진다.
- 구현 클래스마다 테이블 전략: 서브타입 테이블만 만든다. 설계 전문가와 ORM 전문가 모두 추천하지 않는 방식이다.

이 예제에서는 Item을 상속받는 Album, Book, Movie를 구현할 때 단일 테이블 전략을 사용하기로 한다.
단일 테이블 전략에서는 슈퍼타입에 @DiscriminatorColumn(name = "DTYPE") 어노테이션을 사용하여 매핑해주고,
서브타입에는 @DiscriminatorValue("XXX")를 사용해서 매핑한다.

**모든 연관관계는 지연로딩으로 설정**  
즉시로딩(Eager)는 예측이 어렵고, 어떤 SQL이 실행될지 추적이 어렵다.
모든 연관관계가 EAGER로 설정되어 있으면 필요없는 연관관계까지 모두 끌어오게 되고, JPQL을 사용할 떄 N+1 문제가 발생한다.
따라서 실무에서는 EAGER를 사용하지 않고 모든 연관관계를 지연로딩(LAZY)으로 설정한다.
연관된 엔티티를 함께 조회해야 할 때는 fetch join이나 엔티티 그래프를 이용한다.

OneToMany는 기본 fetch 전략이 LAZY로 되어 있지만, XToOne은 기본 fetch 전략이 EAGER로 되어 있어 직접 설정해야 한다.

**컬렉션은 필드에서 바로 초기화**  
컬렉션은 필드에서 바로 초기화 하는 것이 null 문제에서도 안전하고, 하이버네이트 영속화 시에 안전하다.
하이버네이트는 엔티티를 영속화 할 떄 하이버네이트가 제공하는 내장 컬렉션으로 감싸서 변경하기 때문에,
임의의 메소드에서 컬렉션을 잘못 생성하면 하이버네이트 내부 메커니즘에 문제가 발생할 수 있다.
따라서 필드를 선언할 때 바로 초기화 하는것이 안전하고 코드도 간결하다.

코드 내에서 컬렉션을 꺼내서 Setter 등으로 수정되지만 않으면 되므로, 이번 예제를 공부하면서 컬렉션은 모두 final로 선언하기로 한다.
실제로 엔티티 내의 컬렉션들은 필드 레벨에서 초기화 했을 때 final로 선언하라는 경고 메시지가 뜬다.

**Cascade**  
cascade 옵션은 엔티티의 영속성을 전이시키는 옵션이다. 영속성 전이의 종류로는 ALL, PERSIST, DETACH, REFRESH, MERGE, REMOVE등이 있다.
이 예제에서 사용하는 CascadeType.ALL 옵션을 넣어주면 모든 cascade 옵션이 적용되어 엔티티의 영속화, 병합, 새로고침, 삭제, detatch()가 해당 엔티티의 필드에 보유된 엔티티에도 적용된다.

### 회원 도메인 개발

**EntityManager와 PersistenceContext**  
EntityManager는 엔티티를 관리하는 역할을 수행한다. Repository 클래스 내에 엔티티 매니저를 생성한 뒤, @PersistenceContext 어노테이션으로 엔티티 매니저를 주입해준다.
PersistenceContext(영속성 컨텍스트)는 엔티티를 영구히 저장하는 환경으로, JPA에는 CRUD의 4가지 동작 중 UPDATE가 존재하지 않지만, 엔티티 매니저와 영속성 컨텍스트가 데이터의 변화를 감지하여 필요한 UPDATE 쿼리를 자동으로 수행한다.

**필드 주입과 생성자 주입**  
@Autowired 어노테이션을 필드 윗 줄에 작성하여 해당 빈을 필드 주입 해줄 수 있다. 가장 사용하기 간단하다는 장점이 있지만, 인텔리제이에서는 @Autowired를 사용하면 경고문이 나타난다.
필드 주입이 추천되지 않는 방법이기 때문인데, 보통 필드 주입 대신 생성자 주입을 하는 것이 권장된다.  
생성자 주입은 변경 불가능한 안전한 객체를 생성하는 것이 가능하며, final 키워드를 추가해서 컴파일 시점에 객체를 생성하지 않는 오류를 방지할 수 있다.
또한 생성자 주입을 함으로써 순환 참조를 방지할 수 있고, 테스트 코드의 작성도 용이해진다.  
만약 생성자가 하나라면 @Autowired 어노테이션을 생략할 수 있으며, Spring Data JPA를 사용하면 엔티티 매니저도 생성자 주입할 수 있다.

### 상품 도메인 개발

**커스텀 예외를 만든다**  
상품 엔티티의 비즈니스 로직에서, 상품의 수량을 빼는 로직을 짤 때 남아있는 수량보다 제거하려는 수량이 더 많을 때의 예외 처리를 해 주어야 한다.(재고가 마이너스일 수는 없으니까)
이 때 NotEnoughStockException 이라는 커스텀 예외를 만들어주면 예외의 이름 만으로도 어떤 문제가 있는지 전달할 수 있다.

### 주문 도메인 개발

**생성 메소드**  
Order 클래스의 정적 팩토리 메소드 createOrder() 메소드를 작성. 이 때 Order 클래스 안에는 orderStatus 와 orderDate의 setter를 만들지 않았으므로,
정적 팩토리 메소드 안에서 새 Order를 생성할 때 Builder를 사용하여 생성 단계에서 orderStatus와 orderDate를 넣어주었다. (최대한 setter의 사용을 지양)
나머지 연관관계로 매핑된 엔티티 정보를 변경할때는 엔티티 클래스 내에서 작성한 연관관계 편의 메소드를 사용하여 매핑한다. 이렇게 엔티티 내의 필드들의 정보를 모두 입력한 Order 객체를 반환해준다. OrderItem 클래스에도 마찬가지로 static createOrderItem() 메소드를 작성한다.  

**OrderService에서 Order만 저장하는 이유**  
OrderService에서 주문 기능을 작성할 때 Order만 save하고 OrderItem이나 Delivery는 레포지토리에 저장하지 않는다.
그 이유는, Order 엔티티 안에 필드로 존재하는 Delivery와 OrderItem의 옵션이 CascadeType.ALL 이기 때문에, Order를 persist하게 되면
Delivery와 OrderItem 또한 persist하게 되기 때문이다. (영속성 전이)

**도메인 모델 패턴과 트랜잭션 스크립트 패턴**  
예제에서는 비즈니스 로직 대부분이 엔티티에 있고, 서비스 객체는 엔티티에 필요한 요청을 위임하는 역할을 한다.
이처럼 객체 지향의 특성을 적극 활용하여 엔티티가 비즈니스 로직을 가지고 서비스 계층에서 요청만 하는 것을 도메인 모델 패턴이라고 한다.
반대로, 엔티티에는 비즈니스 로직이 거의 없고 서비스 계층에서 비즈니스 로직을 처리하는 것을 처리하는 것을 트랜잭션 스크립트 패턴이라고 한다.