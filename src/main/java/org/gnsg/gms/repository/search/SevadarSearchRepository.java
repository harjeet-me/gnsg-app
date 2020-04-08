package org.gnsg.gms.repository.search;

import org.gnsg.gms.domain.Sevadar;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Sevadar} entity.
 */
public interface SevadarSearchRepository extends ElasticsearchRepository<Sevadar, Long> {
}
