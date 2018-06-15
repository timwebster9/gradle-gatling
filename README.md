# gradle-gatling

Reference project for executing Gatling performance tests in Gradle.  The project itself is a simple Spring Boot UI sample project.

## Usage (with Docker)
Most of the work is done in the Makefile.  The below instructions use Docker to run the application.  If you don't have Docker installed, see the next section.

To build the project, just run `make`.

To start the app for testing, run `make start`

To execute Gatling, run `make gatling`

Remove the stack with `make stop`

## Usage (without Docker)

Build and start the application with `make run`

*In another terminal*, execute Gatling with `make gatling`

## Test Reports
Reports will be published to `build/reports/gatling`

## Environment Configuration

Set an environment variable for your base URL:

    export BASE_URL=http://localhost:8080
    
You can see how to parameterise URLs in `src/gatling/scala/simulations.default/Environments.scala`.  For example:

    val baseUrl : String = scala.util.Properties.envOrElse("BASE_URL","http://localhost:8080")
    
and then reference the variable in your simulation:

    	val httpProtocol = http
    		.baseURL(Environments.baseUrl)

