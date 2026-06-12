package lt.viko.eif.habittracker.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import lt.viko.eif.habittracker.HabitTrackerApplication;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Configuration hook to link Cucumber tests with Spring Boot context.
 */
@CucumberContextConfiguration
@SpringBootTest(classes = HabitTrackerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {
    // This class remains empty. It serves strictly as a configuration hook for Cucumber.
}