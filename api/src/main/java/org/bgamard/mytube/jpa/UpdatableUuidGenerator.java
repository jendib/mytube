package org.bgamard.mytube.jpa;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;
import java.util.UUID;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

public class UpdatableUuidGenerator implements BeforeExecutionGenerator {
    @Override
    public EnumSet<EventType> getEventTypes() {
        return INSERT_ONLY;
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        return currentValue == null ? UUID.randomUUID() : currentValue;
    }
}