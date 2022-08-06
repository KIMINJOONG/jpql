# JPQL 문법
- 엔티티와 속성은 대소문자 구분O (Member, age)
- JPQL 키워드는 대소문자 구분 X (SELECT, FROM, where)
- 엔티티 이름 사용, 테이블 이름이 아님(Member)
- 별칭은 필수(m) (as는 생략가능)

# 집합과 정렬
- GROUP BY, HAVING
- ORDER BY

# TypeQuery, Query
- TypeQuery: 반환 타입이 명확할 때 사용
- Query: 반환 타입이 명확하지 않을 때 사용

# 결과조회
- query.getResultList(): 결과가 하나 이상일때, 리스트 반환
1. 결과가 없으면 빈 리스트 반환

- query.getSingleList(): 결과가 정확히 하나일때, 단일 객체 반환
1. 결과가 없으면 : javax.persistance.NoResultException
2. 결과가 두개 이상이면: javax.persistance.NonUniqueResultException

# 파라미터 바인딩 - 이름기준, 위치 기준
```
// 이름기준
SELECT m FROM MEMBER m where m.username=:username
query.setParameter("username", usernameParam);

// 위치기준

SELECT m FROM MEMBER m where m.username=?1
query.setParameter(1, usernameParam);
```
이름 기준 권장

---

# 프로젝션
- SELECT 절에 조회할 대상을 지정하는것
- 프로젝션 대상: 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자 등 기본 데이터 타입)
- SELECT m FROM MEMBER m -> 엔티티 프로젝션
- SELECT m.team FROM MEMBER m -> 엔티티 프로젝션
- SELECT m.address FROM MEMBER m -> 임베디드 타입 프로젝션
- SELECT m.username, m.age FROM MEMBER m -> 스칼라 타입 프로젝션
- DISTINCT로 중복 제거

# 프로젝션 - 여러값 조회
- SELECT m.username, m.age FROM MEMBER m
- Query 타입으로 조회
- Object[] 타입으로 조회
- new 명령어로 조회
1. 단순값을 DTO로 바로 조회 SELECT new UserDTO(m.username, m.age) FROM MEMBER m
- 패키지명을 포함한 전체 클래스 명 입력
- 순서와 타입이 일치하는 생성자 필요

---

# 페이징 API
- JPA는 페이징을 다음 두 API로 추상화
- setFirstResult(int startPosition): 조회 시작 위치 0
- setMaxResult(int maxResult): 조회 할 데이터 수
```
// 페이징 쿼리
List<Member> result =  em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();
```

---

#조인
- 내부조인: SELECT m FROM MEMBER m [INNER] JOIN m.team t
- 외부조인: SELECT m FROM MEMBER m LEFT [OUTER] JOIN m.team t
- 세타조인: SELECT count(m) FROM MEMBER m, Team t where m.username = t.name

# 조인 - ON절
- ON절을 활용한 조인(JPA 2.1부터 지원)
1. 조인 대상 필터링
```
    예) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
    JQPL:
        SELECT m, t 
        FROM Member m 
        LEFT JOIN m.team t on t.name = 'A';
        
    SQL:
        SELECT m.*, t.* 
        FROM Member m 
        LEFT JOIN Team t 
        ON m.TEAM_ID = t.id 
        and t.name = 'A';
```
2. 연관관계 없는 엔티티 외부 조인(하이버네이트 5.1부터)
```
    예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
    JQPL:
        SELECT m, t
        FROM Member m
        LEFT JOIN Team t
        on m.username = t.name
        
    SQL:
        SELECT m.*, t.*
        FROM Member m
        JOIN Team t
        ON m.username = t.name
```

---

# 서브쿼리
- 나이가 평균보다 많은 회원
```
    select m
    from Member m
    where m.age > (select avg(m2.age) from Member m2)
```

- 한 건이라도 주문한 고객
```
    select m
    from Member m
    where (
            select count(o)
            from Order o
            where m = o.member
           ) > 0
```

# 서브 쿼리 지원 함수
- [NOT] EXIST(subquery) : 서브쿼리에 결과가 존재하면 참
    1. {ALL | ANY | SOME} (subquery)
    2. ALL 모두 만족하면 참
    3. ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참
- [NOT] IN (subquery): 서브쿼리의 결과중 하나라도 같은것이 있으면 참

# 서브 쿼리 예제
- 팀A 소속인 회원
```
    select m
    from Member m
    where exists (
                    select t
                    from m.team t
                    where t.name = '팀A'
                   )
```

- 전체 상품 각각의 재고보다 주문량이 많은 주문들
```
    select o
    from Order o
    where o.orderAmount > ALL(select p.stockAmount from Product p)
```

- 어떤 팀이든 팀에 소속된 회원
```
    select m
    from Member m
    where m.team = ANY(select m from Team t)
```

