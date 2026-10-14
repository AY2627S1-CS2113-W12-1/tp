# CakeQueue

CakeQueue is a Java command-line app for recording and managing cake orders.
V1 supports adding, listing, viewing, and editing orders.

Orders are kept in memory and are cleared when the app closes.

## Setup

Use JDK 25. Import this folder as a Gradle project in IntelliJ IDEA and select Java 25 as
the project SDK and Gradle JVM. The entry point is `seedu.duke.Duke`.

## Run and Test

From the project folder on Windows:

```powershell
.\gradlew.bat run
.\gradlew.bat clean check
.\gradlew.bat shadowJar
java -ea -jar build/libs/cakequeue.jar
```

On macOS/Linux, use `./gradlew` instead of `.\gradlew.bat`.
The `check` task runs JUnit tests and Checkstyle for production and test code.
Java assertions are enabled for the Gradle `run` task; use `-ea` when running the JAR directly.

## Try It

```text
add n/Alice Tan p/91234567 d/20-10-2026 s/8 f/Chocolate fi/Strawberry
list
view O001
edit O001 s/10
view O001
bye
```

`update` is accepted as an alias for `edit`.
See the [User Guide](docs/UserGuide.md) for command formats, validation rules, and limitations.

## Project Structure

* `src/main/java/seedu/duke`: application, commands, models, and parser.
* `src/test/java/seedu/duke`: JUnit tests, including full CLI sessions.
* `docs`: project documentation.
* `config/checkstyle`: Java coding-style rules.

The project retains the `seedu.duke` package from the course starter template.
