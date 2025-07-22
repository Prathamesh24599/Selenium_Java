package browser_agnostic_feature.retryAnalyzer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.openqa.selenium.support.events.WebDriverListener;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

public class AnnotationTransformer implements IAnnotationTransformer, WebDriverListener {
    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {
        annotation.setRetryAnalyzer(RetryFailedSkipped.class);  // 🔁 only failed
    }
}
