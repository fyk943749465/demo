package cn.itcast.elasticsearch;

import cn.itcast.elasticsearch.pojo.Item;
import cn.itcast.elasticsearch.repository.ItemRepository;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.ParsedAvg;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void testCreate() {
        List<Item> list = new ArrayList<>();
        list.add(new Item(2L, "坚果手机7", "手机", "锤子", 3699.00, "http://image.leyou.com/13133.jpg"));
        list.add(new Item(3L, "华为META10", "手机", "华为", 4499.00, "http://image.leyou.com/13523.jpg"));
        list.add(new Item(4L, "小米Mix2s", "手机", "华为", 4299.00, "http://image.leyou.com/13573.jpg"));
        list.add(new Item(5L, "荣耀V10", "手机", "华为", 2799.00, "http://image.leyou.com/13525.jpg"));
//        this.itemRepository.save(item);

        this.itemRepository.saveAll(list);
    }

    @Test
    public void testFind() {
        Iterable<Item> allById = this.itemRepository.findAll(Sort.by("price").descending());
        allById.forEach(System.out::println);
    }

    @Test
    public void testFindByTitle() {

        List<Item> byPriceBetween = this.itemRepository.findByPriceBetween(3699d, 4499d);
        byPriceBetween.forEach(System.out::println);
//        List<Item> items = this.itemRepository.findByTitle("手机");
//        items.forEach(System.out::println);
    }

    @Test
    public void testSearch() {
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", "手机");
        Iterable<Item> search = this.itemRepository.search(matchQueryBuilder);

        search.forEach(System.out::println);
    }
    // 高级查询
    @Test
    public void testNative() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "手机"));
        Page<Item> search = this.itemRepository.search(queryBuilder.build());
        System.out.println(search.getTotalPages());
        System.out.println(search.getTotalElements());
        search.getContent().forEach(System.out::println);

    }

    @Test
    public void testPageSearch() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("category", "手机"));
        queryBuilder.withPageable(PageRequest.of(1, 2));
        Page<Item> search = this.itemRepository.search(queryBuilder.build());
        System.out.println(search.getTotalPages());
        System.out.println(search.getTotalElements());
        search.getContent().forEach(System.out::println);
    }

    @Test
    public void testSortSearch() {

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("category", "手机"));
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));

        Page<Item> search = this.itemRepository.search(queryBuilder.build());
        System.out.println(search.getTotalPages());
        System.out.println(search.getTotalElements());
        search.getContent().forEach(System.out::println);
    }

    // 聚合查询
    @Test
    public void testAggs() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brand"));
        // 添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        // 执行聚合查询
        AggregatedPage<Item> itemPage = (AggregatedPage<Item>)this.itemRepository.search(queryBuilder.build());
        // 解析聚合结果,根据聚合的类型以及字段类型进行强转,通过聚合名称获取聚合对象
        ParsedStringTerms brandAgg = (ParsedStringTerms)itemPage.getAggregation("brandAgg");

        List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();
        // 获取桶的集合
        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
        });

    }

    @Test
    public void testSubAggs() {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加聚合  添加子聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brand")
                .subAggregation(AggregationBuilders.avg("price_avg").field("price")));
        // 添加结果集过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        // 执行聚合查询
        AggregatedPage<Item> itemPage = (AggregatedPage<Item>)this.itemRepository.search(queryBuilder.build());
        // 解析聚合结果,根据聚合的类型以及字段类型进行强转,通过聚合名称获取聚合对象
        ParsedStringTerms brandAgg = (ParsedStringTerms)itemPage.getAggregation("brandAgg");

        List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();
        // 获取桶的集合
        buckets.forEach(bucket -> {
            System.out.println(bucket.getKeyAsString());
            System.out.println(bucket.getDocCount());
            Map<String, Aggregation> stringAggregationMap = bucket.getAggregations().asMap();

            ParsedAvg aggregation = (ParsedAvg)stringAggregationMap.get("price_avg");
            System.out.println(aggregation.getValue());

        });

    }

}
