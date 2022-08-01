package com.example.jpql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.*;
import java.util.List;

@SpringBootApplication
public class JpqlApplication {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Team team = new Team();
            team.setName("team");
            em.persist(team);

            Member member = new Member();
            member.setUsername("관리자1");
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            em.persist(member2);



            em.flush();
            em.clear();

            String query = "select function('group_concat', m.username) from Member m";

            List<String> result = em.createQuery(query, String.class)
                    .setFirstResult(0)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("result = " + result.size());

            for (String s : result) {
                System.out.println("s = " + s);
            }


            tx.commit();

        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();

        }
        emf.close();
    }

}
