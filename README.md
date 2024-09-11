# kitchensink

Web-enabled database application adapted from [a JBoss quickstart project](https://github.com/jboss-developer/jboss-eap-quickstarts/tree/8.0.x/kitchensink) to use Java 21 and other, more modern technologies, such as Spring Boot & MongoDB

## What is it?

This project is a simplified migration of a Java-based web application using the following technologies:

### Migration Overview
#### Technologies Introduced
* Java 21
* Spring Boot
  * Replaces direct Jakarta CDI API
* MongoDB (integrated via Spring Data)
  * Replaces in-memory, relational H2 database integrated via JNDI
* JoinFaces
  * Enables simplified Spring Boot integration with JSF 
* TestContainers
  * Enables ephemeral MongoDB Docker container when running unit tests

#### Technologies Retained

* Maven
* Wildfly
* Jakarta (several libraries)
* JSF
* JUnit

## Building, Deploying, & Testing the Application

### Build

* To build the `.war` file necessary for running the application, run `mvn package`
  * When rerunning the build step, you can precede this command with `mvn clean` to delete the previous build's artifacts (i.e., the `target/` directory)
  * **NOTE:** The Jakarta CDI API dependency should be included (i.e., `<scope>provided</scope>`) as part of Spring Boot starter, as well as EAP if that is where the application is deployed 

### Deploy
* Prior to deploying the application, you must have a MongoDB instance running (local or remote) and its connection details configured within `application.properties`
  * **NOTE:** The MongoDB instance details pre-populated within `application.properties` are only applicable to a locally running instance. Remote instance details should never be committed to version control 
* The application can be deployed to a Wildfly server (local or remote) by running `mvn deploy`
  * 

### Test
* The tests included with this application can be run with either `mvn verify` or `mvn integration-test`
  * `MemberRegistrationIT` requires that the Docker daemon is running so that the test can execute absent an independently running MongoDB instance
  * The `RemoteMemberRegistrationIT` test expects the application to either be running locally or on a remote host (configured in the `SERVER_HOST` environment variable)

### Prerequisites
* [OpenJDK 21](https://openjdk.org/install/)
* [Maven 3](https://maven.apache.org/download.cgi)
* [JBoss EAP/Wildfly Server](https://www.redhat.com/en/technologies/jboss-middleware/application-platform)
  * Local or remote
  * Tested with JBoss EAP 8.0
* [MongoDB](https://www.mongodb.com/docs/manual/administration/install-community/)
  * Local or remote
* [Docker](https://docs.docker.com/engine/install/) (needed when running tests)

## Considerations for a Larger Scale Migration

The following categorizes the improvements that could be made to this application in the context of a larger migration performed by a larger, dedicated team over an extended period of time.

### Documentation

* All classes and methods thoroughly documented with Javadocs

### Testing

* Exhaustive integration tests written and validated prior to migration and leveraged post-migration
* Unit tests to increase code coverage (within reason)

### Code Standards

* Linting tools configured
* Code-style enforcement tools configured
* Pre-commit hooks to enforce those standards before reaching remote version control

### Data Migration

* A realistic application would presumably have a production relational database rather an in-memory database – MongoDB's relational DB migration tool could be used to transition away from that database

### Automation

* Continuous integration pipelines to catch and address code issues (e.g., failed tests, code standard violations, compilation failures, etc.)
* Continuous delivery pipeline pushing to an artifact repository – organized by an agreed upon semantic versioning system – for traceability and deployment reversions
* Continuous deployment pipeline – at the very least to lower environment(s) – so that new versions can be interacted with and tested quickly  

### UI

* Replace JSF with something that integrates more neatly with Spring Boot – JoinFaces is fragile and frequently breaks with new versions of both Spring Boot and JSF

### Miscellaneous Optimizations

* Server-agnostic application/dependencies (i.e., not reliant on deployment to JBoss EAP/Wildfly)
* 
