package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final int MAX_RETRY = 1; // Retry once
    private int count = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY) {
            count++;
            LoggerUtil.info(this.getClass(),
                    "Retrying test: " + result.getName() +
                            " | Attempt: " + (count + 1) +
                            " of " + (MAX_RETRY + 1));
            return true;
        }
        return false;
    }

}
