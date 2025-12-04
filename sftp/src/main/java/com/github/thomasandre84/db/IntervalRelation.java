package com.github.thomasandre84.db;

import jakarta.persistence.*;

@Entity
@Table(name = "interval_relation", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "interval_id", "initial_interval_id" })
})
public class IntervalRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "initial_interval_id", nullable = false)
    private InitialInterval initialInterval;

    @OneToOne
    @JoinColumn(name = "interval_id", nullable = false)
    private Interval interval;

    public IntervalRelation(InitialInterval initialInterval, Interval interval) {
        this.initialInterval = initialInterval;
        this.interval = interval;
    }

    protected IntervalRelation() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public InitialInterval getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(InitialInterval initialInterval) {
        this.initialInterval = initialInterval;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }
}
