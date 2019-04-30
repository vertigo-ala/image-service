package au.org.ala.images

import grails.converters.JSON
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.action.admin.indices.get.GetIndexRequest
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
import org.elasticsearch.action.search.SearchRequestBuilder
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
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder
import org.elasticsearch.search.sort.SortOrder

import javax.annotation.PreDestroy
import java.util.regex.Pattern
import javax.annotation.PostConstruct
import org.elasticsearch.node.Node

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
        data.metadata = [:]
        md.each {
            // Keys get lowercased here and when being searched for to make the case insensitive
            data.metadata[it.name.toLowerCase()] = it.value
        }

        def json = (data as JSON).toString()


        IndexRequest request = new IndexRequest("images")
        request.id( image.id.toString())
        request.source(json, XContentType.JSON)
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

//        IndexResponse response = client.prepareIndex("images", "image", image.id.toString()).setSource(json).execute().actionGet();
        ct.stop(true)
    }

    def deleteImage(Image image) {
        if (image) {
            DeleteResponse response = client.prepareDelete("images", "image", image.id.toString()).execute().actionGet();
        }
    }

    QueryResults<Image> simpleImageSearch(String query, GrailsParameterMap params) {
//        def qmap = [query:
//                            [filtered:
//                                 [query:
//                                          [query_string:
//                                                   [query: query?.toLowerCase()]
//                                          ]
//                                 ]
//                            ]
//                    ]
        def qmap = [query:
                          [simple_query_string:
                                   [query: query?.toLowerCase()]
                          ]
                   ]
        return search(qmap, params)
    }

    QueryResults<Image> search(Map query, GrailsParameterMap params) {
//        Map qmap = null
//        Map fmap = null
//        if (query.query) {
//            qmap = query.query
//        } else {
//            if (query.filter) {
//                fmap = query.filter
//            } else {
//                qmap = query
//            }
//        }

        log.debug "search params: ${params}"

        SearchRequest request = buildSearchRequest(JsonOutput.toJson(query), params, "images", [:])
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
    SearchRequest buildSearchRequest(String queryString, Map params, String index, Map geoSearchCriteria = [:]) {
        SearchRequest request = new SearchRequest()
        request.searchType SearchType.DFS_QUERY_THEN_FETCH

        // set indices and types
        request.indices(index)
        def types = []
        if (params.types && params.types instanceof Collection<String>) {
            types = params.types
        }
        request.types(types as String[])

        QueryBuilder query = QueryBuilders.queryStringQuery("*")

        // set pagination stuff
        SearchSourceBuilder source = pagenateQuery(params).query(query)
//
//        // add facets
//        addFacets(params.facets, params.fq, params.flimit, params.fsort).each {
//            source.query(it)
//        }
//
//        if(params.rangeFacets){
//            addRangeFacets(params.rangeFacets as List).each {
//                source.query(it)
//            }
//        }
//
//        if(params.histogramFacets){
//            addHistogramFacets(params.histogramFacets).each {
//                source.query(it)
//            }
//        }

        if (params.highlight) {
            source.highlight(new HighlightBuilder().preTags("<b>").postTags("</b>").field("_all", 60, 2))
        }

        if (params.omitSource) {
            source.noFields()
        }

        request.source(source)

        return request
    }

    private SearchSourceBuilder pagenateQuery(Map params) {
        SearchSourceBuilder source = new SearchSourceBuilder()
        source.from(params.offset ? params.offset as int : 0)
        source.size(params.max ? params.max as int : 10)
//        source.explain(params.explain ?: false)
//        if (params.sort) {
//            source.sort(params.sort, SortOrder.valueOf(params.order?.toUpperCase() ?: "ASC"))
//        }
        source
    }

    private QueryBuilder buildQuery(String query, Map params, Map geoSearchCriteria = null, String index) {
        QueryBuilder queryBuilder
        List filters = []

        if (params.terms) {
            filters << QueryBuilders.termQuery(params.terms.field, params.terms.values)
        }

        QueryStringQueryBuilder qsQuery = QueryBuilders.queryStringQuery(query)

        if (filters) {
            BoolQueryBuilder builder = QueryBuilders.boolQuery()
            builder.must(*filters)

            queryBuilder = builder.must(qsQuery) //QueryBuilders.termQuery(qsQuery). builder)
        }
        else {
            queryBuilder = qsQuery
        }

        if (params.weightResultsByEntity) {
            queryBuilder = applyWeightingToEntities(queryBuilder)
        }

        queryBuilder
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
            } else {
                log.info "Index already exists"
            }

        } catch (Exception e) {
            log.error ("Error creating index for images: ${e.message}", e)
        }
    }

    def searchUsingCriteria(List<SearchCriteria> criteriaList, GrailsParameterMap params) {

        def metaDataPattern = Pattern.compile("^(.*)[:](.*)\$")
        // split out by criteria type
        def criteriaMap = criteriaList.groupBy { it.criteriaDefinition.type }


        def filter  = criteriaList ? FilterBuilders.boolFilter() : FilterBuilders.matchAllFilter()

        def list = criteriaMap[CriteriaType.ImageProperty]
        if (list) {
            ESSearchCriteriaUtils.buildCriteria(filter, list)
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

                    filter.must(QueryBuilders.queryFilter(QueryBuilders.queryStringQuery("${matcher.group(1)}:${term}")))
                }
            }
        }

        return executeFilterSearch(filter, params)
    }

    QueryResults<Image> searchByMetadata(String key, List<String> values, GrailsParameterMap params) {

        def queryString = values.collect { key.toLowerCase() + ":\"" + it + "\""}.join(" OR ")
        QueryStringQueryBuilder builder = QueryBuilders.queryStringQuery(queryString)

        //DM - Im unclear as to why this stopped working !!!
//        def filter = FilterBuilders.orFilter()
//        values.each { value ->
//            // Metadata keys are lowercased when indexed
//            filter.add(FilterBuilders.termFilter(key.toLowerCase(), value))
//        }

//        return executeFilterSearch(filter, params)
        builder.defaultField("content")
        def searchRequestBuilder = client.prepareSearch("images").setSearchType(SearchType.QUERY_THEN_FETCH)
        searchRequestBuilder.setQuery(builder)
        return executeSearch(searchRequestBuilder, params)
    }

    private QueryResults<Image> executeFilterSearch(QueryBuilder filterBuilder, GrailsParameterMap params) {
        def searchRequestBuilder = client.prepareSearch("images").setSearchType(SearchType.QUERY_THEN_FETCH)
        searchRequestBuilder.setPostFilter(filterBuilder)
        return executeSearch(searchRequestBuilder, params)
    }

    private QueryResults<Image> executeSearch(SearchRequestBuilder searchRequestBuilder, GrailsParameterMap params) {

        try {
            if (params?.offset) {
                searchRequestBuilder.setFrom(params.int("offset"))
            }

            if (params?.max) {
                searchRequestBuilder.setSize(params.int("max"))
            } else {
                searchRequestBuilder.setSize(Integer.MAX_VALUE) // probably way too many!
            }

            if (params?.sort) {
                def order = params?.order == "asc" ? SortOrder.ASC : SortOrder.DESC
                searchRequestBuilder.addSort(params.sort as String, order)
            }

            def ct = new CodeTimer("Index search")
            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
            ct.stop(true)

            ct = new CodeTimer("Object retrieval (${searchResponse.hits.hits.length} of ${searchResponse.hits.totalHits} hits)")
            def imageList = []
            if (searchResponse.hits) {
                searchResponse.hits.each { hit ->
                    imageList << Image.get(hit.id.toLong())
                }
            }
            ct.stop(true)

            return new QueryResults<Image>(list: imageList, totalCount: searchResponse?.hits?.totalHits ?: 0)
        } catch (SearchPhaseExecutionException e) {
            log.warn(".SearchPhaseExecutionException thrown - this is expected behaviour for a new empty system.")
            return new QueryResults<Image>(list: [], totalCount: 0)
        } catch (Exception e) {
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

    def ping() {
        logService.log("ElasticSearch Service is ${node ? '' : 'NOT' } alive.")
    }
}
