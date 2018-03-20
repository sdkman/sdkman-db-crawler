# DB Cleanup

This application runs regularly on CI checking validity of Candidate Version URLs. It will then send out an email notification of all orphaned URLs to be cleaned up.

### Run local

Spin up all dependencies with docker-compose:

    docker-compose -f docker/docker-compose.yml up -d mongo-init greenmail wiremock

Run the tests:

    ./gradlew clean check

Build the docker image:

    ./gradlew docker

Run it locally:

    docker run --rm --name=cleanup --network=host sdkman/sdkman-db-cleanup:latest

Bring down all containers

    docker rm -f cleanup
    docker-compose -f docker/docker-compose.yml down

### Publish to Docker Hub

Tag the docker images and publish to docker hub:

    ./gradlew dockerTag dockerPush
