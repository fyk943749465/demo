package cn.itcast.elasticsearch.pojo;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Document(indexName = "item", shards = 1, replicas = 0)
public class Item {

    @Id
    Long id;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    String title;
    @Field(type = FieldType.Keyword)
    String category;
    @Field(type = FieldType.Keyword)
    String brand;
    @Field(type = FieldType.Double)
    Double price;
    @Field(type = FieldType.Keyword, index = false)
    String images;
}
