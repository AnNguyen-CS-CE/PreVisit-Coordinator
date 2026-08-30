# 1. Application entry and component scanning

- **Original file covered:** [PrevisitCoordinatorApplication.java](../../previsit-coordinator/src/main/java/com/previsitcoordinator/PrevisitCoordinatorApplication.java), `main`
- **Responsibility:** Give Java a starting method and tell Spring Boot where this application's components begin.
- **Understand first:** [Lesson 01](01-maven-project-setup.md): Maven and dependencies.

# 2. Heavily Commented Code

```java
package com.previsitcoordinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Marks this as the Spring Boot starting class and the root scan package.
@SpringBootApplication
public class PrevisitCoordinatorApplication {

    // The Java Virtual Machine starts a normal Java program by calling main.
    public static void main(String[] args) {
        // Give Spring this class as an anchor. Spring builds its application
        // context, discovers components in this package, and starts the web app.
        SpringApplication.run(PrevisitCoordinatorApplication.class, args);
    }
}
```

# 3. Deep Dive Analysis

## What is it?

A **class** groups related Java code. A **method** is a named action inside a class. `main` is special: the Java Virtual Machine calls it to begin a normal Java program. `static` means Java can call the method without first creating an object of the class. `args` holds any command-line arguments. An **annotation** begins with `@`; it is metadata that a framework can read.

## Why use it here?

The backend needs one clear starting point. `SpringApplication.run(...)` hands control from plain Java to Spring Boot. `PrevisitCoordinatorApplication.class` gives Spring the location of the root package, `com.previsitcoordinator`. That matters because `HealthController` is in that same package, so Spring can discover it automatically. The package position is deliberate, not decoration.

## When to use it?

Every executable Java application needs an entry point, usually `public static void main(String[] args)`. Use `@SpringBootApplication` once on the main class of a small Spring Boot application. Put that class in a root package above its controllers and future services. Do not add this annotation to every class; that would make configuration confusing.

## How to use it?

`ClassName.class` is not an object; it is Java's way to refer to a class itself. A common beginner mistake is writing `new PrevisitCoordinatorApplication()` just to call `main`; that is unnecessary because `main` is `static`. Another is placing the main class in a lower package than the controller, which can stop automatic discovery. Spring Boot's [code-structure guidance](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html) explains this package rule.

# 4. Feynman Technique Explanation

Imagine the backend is a clinic building before opening time. `main` is the front-door key: Java uses it to enter the building. `SpringApplication.run(...)` is the manager who turns on the lights, finds the staff, and prepares each work area. The class location is the building's directory; by starting at `com.previsitcoordinator`, Spring knows where to look for the health-check receptionist, `HealthController`.

# 5. Realistic Example

Current-project startup trace:

```text
Java starts PrevisitCoordinatorApplication.main(args)
  -> main calls SpringApplication.run(PrevisitCoordinatorApplication.class, args)
  -> Spring searches com.previsitcoordinator
  -> Spring discovers HealthController
```

This trace describes the actual application class and controller already in the repository. Its important connection is package placement: `HealthController` is discoverable because it is in the package rooted by the application class.

# 6. Your Turn: Proof of Understanding

Trace this line in your own words:

```java
SpringApplication.run(PrevisitCoordinatorApplication.class, args);
```

What does `.class` refer to, why is `args` passed along, and why does Java not need `new PrevisitCoordinatorApplication()` first? Explain each part.
