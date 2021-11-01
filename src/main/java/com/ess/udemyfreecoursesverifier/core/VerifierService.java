package com.ess.udemyfreecoursesverifier.core;

import com.ess.udemyfreecoursesverifier.model.Course;
import com.ess.udemyfreecoursesverifier.model.SimpleMail;
import com.ess.udemyfreecoursesverifier.model.User;
import com.ess.udemyfreecoursesverifier.service.CourseService;
import com.ess.udemyfreecoursesverifier.service.CourseUserService;
import com.ess.udemyfreecoursesverifier.service.UserService;
import com.ess.udemyfreecoursesverifier.util.DriverUtil;
import com.ess.udemyfreecoursesverifier.util.LinkTransform;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VerifierService {

    private final CourseService courseService;
    private final UserService userService;
    private final CourseUserService courseUserService;

    public VerifierService() {
        this.courseService = new CourseService();
        this.userService = new UserService();
        this.courseUserService = new CourseUserService();
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void verify() {
        courseService.findAll()
                .stream()
                .map(Course::getLink)
                .forEach(this::getSourcePage);
    }

    public void getSourcePage(String url) {
        url = LinkTransform.addSlashes(url);
        System.out.println("VERIFICANDO A URL -> " + url);
        RemoteWebDriver driver = DriverUtil.getChromeWebDriver();
        driver.get(url);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }

        Document document = Jsoup.parse(driver.getPageSource());

        driver.close();
        driver.quit();

        getPrice(document, url);
    }

    private void getPrice(Document document, String url) {
        Elements divPrice = document.getElementsByAttributeValueContaining("data-purpose", "course-price");
        Elements h1Title = document.getElementsByAttributeValueContaining("data-purpose", "lead-title");

        List<String> precos = divPrice.stream()
                .map(item -> item.getElementsByTag("span"))
                .map(Elements::text)
                .collect(Collectors.toList());

        String courseTitle = h1Title.stream()
                .map(Element::text)
                .collect(Collectors.toList()).get(0);

        if (!precos.isEmpty() && (precos.get(0).contains("Gratuito") || precos.get(0).contains("Free"))) {
            List<User> users = userService.findAllByLink(LinkTransform.removeSlashes(url));
            users.forEach(user -> sendEmail(user.getEmail(), courseTitle));
        }
    }

    private void sendEmail(String email, String courseTitle) {
        SimpleMail entity = createForm(email, courseTitle);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity("http://localhost:8081/email/simple-mail", entity, Void.class);
    }

    private SimpleMail createForm(String email, String courseTitle) {
        return new SimpleMail(email,
                "O curso que vc deseja está gratuito neste momento",
                "O curso " + courseTitle + " está gratuito nesse momento, aproveite.");
    }
}
