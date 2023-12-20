package com.price_comparison.web_scrapper;

import com.price_comparison.model.PhoneModel;
import com.price_comparison.repository.PhoneRepository;
import java.io.IOException;
import java.util.Arrays;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArgosScraper implements Runnable {

  private static String[] extractBrandModelCapacity(String input) {
    // Split the title into words
    String[] words = input.split("\\s+");

    // Check if there are at least three words
    if (words.length > 3) {
      // Extract the brand, model, and capacity
      return Arrays.copyOfRange(words, 2, 6);
    } else {
      // If there are not enough words, return an array of empty strings or handle it as needed
      return new String[] { "", "", "" };
    }
  }

  @Override
  public void run() {
    for (int i = 0; i <= 20; i++) {
      String url = "https://www.argos.co.uk/search/phones/opt/page:" + i + "/";

      Document doc = null;
      try {
        doc = Jsoup.connect(url).get();

        Elements productElements = doc.select("[data-test=product-list]");

        PhoneRepository phoneRepository = new PhoneRepository();

        for (Element e : productElements) {
          Elements priceElement = e.select("strong");
          String price = "";
          for (Element element : priceElement) {
            price = element.text().replace("£", "");
          }

          Elements titles = e.select(
            "[data-test=component-product-card-title]"
          );

          Element imageElement = e.selectFirst("img");

          String imageURL = (imageElement != null)
            ? imageElement.attr("src")
            : "N/A";

          Element productLinkElement = e.selectFirst(
            "[data-test=component-product-card-link]"
          );
          String productLink = (productLinkElement != null)
            ? productLinkElement.attr("href")
            : "N/A";

          String description = "";
          if (!titles.isEmpty()) {
            // Extract the brand, model, and capacity for each title
            for (Element title : titles) {
              String[] brandModelCapacity = extractBrandModelCapacity(
                title.text()
              );

              description = title.text();
              String brand = brandModelCapacity[0];
              if ((brand.equalsIgnoreCase("Iphone"))) {
                brandModelCapacity[0] = "Apple";
              }

              // Create a PhoneModel instance
              PhoneModel phoneModel = new PhoneModel(
                brandModelCapacity[0],
                brandModelCapacity[1],
                description,
                imageURL
              );

              String color = brandModelCapacity[6];
              // Save the details to the database
              phoneRepository.save(
                phoneModel,
                color,
                Integer.parseInt(brandModelCapacity[2]),
                Float.parseFloat(price),
                productLink
              );
            }
          } else {
            System.out.println("Title element not found for a product.");
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
