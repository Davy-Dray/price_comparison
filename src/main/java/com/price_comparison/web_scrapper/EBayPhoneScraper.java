package com.price_comparison.web_scrapper;

import com.price_comparison.model.PhoneModel;
import com.price_comparison.repository.PhoneRepository;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EBayPhoneScraper implements Runnable {

  @Override
  public void run() {
    for (int i = 1; i <= 20; i++) {
      try {
        String ebayLink =
          "https://www.ebay.com/sch/i.html?_from=R40&_nkw=phones&_sacat=0&_pgn=" +
          i +
          "&rt=nc";

        Document doc = Jsoup.connect(ebayLink).get();

        Elements products = doc.select(".s-item");

        for (Element product : products) {
          Element titleElement = product.selectFirst(".s-item__title");
          String title = (titleElement != null) ? titleElement.text() : "N/A";

          Element priceElement = product.selectFirst(".s-item__price");
          String price = (priceElement != null)
            ? extractSinglePrice(priceElement.text().replace("$", ""))
            : "N/A";
          String newPrice = price.replace(",", "");
          String brand = extractBrand(title);

          String model = extractModel(title);
          String description = extractDescription(title);

          Element imageElement = product.selectFirst(
            ".s-item__image-wrapper img"
          );
          String imageURL = (imageElement != null)
            ? imageElement.attr("src")
            : "N/A";

          String color = extractColor(title);

          Integer capacity = extractCapacity(title);

          String productLink = "";

          Elements productLinks = product.select(".s-item__link");
          for (Element link : productLinks) {
            productLink = link.attr("href");
          }

          PhoneModel phoneModel = new PhoneModel(
            brand,
            model,
            description,
            imageURL
          );

          PhoneRepository phoneRepository = new PhoneRepository();

          phoneRepository.save(
            phoneModel,
            color,
            capacity,
            Float.parseFloat(newPrice),
            productLink
          );
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static String extractSinglePrice(String priceText) {
    // Extract the first price if the text contains a range
    if (priceText.contains(" to ")) {
      return priceText.split(" to ")[0].trim();
    } else {
      return priceText.trim();
    }
  }

  private static String extractBrand(String title) {
    // Split the title into words
    String[] words = title.split("\\s+");

    // The first word is assumed to be the brand
    if (words.length > 0) {
      return words[0];
    } else {
      return "N/A";
    }
  }

  private static String extractModel(String title) {
    // Split the title into words
    String[] words = title.split("\\s+");

    // Check if there are at least three words
    if (words.length >= 3) {
      // Combine the second and third words as the model
      return words[1] + " " + words[2];
    } else {
      return "N/A";
    }
  }

  private static String extractColor(String title) {
    // Use a regular expression to find color information
    Pattern pattern = Pattern.compile(
      "(Black|Red|Blue|White)",
      Pattern.CASE_INSENSITIVE
    );
    Matcher matcher = pattern.matcher(title);

    if (matcher.find()) {
      return matcher.group(1);
    } else {
      return "Unknown";
    }
  }

  private static Integer extractCapacity(String title) {
    // Use a regular expression to find capacity information
    Pattern pattern = Pattern.compile("(\\d+)GB");
    Matcher matcher = pattern.matcher(title);

    if (matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    } else {
      return -1; // or return null if capacity is not found
    }
  }

  private static String extractDescription(String title) {
    // Split the title into words
    String[] words = title.split("\\s+");

    // Find the index where the description starts
    int startIndex = 3; // Assuming the description starts after the third word (Apple iPhone 8)
    int endIndex = words.length; // Using the entire title if there's no explicit description keyword

    // If there's an explicit keyword indicating the start of the description, adjust the endIndex accordingly
    for (int i = startIndex; i < words.length; i++) {
      if (words[i].equals("-")) {
        endIndex = i;
        break;
      }
    }

    // Combine the words within the description range
    StringBuilder descriptionBuilder = new StringBuilder();
    for (int i = startIndex; i < endIndex; i++) {
      descriptionBuilder.append(words[i]).append(" ");
    }

    return descriptionBuilder.toString().trim();
  }
}
