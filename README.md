# SchoolLearning
Various games for primary school homework exercises

To build:

Maven plugin jib:dockerBuild

This is then school-learning:[version]

To start container locally:

docker run -d --name school-learning --rm -p 8080:8080 school-learning:[version]

To push to Docker Hub, must be tagged as latest:

docker tag school-learning:[version] conradlco/school:latest