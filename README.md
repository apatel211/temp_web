#  Assignment 1 : Selenium + TestNG UI Automation Framework (MultiBank)

A modular UI test framework built with **Selenium 4**, **TestNG**, and **Extent Reports**.  
Supports **parallel execution** locally or on a **Selenium Grid (Standalone JAR)**.

## 🚀 Tech Stack
- Java 17+, Maven 3.9+
- Selenium 4, TestNG 7.x
- WebDriverManager (auto driver management)
- ExtentReports for HTML reports
- SLF4J + Logback for logging

## 📂 Project Structure
```
selenium-testng-framework/
├── pom.xml
├── testng.xml
├── src
│ ├── main/java
│ │ ├── pages/ # Page Objects
│ │ └── utils/ # DriverFactory, WaitUtils, LoggerUtil, ReportManager, etc.
│ └── test/java
│ ├── tests/ # Test classes
│ ├── listeners/ # TestListener, RetryListener
│ └── utils/ # RetryAnalyzer (test utils)
│ └── test/resources
│ ├── config.properties # baseUrl, browser, gridUrl, timeout
│ ├── expected_texts.json # UI text checks
│ ├── testdata.json # Nav/Footer/App links
│ └── logback.xml # Logging config
└── .gitignore
```

## 🔧 Prerequisites
- Java 17+
- Maven 3.9+
- Selenium Standalone JAR (download from [SeleniumHQ releases](https://github.com/SeleniumHQ/selenium/releases))

## ⚙️ Setup
- Import this folder as a Maven project in IntelliJ (File -> Open).
- Install dependencies:
- mvn clean compile

## ▶️ Run Tests (Local Browser)
By default, the browser is read from `config.properties`.
- mvn clean test                        # runs Chrome + Firefox
- mvn clean test -DsuiteXmlFile=testng.xml   # same effect, explicit suite


## 🌐 Run Tests on Selenium Grid (Standalone JAR)

- Start Grid standalone (hub + node in one process):
- java -jar selenium-server-4.35.0.jar standalone
- Default Hub UI: http://localhost:4444/ui
- Run tests against Grid:
- mvn clean test -Dbrowser=chrome -DgridUrl=http://localhost:4444
- mvn clean test -Dbrowser=firefox -DgridUrl=http://localhost:4444
- mvn clean test -DsuiteXmlFile=testng.xml

## 🔀 Parallel Execution
- Controlled in testng.xml: Number of  thread count
- Can be run differently with Classes / Methods  
  mvn clean test -Pparallel-classes
  mvn clean test -Pparallel-methods
- Retry analyzer is globally set testng.xml:

- Cross-browser , added for chrome and firefox.


## ⚙️ Configuration

Increase thread-count for more parallelism.
Add additional <test> blocks for cross-browser runs.
- Extent report: target/extent-report.html
- Screenshots: target/screenshots/
- Locators may need adjustment if the site DOM changes.
- JsonUtils.readJson("filename.json") is reusable for any JSON file under src/test/resources.

## 📊 Reports & Artifacts
- Extent HTML Report → test-output/ExtentReport_<timestamp>.html
- Logs → logs/automation.log
- Screenshots (failures) → screenshots/
- These are ignored in Git via .gitignore. ( I will commit for initial one as it asked for )

## 📌 Deliverables Checklist
- ✅ Public GitHub repository with progressive commits
- ✅ Maven project with clear POM + package layout
- ✅ Clean README.md (setup, run commands, parallel flags, JSON instructions)
- ✅ Local Selenium Grid setup using standalone JAR

## Note : 
- No Optional Bonus code has been added 

## Assignment 2 : How many times a character is repeating in a string and print its count in the same order as the string
```java
import java.util.LinkedHashMap;
import java.util.Map;

public class CharFrequency {

    // Function to count characters and print in input order
    public static void printCharFrequency(String input) {
        // Assumptions:
        // 1. Case sensitive
        // 2. Ignore whitespace
        input = input.replace(" ", "");

        Map<Character, Integer> freqMap = new LinkedHashMap<>();

        // Count each char, keeping insertion order
        for (char c : input.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // Print in order of first appearance
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    public static void main(String[] args) {
        String input = "I am Programming in Java";
        printCharFrequency(input);
    }
}

