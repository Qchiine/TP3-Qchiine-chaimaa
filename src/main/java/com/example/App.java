package com.example;

import com.example.model.*;
import javax.persistence.*;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gestion-reservations-pu");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Utilisateur user = new Utilisateur();
            user.setEmail("alice@example.com");
            user.setNom("Alice Dupont");

            Salle salle = new Salle();
            salle.setNom("Salle A");

            Equipement projecteur = new Equipement();
            projecteur.setNom("Projecteur");

            salle.addEquipement(projecteur);

            Reservation reservation = new Reservation();
            reservation.setSalle(salle);
            reservation.setUtilisateur(user);

            user.addReservation(reservation);

            em.persist(user); // cascade persist propagee

            em.getTransaction().commit();

            ExercicesSupplementaires.executer(em, user, salle, projecteur);

            em.getTransaction().begin();
            Utilisateur utilisateur = em.find(Utilisateur.class, 1L);
            Reservation res = utilisateur.getReservations().iterator().next();
            utilisateur.getReservations().remove(res);
            em.getTransaction().commit();

            em.getTransaction().begin();
            utilisateur = em.find(Utilisateur.class, 1L);
            em.remove(utilisateur);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("[ERREUR de persistance] " + e.getMessage());
        } finally {
            if (em.isOpen()) {
                em.close();
            }
            emf.close();
        }
    }
}
