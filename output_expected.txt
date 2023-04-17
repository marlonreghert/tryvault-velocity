# README for Git Repository

## Description
This is a Java application that reads load attempts from a file, processes them using a service, and writes the results to an output file. The application uses Spring Boot framework for dependency injection and logging, and it includes JPA configuration for data access.

## Prerequisites
- Java JDK 8 or higher
- Maven build tool
- Git version control system
- H2 db

## Installation
1. Clone the repository to your local machine using the following command:
git clone <repository_url>
2. Navigate to the project directory:
cd <project_directory>
3. Build the project using Maven:
mvn clean install

## Usage
1. Run the application using the following command, providing the input and output file paths as arguments:
java -jar target/app.jar <input_file_path> <output_file_path>
Example:
java -jar target/app.jar input.txt output.txt
Note: Make sure to replace `<input_file_path>` and `<output_file_path>` with the actual file paths on your local machine.
2. The application will read the load attempts from the input file, process them using the service, and write the results to the output file.
3. The processed results will be logged in the console, and any exceptions that occur during processing will also be logged.

## Configuration
The application uses Log4j2 for logging, and the log configuration can be modified in the `log4j2.xml` file located in the `src/main/resources` directory.
As a Springboot & Maven application, the settings are respectivelly located in the application.properties and pom.xml

## Future Work
Things we should tackle with some level o priority:
1. Improve the test suite to cover more unit test but also integration tests.
2. Separate the DB in env (dev, staging, prod)
3. Improve the security of the the DB credentials
4. improve the data model, example: Add indexes to speed up the overall performance.

## Contributing
If you would like to contribute to this project, you can fork the repository, create a new branch, make changes, and submit a pull request.

## License
This project is licensed under the [MIT License](LICENSE).

## Contact
For any questions or feedback, please contact Marlon Alves at marlonreghert@gmail.com.
