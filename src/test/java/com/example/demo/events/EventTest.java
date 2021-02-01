package com.example.demo.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {
    @Test
    void eventBuilder() {
        Event event = Event.builder()
                .name("REST API")
                .description("스프링 rest api")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    void free() {
        //given
        Event event = Event.builder()
                .name("REST API")
                .description("스프링 rest api")
                .basePrice(0)
                .maxPrice(0)
                .build();
        //when
        event.update();
        //then
        assertThat(event.isFree()).isTrue();
    }

    @Test
    void noneFree() {
        //given
        Event event = Event.builder()
                .name("REST API")
                .description("스프링 rest api")
                .basePrice(10)
                .maxPrice(0)
                .build();
        //when
        event.update();
        //then
        assertThat(event.isFree()).isFalse();
    }
    void noneFree2() {
        //given
        Event event = Event.builder()
                .name("REST API")
                .description("스프링 rest api")
                .basePrice(0)
                .maxPrice(10)
                .build();
        //when
        event.update();
        //then
        assertThat(event.isFree()).isFalse();
    }


}