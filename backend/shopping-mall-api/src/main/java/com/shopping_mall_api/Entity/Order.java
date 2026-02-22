package com.shopping_mall_api.Entity;

import com.shopping_mall_api.TableNames;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLUpdates;

import javax.naming.Name;
import java.util.Date;

@Getter
@Entity(name = TableNames.orderTableName)
@Table(name = TableNames.orderTableName)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Date orderDate;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}
