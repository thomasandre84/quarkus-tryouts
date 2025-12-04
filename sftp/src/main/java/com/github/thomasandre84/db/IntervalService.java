package com.github.thomasandre84.db;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class IntervalService {

    @Inject
    private EntityManager em;


    @Transactional
    public InitialInterval createInitialInterval() {
        InitialInterval initialInterval = new InitialInterval();
        em.persist(initialInterval);
        return initialInterval;
    }

    @Transactional
    public Interval createInterval() {
        Interval interval = new Interval();
        em.persist(interval);
        return interval;
    }

    @Transactional
    public IntervalRelation createIntervalRelation(int initialInterval, int interval) {
        InitialInterval initialIntervalEntity = em.find(InitialInterval.class, initialInterval);
        Interval intervalEntity = em.find(Interval.class, interval);
        IntervalRelation intervalRelation = new IntervalRelation(initialIntervalEntity, intervalEntity);
        em.persist(intervalRelation);
        return intervalRelation;
    }

    @Transactional
    public void deleteInterval(int intervalId) {
        Interval interval = em.find(Interval.class, intervalId);
        em.remove(interval);
    }

    public List<IntervalRelation> getIntervalRelations() {
        return em.createQuery("SELECT ir FROM IntervalRelation ir", IntervalRelation.class)
                 .getResultList();
    }

}
