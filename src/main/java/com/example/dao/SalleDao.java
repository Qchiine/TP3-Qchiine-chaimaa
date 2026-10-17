package com.example.dao;

import com.example.model.Salle;

import javax.persistence.EntityManager;
import java.util.List;

public class SalleDao {

    private final EntityManager em;

    public SalleDao(EntityManager em) {
        this.em = em;
    }

    /** Requete JPQL : salles equipees d'un equipement donne. */
    public List<Salle> findByEquipementNom(String nomEquipement) {
        return em.createQuery(
                        "SELECT DISTINCT s FROM Salle s JOIN s.equipements e WHERE e.nom = :nom", Salle.class)
                .setParameter("nom", nomEquipement)
                .getResultList();
    }
}
