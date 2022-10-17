package ru.timutkin.reactivefirststeps.base;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

class BaseOperationsWithFluxTest {


    @Test
    void createAFlux_just(){
        Flux<String> fruits = Flux.just("Apple", "Banana","Orange","Strawberry");

        fruits.subscribe(f -> System.out.println(f));

        StepVerifier.create(fruits)
                .expectNext("Apple")
                .expectNext("Banana")
                .expectNext("Orange")
                .expectNext("Strawberry")
                .verifyComplete();
    }

    @Test
    void createAFlux_fromArray(){
        String[] fruits = new String[]{"Apple", "Banana","Orange","Strawberry"};

        Flux<String> fluxFruits = Flux.fromArray(fruits);

        StepVerifier.create(fluxFruits)
                .expectNext("Apple")
                .expectNext("Banana")
                .expectNext("Orange")
                .expectNext("Strawberry")
                .verifyComplete();
    }
    @Test
    void createAFlux_fromIterable(){
        ArrayList<String> fruits = new ArrayList<>();
        fruits.add("Apple");
        fruits.add("Banana");
        fruits.add("Orange");

        Flux<String> fluxFruits = Flux.fromIterable(fruits);

        StepVerifier.create(fluxFruits)
                .expectNext("Apple")
                .expectNext("Banana")
                .expectNext("Orange")
                .verifyComplete();
    }

    @Test
    void createAFlux_fromStream(){
        Stream<String> fruits = Stream.of("Apple", "Banana","Orange");

        Flux<String> fluxFruits = Flux.fromStream(fruits);

        StepVerifier.create(fluxFruits)
                .expectNext("Apple")
                .expectNext("Banana")
                .expectNext("Orange")
                .verifyComplete();
    }

    @Test
    void createAFlux_range(){
        Flux<Integer> fluxRange = Flux.range(1,5);

        StepVerifier.create(fluxRange)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    void createAFlux_interval(){
        Flux<Long> fluxRange = Flux.interval(Duration.ofSeconds(1L))
                .take(5);

        StepVerifier.create(fluxRange)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .verifyComplete();
    }

    @Test
    void mergeFlux(){
        Flux<String> numbers = Flux.just("1", "2","3")
                .delayElements(Duration.ofMillis(500));

        Flux<String> stringNumbers = Flux.just("one","two","three")
                .delaySubscription(Duration.ofMillis(250))
                .delayElements(Duration.ofMillis(500));

        Flux<String> mergedFlux = numbers.mergeWith(stringNumbers);
        StepVerifier.create(mergedFlux)
                .expectNext("1")
                .expectNext("one")
                .expectNext("2")
                .expectNext("two")
                .expectNext("3")
                .expectNext("three")
                .verifyComplete();
    }


    @Test
    void zipFlux(){
        Flux<String> numbers = Flux.just("1", "2","3");

        Flux<String> stringNumbers = Flux.just("one","two","three");

        Flux<Tuple2<String, String>> zipFlux = Flux.zip(numbers, stringNumbers);


        StepVerifier.create(zipFlux)
                .expectNextMatches( z->(
                        z.getT1().equals("1") &&
                        z.getT2().equals("one")
                        ))
                .expectNextMatches( z->(
                        z.getT1().equals("2") &&
                        z.getT2().equals("two")
                        ))
                .expectNextMatches( z->(
                        z.getT1().equals("3") &&
                        z.getT2().equals("three")
                        ))
                .verifyComplete();
    }

    @Test
     void firstWithSignalFlux() {
        Flux<String> slowFlux = Flux.just("tortoise", "snail", "sloth")
                .delaySubscription(Duration.ofMillis(100));

        Flux<String> fastFlux = Flux.just("hare", "cheetah", "squirrel");

        Flux<String> firstFlux = Flux.firstWithSignal(slowFlux, fastFlux);

        StepVerifier.create(firstFlux)
                .expectNext("hare")
                .expectNext("cheetah")
                .expectNext("squirrel")
                .verifyComplete();
    }

    @Test
    void skipAFew() {
        Flux<String> countFlux = Flux.just(
                        "1", "2", "3", "4", "5")
                .skip(3);

        StepVerifier.create(countFlux)
                .expectNext("4", "5")
                .verifyComplete();
    }

    @Test
    void take() {
        Flux<String> nationalParkFlux = Flux.just(
                        "Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Acadia")
                .take(3);
        StepVerifier.create(nationalParkFlux)
                .expectNext("Yellowstone", "Yosemite", "Grand Canyon")
                .verifyComplete();
    }

    @Test
    void filter() {
        Flux<String> nationalParkFlux = Flux.just(
                        "Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
                .filter(np -> !np.contains(" "));
        StepVerifier.create(nationalParkFlux)
                .expectNext("Yellowstone", "Yosemite", "Zion")
                .verifyComplete();
    }

    @Test
    void distinct() {
        Flux<String> animalFlux = Flux.just(
                        "dog", "cat", "bird", "dog", "bird", "anteater")
                .distinct();
        StepVerifier.create(animalFlux)
                .expectNext("dog", "cat", "bird", "anteater")
                .verifyComplete();
    }

    @Test
    void flatMapAsync(){
        Flux<Point> pointFlux = Flux.just("1 1", "2 2", "3 3", "4 4")
                .flatMap(n-> Mono.just(n)
                        .map( p ->{
                            String[] coordinates = p.split("\\s");
                            return Point.builder()
                                    .x(Integer.parseInt(coordinates[0]))
                                    .y(Integer.parseInt(coordinates[1]))
                                    .build();
                        })
                        .subscribeOn(Schedulers.parallel())
                );

        pointFlux.subscribe(s-> System.out.println(s));

        List<Point> points = new ArrayList<>();
        points.add(new Point(1,1));
        points.add(new Point(2,2));
        points.add(new Point(3,3));
        points.add(new Point(4,4));

        StepVerifier.create(pointFlux)
                .expectNextMatches(point -> points.contains(point))
                .expectNextMatches(point -> points.contains(point))
                .expectNextMatches(point -> points.contains(point))
                .expectNextMatches(point -> points.contains(point))
                .verifyComplete();
    }

    @Test
    public void buffer() {
        Flux<String> fruitFlux = Flux.just(
                "apple", "orange", "banana", "kiwi", "strawberry");
        Flux<List<String>> bufferedFlux = fruitFlux.buffer(3);
        StepVerifier
                .create(bufferedFlux)
                .expectNext(Arrays.asList("apple", "orange", "banana"))
                .expectNext(Arrays.asList("kiwi", "strawberry"))
                .verifyComplete();
    }
    @Test
    public void bufferAsync() {
        Flux<String> fruitFlux = Flux.just(
                "apple", "orange", "banana", "kiwi", "strawberry");
        Flux<String> bufferedFlux = fruitFlux.buffer(3)
                .flatMap(x ->
                        Flux.fromIterable(x)
                                .map(String::toUpperCase)
                                .subscribeOn(Schedulers.parallel())
                                .log()
                );
        List<String> fruitList = new ArrayList<>();
        Collections.addAll(fruitList, "APPLE", "ORANGE", "BANANA", "KIWI", "STRAWBERRY");

        StepVerifier.create(bufferedFlux)
                .expectNextMatches(fruit -> fruitList.contains(fruit))
                .expectNextMatches(fruit -> fruitList.contains(fruit))
                .expectNextMatches(fruit -> fruitList.contains(fruit))
                .expectNextMatches(fruit -> fruitList.contains(fruit))
                .expectNextMatches(fruit -> fruitList.contains(fruit))
                .verifyComplete();



    }



}
