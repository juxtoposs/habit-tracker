package lt.viko.eif.habittracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lt.viko.eif.habittracker.config.CacheSettings;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Root controller that exposes the main entry point of the REST API.
 */
@RestController
@Tag(name = "API Root", description = "Entry point for discovering available API resources")
public class ApiRootController {

    /**
     * Returns root API links.
     *
     * @return root resource with HATEOAS links
     */
    @GetMapping("/api")
    @Operation(summary = "Get API root")
    public ResponseEntity<RepresentationModel<?>> getApiRoot() {
        RepresentationModel<?> root = new RepresentationModel<>();

        root.add(linkTo(methodOn(ApiRootController.class).getApiRoot()).withSelfRel());
        root.add(linkTo(methodOn(HabitController.class).getAllHabits()).withRel("habits"));

        return ResponseEntity.ok()
                .cacheControl(CacheSettings.shortPrivateCache())
                .body(root);
    }
}