package au.org.ala.images

import com.opencsv.CSVWriter
import grails.converters.JSON
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.common.xcontent.XContentType
import groovy.json.JsonOutput
import grails.web.servlet.mvc.GrailsParameterMap
import org.apache.http.HttpHost
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.delete.DeleteResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.index.IndexResponse
import org.elasticsearch.action.search.SearchPhaseExecutionException
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.cluster.ClusterState
import org.elasticsearch.cluster.metadata.IndexMetaData
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.index.query.QueryStringQueryBuilder
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder
import org.elasticsearch.search.sort.SortOrder

import javax.annotation.PreDestroy
import java.util.regex.Pattern
import javax.annotation.PostConstruct

class ElasticSearchService {

    def logService
    def grailsApplication

    private RestHighLevelClient client

    @PostConstruct
    def initialize() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
        initialiseIndex()
    }

    @PreDestroy
    def destroy() {
        if (client){
            try {
                client.close()
            } catch (Exception e){
                log.error("Error thrown trying to close down client", e)
            }
        }
    }

    def reinitialiseIndex() {
        try {
            def ct = new CodeTimer("Index deletion")
            def response = client.indices().delete(new DeleteIndexRequest("images"), RequestOptions.DEFAULT)
            if (response.isAcknowledged()) {
                log.info "The index is removed"
            } else {
                log.error "The index could not be removed"
            }
            ct.stop(true)

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex)
            // failed to delete index - maybe because it didn't exist?
        }
        initialiseIndex()
    }

    def indexImage(Image image) {
        if (!image){
            log.error("Supplied image was null")
        }
        def ct = new CodeTimer("Index Image ${image.id}")
        // only add the fields that are searchable. They are marked with an annotation
        def fields = Image.class.declaredFields
        def data = [:]
        fields.each { field ->
            if (field.isAnnotationPresent(SearchableProperty)) {
                data[field.name] = image."${field.name}"
            }
        }

        def md = ImageMetaDataItem.findAllByImage(image)
//        data.metadata = [:]
//        md.each {
//            // Keys get lowercased here and when being searched for to make the case insensitive
//            data.metadata[it.name.toLowerCase()] = it.value
//        }

        if(image.recognisedLicense) {
            data.recognisedLicence = image.recognisedLicense.acronym
        } else {
            data.recognisedLicence = "Unrecognised license"
        }

        if(image.creator){
            image.creator = image.creator.replaceAll("[\"|']", "")
        }

        def json = (data as JSON).toString()

        IndexRequest request = new IndexRequest("images")
        request.id( image.id.toString())
        request.source(json, XContentType.JSON)
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        ct.stop(true)
    }

    def deleteImage(Image image) {
        if (image) {
            DeleteResponse response = client.delete(new DeleteRequest("images", image.id.toString()), RequestOptions.DEFAULT)
            log.info(response.status())
        }
    }

    QueryResults<Image> simpleImageSearch(List<SearchCriteria> searchCriteria, GrailsParameterMap params) {
        log.debug "search params: ${params}"
        SearchRequest request = buildSearchRequest(params, searchCriteria, "images")
        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT)
        def imageList = []
        if (searchResponse.hits) {
            searchResponse.hits.each { hit ->
                imageList << Image.get(hit.id.toLong())
            }
        }
        QueryResults<Image> qr = new QueryResults<Image>()
        qr.list = imageList
        qr.totalCount = searchResponse.hits.totalHits.value

        searchResponse.aggregations.each {
            def facet = [:]
            it.buckets.each { bucket ->
                facet[bucket.getKeyAsString()] = bucket.getDocCount()
            }
            qr.aggregations.put(it.name, facet)
        }
        qr
    }


    /**
     * Execute the search using a map query.
     *
     * @param query
     * @param params
     * @return
     */
    QueryResults<Image> search(Map query, GrailsParameterMap params) {
        log.debug "search params: ${params}"
        SearchRequest request = buildSearchRequest(JsonOutput.toJson(query), params, "images")
        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT)
        def imageList = []
        if (searchResponse.hits) {
            searchResponse.hits.each { hit ->
                imageList << Image.get(hit.id.toLong())
            }
        }
        QueryResults<Image> qr = new QueryResults<Image>()
        qr.list = imageList
        qr.totalCount = searchResponse.hits.totalHits.value
        qr
    }

    /**
     * Build the search request object from query and params
     *
     * @param queryString
     * @param params
     * @param index index name
     * @param geoSearchCriteria geo search criteria.
     * @return SearchRequest
     */
    SearchRequest buildSearchRequest(Map params, List<SearchCriteria> criteriaList, String index) {

        SearchRequest request = new SearchRequest()
        request.searchType SearchType.DFS_QUERY_THEN_FETCH

        // set indices and types
        request.indices(index)
        def types = []
        if (params.types && params.types instanceof Collection<String>) {
            types = params.types
        }
        request.types(types as String[])

        //create query builder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
        boolQueryBuilder.must(QueryBuilders.queryStringQuery(params.q ?: "*:*"))

        // Add FQ query filters
        def filterQueries = params.findAll { it.key == 'fq'}
        if (filterQueries) {
            filterQueries.each {

                if(it.value instanceof String[]){
                    it.value.each { filter ->
                        def kv = filter.split(":")
                        boolQueryBuilder.must(QueryBuilders.termQuery(kv[0], kv[1]))
                    }
                } else {
                    def kv = it.value.split(":")
                    boolQueryBuilder.must(QueryBuilders.termQuery(kv[0], kv[1]))
                }
            }
        }

        //add search criteria
        boolQueryBuilder = createQueryFromCriteria(boolQueryBuilder, criteriaList)


        // set pagination stuff
        SearchSourceBuilder source = pagenateQuery(params).query(boolQueryBuilder)

        // request aggregations (facets)
        grailsApplication.config.facets.each { facet ->
            source.aggregation(AggregationBuilders.terms(facet as String).field(facet as String))
        }

        //ask for the total
        source.trackTotalHits(true)

        if (params.highlight) {
            source.highlight(new HighlightBuilder().preTags("<b>").postTags("</b>").field("_all", 60, 2))
        }

        request.source(source)

        request
    }

    private SearchSourceBuilder pagenateQuery(Map params) {
        SearchSourceBuilder source = new SearchSourceBuilder()
        source.from(params.offset ? params.offset as int : 0)
        source.size(params.max ? params.max as int : 10)
        source.sort('dateUploaded', SortOrder.DESC)
        source
    }

    private def initialiseIndex() {
        try {

            boolean indexExists  = client.indices().exists(new org.elasticsearch.client.indices.GetIndexRequest("images"), RequestOptions.DEFAULT)
            if (!indexExists){
                CreateIndexRequest request = new CreateIndexRequest("images")
                CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT)
                if (createIndexResponse.isAcknowledged()) {
                    log.info "Successfully created index and mappings for images"
                } else {
                    log.info "UN-Successfully created index and mappings for images"
                }

                PutMappingRequest putMappingRequest = new PutMappingRequest("images")
                putMappingRequest.type("images")
                putMappingRequest.source(
                        """{
                                  "properties": {
                                    "dateUploaded": {
                                      "type": "date"
                                    },
                                    "dataResourceUid": {
                                      "type": "keyword"
                                    },               
                                    "license": {
                                      "type": "keyword"
                                    },  
                                    "recognisedLicence": {
                                      "type": "keyword"
                                    },                                     
                                    "creator": {
                                      "type": "keyword"
                                    }                                                                                             
                                  }
                                }""",
                        XContentType.JSON);
                def resp = client.indices().putMapping(putMappingRequest, RequestOptions.DEFAULT)
            } else {
                log.info "Index already exists"
            }

        } catch (Exception e) {
            log.error ("Error creating index for images: ${e.message}", e)
        }
    }

    /**
     * Create a boolean elastic search query builder from the supplied criteria.
     * @param criteriaList
     * @return
     */
    private BoolQueryBuilder createQueryFromCriteria(BoolQueryBuilder boolQueryBuilder, List<SearchCriteria> criteriaList) {

        def metaDataPattern = Pattern.compile("^(.*)[:](.*)\$")

        // split out by criteria type
        def criteriaMap = criteriaList.groupBy { it.criteriaDefinition.type }

        def list = criteriaMap[CriteriaType.ImageProperty]
        if (list) {
            ESSearchCriteriaUtils.buildCriteria(boolQueryBuilder, list)
        }

        list = criteriaMap[CriteriaType.ImageMetadata]
        if (list) {
            for (int i = 0; i < list.size(); ++i) {
                def criteria = list[i]
                // need to split the metadata name out of the value...
                def matcher = metaDataPattern.matcher(criteria.value)
                if (matcher.matches()) {
                    def term = matcher.group(2)?.replaceAll('\\*', '%')
                    term = term.replaceAll(":", "\\:")

                    boolQueryBuilder.must(QueryBuilders.queryFilter(QueryBuilders.queryStringQuery("${matcher.group(1)}:${term}")))
                }
            }
        }

        boolQueryBuilder
    }

    QueryResults<Image> searchByMetadata(String key, List<String> values, GrailsParameterMap params) {
        def queryString = values.collect { key.toLowerCase() + ":\"" + it + "\""}.join(" OR ")
        QueryStringQueryBuilder builder = QueryBuilders.queryStringQuery(queryString)
        builder.defaultField("content")
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
        searchSourceBuilder.query(builder)
        return executeSearch(searchSourceBuilder, params)
    }

    private QueryResults<Image> executeSearch(GrailsParameterMap params) {

        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()

            QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(params.q)

            def filterQueries = params.findAll { it.key == 'fq'}

            def filters = []

            if (filterQueries) {
                filterQueries.each {

                    if(it.value instanceof String[]){
                        it.value.each { filter ->
                            def kv = filter.split(":")
                            filters << QueryBuilders.termQuery(kv[0], kv[1])
                        }
                    } else {
                        def kv = it.value.split(":")
                        filters << QueryBuilders.termQuery(kv[0], kv[1])
                    }
                }
            }

            if (filters) {
                BoolQueryBuilder builder = QueryBuilders.boolQuery()
                filters.each {
                    builder.must(it)
                }
                queryBuilder = builder.must(queryBuilder) //QueryBuilders.termQuery(qsQuery). builder)
            }


            createQueryFromCriteria()


            if (params?.offset) {
                searchSourceBuilder.from(params.int("offset"))
            }

            if (params?.max) {
                searchSourceBuilder.size(params.int("max"))
            } else {
                searchSourceBuilder.size(Integer.MAX_VALUE) // probably way too many!
            }

            if (params?.sort) {
                def order = params?.order == "asc" ? SortOrder.ASC : SortOrder.DESC
                searchSourceBuilder.sort(params.sort as String, order)
            }

            // aggregations = facets
            grailsApplication.config.facets.each { facet ->
                searchSourceBuilder.aggregation(AggregationBuilders.terms(facet as String).field(facet as String))
            }

            def ct = new CodeTimer("Index search")
            SearchRequest searchRequest = new SearchRequest()
            searchRequest.indices("images")
            searchRequest.source(searchSourceBuilder)
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT)

            ct.stop(true)

            ct = new CodeTimer("Object retrieval (${searchResponse.hits.hits.length} of ${searchResponse.hits.totalHits} hits)")
            def imageList = []
            def aggregations = [:]
            if (searchResponse.hits) {
                searchResponse.hits.each { hit ->
                    imageList << Image.get(hit.id.toLong())
                }
                searchResponse.aggregations.each {
                    def facet = [:]
                    it.buckets.each { bucket ->
                        facet[bucket.getKeyAsString()] = bucket.getDocCount()
                    }
                    aggregations.put(it.name, facet)
                }
            }
            ct.stop(true)

            return new QueryResults<Image>(list: imageList, aggregations:aggregations, totalCount: searchResponse?.hits?.totalHits.value ?: 0)
        } catch (SearchPhaseExecutionException e) {
            log.warn(".SearchPhaseExecutionException thrown - this is expected behaviour for a new empty system.")
            return new QueryResults<Image>(list: [], totalCount: 0)
        } catch (Exception e) {
            e.printStackTrace()
            log.warn("Exception thrown - this is expected behaviour for a new empty system.")
            return new QueryResults<Image>(list: [], totalCount: 0)
        }
    }

    def getMetadataKeys() {
        ClusterState cs = client.admin().cluster().prepareState().execute().actionGet().getState();
        IndexMetaData imd = cs.getMetaData().index("images")
        Map mdd = imd.mapping("image").sourceAsMap()
        Map metadata = mdd?.properties?.metadata?.properties
        def names = []
        if (metadata) {
            names = metadata.collect { it.key }
        }
        return names
    }
}
