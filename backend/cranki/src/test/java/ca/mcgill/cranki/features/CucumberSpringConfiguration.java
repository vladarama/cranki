package ca.mcgill.cranki.features;

import ca.mcgill.cranki.CrankiApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@CucumberContextConfiguration
@SpringBootTest(classes = CrankiApplication.class)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}
