package ca.mcgill.cranki.features;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = "pretty", features = "src/test/resources/ca/mcgill/cranki", glue = "ca.mcgill.cranki.features")
public class CucumberTestRunner {
}
