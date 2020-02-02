package org.gnsg.gms.repository.search;

import org.gnsg.gms.domain.File;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link File} entity.
 */
public interface FileSearchRepository extends ElasticsearchRepository<File, Long> {
}
