package org.bgamard.mytube.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.bgamard.mytube.jpa.UpdatableUuid;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@RegisterForReflection(targets = { UUID[].class })
public abstract class BaseEntity extends PanacheEntityBase {
    @Id
    @UpdatableUuid
    @Column(nullable = false)
    public UUID id;

    public String toString() {
        return this.getClass().getSimpleName() + "<" + this.id + ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}