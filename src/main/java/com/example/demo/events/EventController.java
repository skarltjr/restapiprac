package com.example.demo.events;

import com.example.demo.accounts.Account;
import com.example.demo.accounts.CurrentUser;
import com.example.demo.commons.ErrorsResource;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@CurrentUser Account account,@PathVariable Integer id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event real = event.get();
        EventResource eventResource = new EventResource(real);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if (account!=null && real.getManager().equals(account)) {
            eventResource.add(linkTo(EventController.class).slash(real.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(@CurrentUser Account account,
                                      Pageable pageable, PagedResourcesAssembler<Event> assembler) {
        Page<Event> page = eventRepository.findAll(pageable);
        //링크를 담아주기위해
        PagedModel<EventResource> eventResources = assembler.toModel(page, e -> new EventResource(e));
        eventResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        if (account != null) {
            eventResources.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(eventResources);
    }

    @PostMapping
    public ResponseEntity createEvent(@CurrentUser Account account,
                                      @RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }
        eventValidator.validate(eventDto, errors); // init바인더로 묶으면 값이 없는경우 npe가 난다
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(account);
        Event save = eventRepository.save(event);

        // 리소스= 링크+데이터  1.데이터 = event 2. 링크담아주기
        URI createdUri = linkTo(EventController.class).slash(save.getId()).toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(linkTo(EventController.class).slash(save.getId()).withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@CurrentUser Account account, @PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto, Errors errors) {
        Optional<Event> byId = eventRepository.findById(id);
        if (byId.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(new ErrorsResource(errors));
        }
        Event event = byId.get();
        if (!event.getManager().equals(account)) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        modelMapper.map(eventDto, event);
        Event saved = eventRepository.save(event);
        EventResource eventResource = new EventResource(saved);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

}
