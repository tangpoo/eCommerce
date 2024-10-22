package com.potato.ecommerce.domain.product.entity;

import com.potato.ecommerce.domain.category.entity.ProductCategoryEntity;
import com.potato.ecommerce.domain.store.entity.StoreEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteOnlyProperty;

@Document(indexName = "products")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @Field(type = FieldType.Long)
    private Long productId;

    @Field(type = FieldType.Long)
    private Long storeId;

    @Field(type = FieldType.Long)
    private Long productCategory;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Long)
    private Long price;

    @Field(type = FieldType.Integer)
    private Integer stock;

    @Field(type = FieldType.Boolean)
    private Boolean isDeleted;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private LocalDateTime createdAt;

    public Product(ProductEntity productEntity) {
        this.productId = productEntity.getId();
        this.storeId = productEntity.getStore().getId();
        this.productCategory = productEntity.getProductCategory().getId();
        this.name = productEntity.getName();
        this.description = productEntity.getDescription();
        this.price = productEntity.getPrice();
        this.stock = productEntity.getStock();
        this.isDeleted = productEntity.getIsDeleted();
        this.createdAt = productEntity.getCreatedAt();
    }

}
