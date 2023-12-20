package com.price_comparison.model;

import javax.persistence.*;

@Entity
public class PhoneModel {

  @Id
  @SequenceGenerator(
    name = "phoneModel_id_seq",
    sequenceName = "phoneModel_id_seq",
    allocationSize = 1
  )
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "phoneModel_id_seq"
  )
  private Integer id;

  public PhoneModel() {}

  public PhoneModel(
    String brand,
    String model,
    String description,
    String imageURL
  ) {
    this.brand = brand;
    this.model = model;
    this.description = description;
    this.imageURL = imageURL;
  }

  @Column(nullable = false)
  private String brand;

  @Column(nullable = false)
  private String model;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String imageURL;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }
}
