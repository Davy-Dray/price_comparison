package com.price_comparison.repository;

import com.price_comparison.model.Comparison;
import com.price_comparison.model.Phone;
import com.price_comparison.model.PhoneModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class PhoneRepository {

  private static final SessionFactory sessionFactory;

  static {
    sessionFactory =
            new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
  }

  private static SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void save(PhoneModel phoneModel, String color, int capacity, float price, String url) {
    Session session = getSessionFactory().getCurrentSession();
    session.beginTransaction();

    PhoneModel existingPhoneModel = findPhoneModelByBrandAndModel(
            phoneModel.getBrand(),
            phoneModel.getModel(),
            session
    );

    Phone phone;
    if (existingPhoneModel == null) {
      // If PhoneModel doesn't exist, save it
      session.save(phoneModel);

      // Create a new Phone instance with the newly saved PhoneModel
      phone = new Phone(phoneModel, color, capacity);
      session.save(phone);

    } else {
      // If PhoneModel already exists, use it
      phoneModel = existingPhoneModel;
      phone = new Phone(phoneModel, color, capacity);

      // Save the phone before creating and saving the Comparison
      session.save(phone);

      String shortenedUrl = url.substring(0, Math.min(url.length(), 255));
      // Now that phone is saved, you can create and save the Comparison
      Comparison comparison = new Comparison(price, phone, shortenedUrl);
      session.save(comparison);
    }


    session.getTransaction().commit();
  }

  private static PhoneModel findPhoneModelByBrandAndModel(
          String brand,
          String model,
          Session session
  ) {
    // Query to find a phone by brand and model
    String queryString =
            "FROM PhoneModel WHERE brand = :brand AND model = :model";
    return session
            .createQuery(queryString, PhoneModel.class)
            .setParameter("brand", brand)
            .setParameter("model", model)
            .setMaxResults(1) // We expect at most one result
            .uniqueResult();
  }
}
