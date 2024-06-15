![Build](https://github.com/hostettler/sample-microservice-application/actions/workflows/maven.yml/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=hostettler_sample-microservice-application&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=hostettler_sample-microservice-application)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=hostettler_sample-microservice-application&metric=coverage)](https://sonarcloud.io/summary/new_code?id=hostettler_sample-microservice-application)




account-service
--> keeps a local cache of the org and the user that is kept up to date via messaging
org-service
--> sends new org to the rabbit mq 
user-service
--> sends new user to the rabbit mq 