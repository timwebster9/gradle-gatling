.DEFAULT=image

image: gradle-build
	docker build -t timw/gatling .

gradle-build:
	./gradlew build

start:
	docker-compose up -d && docker-compose logs -f

run:
	./gradlew bootRun

stop:
	docker-compose down

gatling:
	./gradlew gatlingRun