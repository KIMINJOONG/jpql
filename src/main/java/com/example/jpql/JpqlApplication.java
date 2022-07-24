package com.example.jpql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.*;
import java.util.List;

@SpringBootApplication
public class JpqlApplication {

    public static void main(String[] args) {

        EntityManagerFactory emf =  Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx =  em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            List<MemberDTO> resultList =  em.createQuery("select new com.example.jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class).getResultList();
            MemberDTO memberDTO = resultList.get(0);
            System.out.println("username : " + memberDTO.getUsername());
            System.out.println("age : " + memberDTO.getAge());
            tx.commit();

        } catch(Exception e) {
            tx.rollback();
        } finally {
            em.close();

        }
        emf.close();
    }

}
