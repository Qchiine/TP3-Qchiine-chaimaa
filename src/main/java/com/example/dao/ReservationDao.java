package com.example.dao;

import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class ReservationDao {

    private final EntityManager em;

    public ReservationDao(EntityManager em) {
        this.em = em;
    }

    /** Requete JPQL : reservations d'un utilisateur. */
    public List<Reservation> findByUtilisateur(Utilisateur utilisateur) {
        return em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.utilisateur = :utilisateur", Reservation.class)
                .setParameter("utilisateur", utilisateur)
                .getResultList();
    }

    /** Requete JPQL : reservations d'une salle. */
    public List<Reservation> findBySalle(Salle salle) {
        return em.createQuery(
                        "SELECT r FROM Reservation r WHERE r.salle = :salle", Reservation.class)
                .setParameter("salle", salle)
                .getResultList();
    }

    /** Verifie qu'une salle est disponible sur une periode donnee. */
    public boolean isSalleDisponible(Salle salle, LocalDate debut, LocalDate fin) {
        Long chevauchements = em.createQuery(
                        "SELECT COUNT(r) FROM Reservation r WHERE r.salle = :salle "
                                + "AND r.dateDebut < :fin AND r.dateFin > :debut", Long.class)
                .setParameter("salle", salle)
                .setParameter("debut", debut)
                .setParameter("fin", fin)
                .getSingleResult();
        return chevauchements == 0;
    }
}
