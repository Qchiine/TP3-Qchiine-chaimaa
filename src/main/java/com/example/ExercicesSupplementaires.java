package com.example;

import com.example.dao.ReservationDao;
import com.example.dao.SalleDao;
import com.example.model.Equipement;
import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

/**
 * Etape 13 : exercices supplementaires pour approfondir.
 * 1. Validation des dates
 * 2. Disponibilite des salles
 * 3. Requetes JPQL
 * 4. Gestion des exceptions
 */
public class ExercicesSupplementaires {

    public static void executer(EntityManager em, Utilisateur user, Salle salle, Equipement projecteur) {
        exercice3RequetesJpql(em, user, salle, projecteur);
        exercice2Disponibilite(em, salle);
        exercice1ValidationDates(em, user, salle);
        exercice4GestionExceptions(em, salle);
    }

    /** Exercice 3 : requetes JPQL. */
    private static void exercice3RequetesJpql(EntityManager em, Utilisateur user, Salle salle, Equipement projecteur) {
        ReservationDao reservationDao = new ReservationDao(em);
        SalleDao salleDao = new SalleDao(em);

        List<Reservation> reservationsUtilisateur = reservationDao.findByUtilisateur(user);
        System.out.println(">>> JPQL - reservations de " + user.getNom() + " : " + reservationsUtilisateur.size());

        List<Reservation> reservationsSalle = reservationDao.findBySalle(salle);
        System.out.println(">>> JPQL - reservations de " + salle.getNom() + " : " + reservationsSalle.size());

        List<Salle> sallesEquipees = salleDao.findByEquipementNom(projecteur.getNom());
        System.out.println(">>> JPQL - salles equipees d'un " + projecteur.getNom() + " : " + sallesEquipees);
    }

    /** Exercice 2 : disponibilite des salles sur une periode. */
    private static void exercice2Disponibilite(EntityManager em, Salle salle) {
        ReservationDao reservationDao = new ReservationDao(em);
        LocalDate debut = LocalDate.now().plusDays(10);
        LocalDate fin = LocalDate.now().plusDays(12);
        System.out.println(">>> Disponibilite de " + salle.getNom() + " du " + debut + " au " + fin
                + " : " + reservationDao.isSalleDisponible(salle, debut, fin));
    }

    /** Exercice 1 : validation des dates (dateDebut < dateFin). */
    private static void exercice1ValidationDates(EntityManager em, Utilisateur user, Salle salle) {
        System.out.println(">>> Validation des dates - date de fin anterieure a la date de debut");
        em.getTransaction().begin();
        try {
            Reservation invalide = new Reservation(LocalDate.now().plusDays(5), LocalDate.now().plusDays(2));
            invalide.setSalle(salle);
            invalide.setUtilisateur(user);
            em.persist(invalide);
            em.getTransaction().commit();
            System.out.println(">>> Aucune erreur : la contrainte n'a pas ete appliquee");
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.clear();
            System.out.println(">>> Reservation refusee : " + rootCause(e).getMessage());
        }
    }

    /** Exercice 4 : gestion robuste des erreurs lors des operations de persistance. */
    private static void exercice4GestionExceptions(EntityManager em, Salle salle) {
        System.out.println(">>> Gestion des exceptions - recherche d'une entite inexistante");
        try {
            Salle inexistante = em.find(Salle.class, 9999L);
            if (inexistante == null) {
                throw new IllegalStateException("Salle 9999 introuvable");
            }
        } catch (RuntimeException e) {
            System.out.println(">>> Erreur geree proprement : " + e.getMessage());
        }
    }

    private static Throwable rootCause(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}
