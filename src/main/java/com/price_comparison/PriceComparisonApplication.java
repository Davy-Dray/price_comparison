package com.price_comparison;

import com.price_comparison.web_scrapper.ArgosScraper;
import com.price_comparison.web_scrapper.EBayPhoneScraper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class PriceComparisonApplication {

  public static void main(String[] args) throws InterruptedException {
    try {
      // Load Hibernate configuration from hibernate.cfg.xml
      Configuration configuration = new Configuration()
        .configure("hibernate.cfg.xml");

      // Build a session factory from the configuration
      SessionFactory sessionFactory = configuration.buildSessionFactory();

      // Open a session from the session factory
      try (Session session = sessionFactory.openSession()) {
        System.out.println("Connected to the database successfully!");
      }

      // Close the session factory when done
      sessionFactory.close();
    } catch (Exception e) {
      System.err.println("Error connecting to the database: " + e.getMessage());
    }

    EBayPhoneScraper phoneScrapper = new EBayPhoneScraper();
    ArgosScraper argosScraper = new ArgosScraper();
    Thread thread1 = new Thread(phoneScrapper);
    Thread thread2 = new Thread(argosScraper);

    thread1.start();
    thread2.start();

    thread1.join();
    thread2.join();
  }
}
