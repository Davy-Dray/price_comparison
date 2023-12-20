package com.price_comparison.model;

import javax.persistence.*;

@Entity
public class Phone {

  @Id
  @SequenceGenerator(
    name = "phone_id_seq",
    sequenceName = "phone_id_seq",
    allocationSize = 1
  )
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "phone_id_seq"
  )
  Integer id;

  @OneToOne
  PhoneModel phoneModel;

  String color;

  Integer capacity;

  public Phone(PhoneModel phoneModel, String color, Integer capacity) {
    this.phoneModel = phoneModel;
    this.color = color;
    this.capacity = capacity;
  }

  public Phone() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public PhoneModel getPhoneModel() {
    return phoneModel;
  }

  public void setPhoneModel(PhoneModel phoneModel) {
    this.phoneModel = phoneModel;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }
}
