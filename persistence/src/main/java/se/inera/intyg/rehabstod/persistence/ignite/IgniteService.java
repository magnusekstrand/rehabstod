package se.inera.intyg.rehabstod.persistence.ignite;

/**
 * Created by eriklupander on 2016-10-06.
 */
public interface IgniteService {

    String getValue(String hsaId, String key);

    void putValue(String hsaId, String key, String value);

    void removeValue(String hsaId, String key);
}