# JPA 서브쿼리의 한계
- JPA는 WHERE, HAVING 절에서만 서브쿼리 사용 가능
- SELECT 절도 가능(하이버 네이트에서 지원)
- FROM 절의 서브쿼리는 현재 JPQL에서 불가능(조인으로 풀수있으면 풀어서 해결)

---

# JPQL 타입 표현
- 문자: 'HELLO', 'She"s'
- 숫자: 10L(Long), 10D(Double), 10F(Float)
- Boolean: TRUE, FALSE
- ENUM: jpabook.MemberType.Admin(패키지명 포함)
- 엔티티 타입: TYPE(m) = Member (상속 관계에서 사용)

# JPQL 기타
- SQL과 문법이 같은 식
- EXIST, IN
- AND, OR, NOT
- =, >, >=, <, <=, <>
- BETWEEN, LIKE, IS NULL

---

# 조건식 - CASE 식
## 기본 CASE 식
```
  select 
    case when m.age <= 10 then '학생요금'
         when m.age >= 60 then '경로요금'
         else '일반요금'
    end
  from Member m   
```

## 단순 CASE 식
```
select t.name
    case when '팀A' <= 10 then '인센티브110%'
         when '팀B' >= 60 then '인센티브120%'
         else '인센티브105%'
    end
  from Team t  
```

## 조건 CASE 식
- COALESCE: 하나씩 조회해서 null이 아니면 반환
- NULLIF: 두 값이 같으면 null 반환, 다르면 첫번째 값 반환

### 사용자 이름이 없으면 이름 없는 회원을 반환
```
  select coalesce(m.username, '이름없는 회원') from Member m
```

### 사용자 이름이 '관리자'면 null을 반환하고 나머지는 본인의 이름을 반환
```
  select NULLIF(m.username, '관리자') from Member m
```

---

# JPQL 기본 함수
- CONCAT
- SUBSTRING
- TRIM
- LOWER, UPPER
- LENGTH
- LOCATE
- ABS, SQRT, MOD
- SIZE, INDEX(JPA 용도)

# 사용자 정의 함수 호출
- 하이버네이트는 사용전 방언에 추가해야한다.
  1. 사용하는 DB 방언을 상속받고, 사용자 정의 함수를 등록한다.
```
  select function('group_concat', i.name) from Item i
```

---

# 경로 표현식
- .(점)을 찍어서 그래프를 탐색하는것
```
  select m.username -> 상태 필드
  from Member m
  join m.team t -> 단일 값 연관 필드
  join m.orders o -> 컬렉션 값 연관 필드
  where t.name = '팀A'
```

# 경로 표현식 용어 정리
- 상태 필드(state field) : 단순히 값을 저장하기 위한 필드(ex: m.username)
- 연관 필드(association field) : 연관 관계를 위한 필드
1. 단일 값 연관 필드: @ManyToOne, @OneToOne, 대상이 엔티티(ex: m.team)
2. 컬렉션 값 연관 필드: @OneToMany, @ManyToMany, 대상이 컬렉션(ex: m.orders)

# 경로 표현식 특징
- 상태 필드(state field): 경로 탐색의 끝, 탐색 X
- 단일 값 연관 경로: 묵시적 내부조인(inner join) 발생, 탐색 O
- 컬렉션 값 연관 경로: 묵시적 내부조인 발생, 탐색 X
- FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능

# 실무에서는 묵시적 조인을 쓰면 절대 안된다.

# 단일 값 연관 경로 탐색
- JPQL: select o.member from Order o
- SQL: 
  ```
    select m.* 
    from Orders o 
    inner join Member m 
    on o.member_id = m.id
  ```
  
# 명시적 조인, 묵시적 조인
- 명시적 조인: join 키워드 직접 사용
1. select m from Member m join m.team t

- 묵시적 조인: 경로 표현식에 의해 묵시적으로 SQL 조인 발생(내부조인만 가능)
1. select m.team from Member m

# 경로 표현식 예제
```

  // 성공
  select o.member.team
  from Order o
  
  // 성공
  select t.members
  from Team t
  
  // 실패
  select t.members.username
  from Team t
  
  // 성공
  select m.username
  from Team t
  join t.members m
  
  
```

# 경로 탐색을 사용한 묵시적 조인 시 주의사항
- 항상 내부 조인
- 컬렉션은 경로 탐색의 끝, 명시적 조인을 통해 별칭을 얻어야함
- 경로 탐색은 주로 SELECT, WHERE 절에서 사용하지만 묵시적 조인으로 인해 SQL의 FROM(JOIN)절에 영향을 줌

# 실무 조언
- 가급적 묵시적 조인 대신에 명시적 조인 사용
- 조인은 SQL 튜닝에 중요 포인트
- 묵시적 조인은 조인이 일어나는 상황을 한눈에 파악하기 어려움

---

