# 1. Maven project setup

- **Original file covered:** [previsit-coordinator/pom.xml](../../previsit-coordinator/pom.xml)
- **Responsibility:** Describe how Maven builds this Java backend, which Java version it targets, and which Spring libraries it may use.
- **Understand first:** Nothing. This is the project blueprint.

# 2. Heavily Commented Code

```xml
<!-- The root element says this XML file is a Maven project description. -->
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <!-- Maven needs this model version to understand the POM format. -->
    <modelVersion>4.0.0</modelVersion>

    <!--
        This parent is Spring Boot's prepared build blueprint. It supplies
        compatible library and plugin versions, so this project does not choose
        a separate version for every Spring dependency below.
    -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.0</version>
        <!-- Do not search one folder upward for a local copy of this parent. -->
        <relativePath/>
    </parent>

    <!-- These three values identify the build artifact Maven creates. -->
    <groupId>com.previsitcoordinator</groupId>
    <artifactId>previsit-coordinator</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <!-- Human-readable information; Maven does not execute these lines. -->
    <name>previsit-coordinator</name>
    <description>Clinic pre-visit coordination backend</description>

    <properties>
        <!-- Compile this project's code against Java 25. -->
        <java.version>25</java.version>
    </properties>

    <dependencies>
        <!-- Gives production code Spring MVC and the embedded web server. -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webmvc</artifactId>
        </dependency>
        <!-- Gives test code JUnit, MockMvc, and Spring MVC testing support. -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webmvc-test</artifactId>
            <!-- This library is available only while Maven runs tests. -->
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Packages the application in Spring Boot's runnable format. -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

# 3. Deep Dive Analysis

## What is it?

A `pom.xml` is Maven's project description. Maven reads it before compiling Java, running tests, or packaging the application. A **dependency** is a library this project needs. A **parent POM** is another project description whose shared configuration this POM inherits. The `test` scope keeps test-only tools out of the production application. A **plugin** performs a build job; this project's Spring Boot plugin packages the application in a runnable format.

## Why use it here?

The health endpoint imports Spring classes such as `@RestController`; plain Java does not include them. The production dependency provides those classes. The test imports JUnit and MockMvc, so it needs a separate test dependency. Using Spring Boot's parent prevents a beginner project from manually matching dozens of Spring-related version numbers.

## When to use it?

Use a POM whenever Maven builds a Java project. Add a production dependency when code under `src/main/java` needs a library at runtime. Add a `test`-scoped dependency when only code under `src/test/java` needs it. Do not add a dependency simply because it might be useful later; unused libraries make a project harder to understand and maintain.

## How to use it?

Every dependency needs a `groupId` and `artifactId`; the parent here manages its version. A frequent beginner mistake is placing a test library in production scope, or placing a production library in test scope and then wondering why the app will not start. Maven convention places production source files in `src/main/java` and tests in `src/test/java`; this project follows that convention. Read [Apache Maven's POM introduction](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html) after this lesson.

# 4. Feynman Technique Explanation

Think of `pom.xml` as a restaurant's supply order and kitchen rules. It tells Maven, the kitchen manager, which ingredients to bring (`dependencies`), which recipe standard to follow (the Spring Boot `parent`), and which version of the cooking equipment is expected (Java 25). The test dependency is like a thermometer used only in quality checks: useful in the kitchen, but not served to customers. Without this file, Maven does not know how to assemble or check this backend.

# 5. Realistic Example

Current-project build trace:

```text
mvn test
  -> Maven reads previsit-coordinator/pom.xml
  -> Maven uses spring-boot-starter-webmvc-test because the test imports MockMvc
  -> Maven runs HealthEndpointTest
```

This is the real workflow already used in this project, not a fictional dependency or second application. The production MVC starter makes the application code available; the test starter gives the test its tools. The Spring Boot Maven plugin is used when Maven packages the application rather than only running its tests.

# 6. Your Turn: Proof of Understanding

Without changing any file, answer these two questions:

1. Why is `spring-boot-starter-webmvc-test` marked with `<scope>test</scope>` while `spring-boot-starter-webmvc` is not?
2. If `pom.xml` named Java 17 instead of Java 25, what project-level decision would change?

Explain your reasoning in plain English. Do not search for the answer first.
