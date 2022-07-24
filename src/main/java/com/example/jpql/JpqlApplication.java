package com.example.jpql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.*;

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
            em.persist(member);

            TypedQuery<Member> query1 =  em.createQuery("select m from Member m", Member.class);
            TypedQuery<String> query2 =  em.createQuery("select m.username from Member m", Member.class);
            Query query3 =  em.createQuery("select m.username, m.age from Member m", Member.class);

            tx.commit();

        } catch(Exception e) {
            tx.rollback();
        } finally {
            em.close();

        }
        emf.close();
    }

}