# JPQL - 페치 조인(fetch join) 실무에서 엄청나게 중요함!!
- SQL 조인 종류 X
- JPQL에서 성능 최적화를 위해 제공하는 기능
- 연관된 엔티티나 컬렉션을 SQL한꺼번에 함께 조회하는 기능
- join fetch 명령어 사용
- 페치 조인 ::= [LEFT [OUTER] | INNER] JOIN FETCH 조인 경로

# 엔티티 페치 조인
- 회원을 조회하면서 연관된 팀도 함께 조회(SQL 한번에)
- SQL을 보면 회원 뿐만 아니라 팀(T.*)도 함께 SELECT
- [JPQL] select m from Member m join fetch m.team
- [SQL] SELECT M.*, T.* FROM MEMBER M INNER JOIN TEAM T ON M.TEAM_ID = T.ID

# 컬렉션 페치 조인
- 일대다 관계, 컬렉션 페치 조인
- [JQPL]
```
  select t
  from Team t
  join fetch t.members
  where t.name = '팀A'
```

-[SQL]
```
  SELECT T.*, M.*
  FROM TEAM T
  INNER JOIN MEMBER M
  ON T.ID = M.TEAM_ID
  WHERE T.NAME = '팀A'
```

# 페치조인과 DISTINCT
- SQL의 DISTINCT는 중복된 결과를 제거하는 명령
- JPQL의 DISTINCT 2가지 기능 제공
1. SQL에 DISTINCT 추가
2. 애플리케이션에서 엔티티 중복 제거

```
  select distinct t
  from Team t
  where t.name = '팀A'
  
  // SQL에 DISTINCT를 추가하지만 데이터가 다르므로 SQL결과에서 중복 제거 실패
```

- DISTINCT 추가로 애플리케이션에서 중복 제거 시도
- 같은 식별자를 가진 Team 엔티티 제거

# 페치조인과 일반조인의 차이
- 일반 조인 실행시 연관된 엔티티를 함께 조회하지 않음
- [JPQL]
```
  select t
  from Team t
  join t.members m 
  where t.name = '팀A'
```

- [SQL]
```
  SELECT T.*
  FROM TEAM T
  INNER JOIN MEMBER M
  ON T.ID = M.TEAM_ID
  WHERE T.NAME = '팀A'
```
- JPQL은 결과를 반환 할 때 연관관계 고려 X
- 단지 SELECT 절에 지정한 엔티티만 조회할 뿐
- 여기서는 팀 엔티티만 조회하고, 회원 엔티티는 조회 X
- 페치 조인을 사용할 때만 연관된 엔티티도 함께 조회(즉시 로딩)
- 페치 조인은 객체 그래프를 SQL 한번에 조회하는 개념

# 페치조인의 특징과 한계
- 페치 조인 대상에는 별칭을 줄 수없다.
1. 하이버 네이트는 가능, 가급적 사용 X
- 둘 이상의 컬렉션은 페치조인 할 수 없다.
- 컬렉션을 페치조인 하면 페이징 API(setFirstResult, setMaxResults)를 사용 할 수 없다.
1. 일대일, 다대일 같은 단일 값 연관 필드들은 페치 조인해도 페이징 가능
2. 하이버네이트는 경고 로그를 남기고 메모리에서 페이징(매우 위험)
- 연관된 엔티티들을 SQL 한번으로 조회 - 성능 최적화
- 엔티티에 직접 적용하는 글로벌 로딩 전략보다 우선함
```
  @OneToMany(fetch = FetchType.LAZY) // 글로벌 로딩 전략
```
- 실무에서 글로벌 로딩 전략은 모두 지연 로딩
- 최적화가 필요한 곳은 페치 조인 적용

# 페치조인 - 정리
- 모든 것을 페치 조인으로 해결 할 수는 없음
- 페치 조인은 객체 그래프를 유지할 때 사용하면 효과적
- 여러 테이블을 조인해서 엔티티가 가진 모양이 아닌 전혀 다른 결과를 내야하면,
페치 조인보다는 일반 조인을 사용하고 필요한 데이터들만 조회해서 DTO로 반환 하는것이 효과적
  

---
# 다형성 쿼리
# TYPE
- 조회 대상을 특정 자식으로 한정
- 예) Item중에 Book, Movie를 조회해라
- [JQPL]
```
  select i
  from Item i
  where type(i) IN (Book, Movie)
```

- [SQL]
```
  select i
  from i
  where i.DTYPE in ('B', 'M')
```

# TREAT
- 자바의 타입 캐스팅과 유사
- 상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용
- FROM, WHERE, SELECT(하이버네이트 지원) 사용
- 예) 부모인 Item 과 자식 Book이 있다.
- [JPQL]
```
  select i
  from Item i 
  where treat(i as Book).auther = 'kim'
```

-[SQL]
```
  select i
  from Item i 
  where i.DTYPE = 'B' 
  and i.auther = 'kim'
```






