# Java and Spring Basics Notes

## Annotation Marker `@`

In Java, `@` starts an annotation.

An annotation gives extra information about code to Java, Spring, or another tool.

Example:

```java
@SpringBootApplication
public class PrevisitCoordinatorApplication {
}
```

Here, `@SpringBootApplication` tells Spring Boot that this class is the main application setup class.

## Metadata

Metadata means information about something.

In Java, annotations are metadata because they give extra information about classes, methods, fields, or parameters.

Example:

```java
@RestController
public class HealthController {
}
```

`@RestController` is metadata. It tells Spring that this class handles web/API requests.

## Configuration

Configuration means setup instructions.

In Spring Boot, an application configuration class tells Spring how to set up and start the application.

Example:

```java
@SpringBootApplication
public class PrevisitCoordinatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrevisitCoordinatorApplication.class, args);
    }
}
```

This class tells Spring Boot to:

- Start the application from here.
- Scan this package and its subpackages for Spring components.
- Automatically configure common things like the web server.

## Override

Override means a child class defines the same method as a parent class, but gives it more specific behavior.

Example:

```java
class Animal {
    void makeSound() {
        System.out.println("Some animal sound");
    }
}

class Dog extends Animal {
    @Override
    void makeSound() {
        System.out.println("Woof");
    }
}
```

`Animal` has a general version of `makeSound()`.

`Dog` has a specific version of `makeSound()`.

When this code runs:

```java
Dog dog = new Dog();
dog.makeSound();
```

The output is:

```text
Woof
```

Java uses the `Dog` version because `Dog` overrides the parent method.

## Simple Summary

- `@` means annotation marker.
- Metadata means extra information about code.
- Configuration means setup instructions.
- Override means using a specific child method instead of a general parent method.

## My Understanding

Override means:

> I have the same method as the general parent class, but for this specific child class, use this unique behavior instead.
