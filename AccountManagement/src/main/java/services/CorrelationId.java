package services;

import java.util.UUID;

public class CorrelationId {
    private final UUID id;

    public CorrelationId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public static CorrelationId randomId() {
        return new CorrelationId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return "CorrelationId{" +
                "id=" + id +
                '}';
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CorrelationId that = (CorrelationId) obj;
        return id != null ? id.equals(that.id) : that.id == null;
    }
}
