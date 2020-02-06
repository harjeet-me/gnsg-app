package org.gnsg.gms.repository.search;

import org.gnsg.gms.domain.Program;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Program} entity.
 */
public interface ProgramSearchRepository extends ElasticsearchRepository<Program, Long> {
}
