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
