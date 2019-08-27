package com.jp.Model;


/**
 * Created by Administrator on 1/17/2017.
 */

public class Product {

  private int id;
  private long productTypeId;
  private String name;
  private String code;
  private int price;
  private String description;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public long getProductTypeId() {
    return productTypeId;
  }

  public void setProductTypeId(long productTypeId) {
    this.productTypeId = productTypeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
    this.price = price;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "Product{" +
        "id=" + id +
        ", productTypeId=" + productTypeId +
        ", name='" + name + '\'' +
        ", code='" + code + '\'' +
        ", price=" + price +
        ", description='" + description + '\'' +
        '}';
  }
}
