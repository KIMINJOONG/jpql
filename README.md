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