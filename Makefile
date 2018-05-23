.DEFAULT=image

image: gradle-build
	docker build -t timw/gatling .

gradle-build:
	./gradlew build

run:
	docker-compose up -d && docker-compose logs -f

stop:
	docker-compose down

gatling:
	./gradlew gatlingRun