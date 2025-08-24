#!/bin/bash

# Run Maven clean compile, then tests with coverage report
mvn clean compile
mvn test jacoco:report