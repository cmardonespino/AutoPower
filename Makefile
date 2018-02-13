test-all: up test down

up:
	./test-env.sh up

test:
	./test-env.sh test

down:
	./test-env.sh down

status:
	./test-env.sh status
