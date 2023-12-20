package com.price_comparison.model;

import javax.persistence.*;

@Entity
public class Comparison {

  @Id
  @SequenceGenerator(
    name = "comparison_id_seq",
    sequenceName = "comparison_id_seq",
    allocationSize = 1
  )
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "comparison_id_seq"
  )
  private Integer id;

  private float price;

  @OneToOne
  private Phone phone;

  private String url;

  public Comparison(float price, Phone phone, String url) {
    this.price = price;
    this.phone = phone;
    this.url = url;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }
}
