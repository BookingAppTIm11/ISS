package rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.domain.Review;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.dto.ReviewDTO;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.mapper.ReviewMapper;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.service.IAccommodationService;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.service.IReviewService;
import rs.ac.uns.ftn.siit.team11.ProjectSIIT2023Team11.service.IUserService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private IReviewService reviewService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IAccommodationService accommodationService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ReviewDTO>> getReviews() {
        Collection<Review> reviews = reviewService.findAll();
        return new ResponseEntity<Collection<ReviewDTO>>(ReviewMapper.mapToReviewsDTO(reviews), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable("id") Long id) {
        Optional<Review> review = reviewService.findById(id);
        if (review.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ReviewMapper.mapToReviewDTO(review.get()), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) throws Exception {
        if((reviewDTO.getOwnerEmail() == null && reviewDTO.getAccommodationId() == null)
        || (reviewDTO.getOwnerEmail() != null && reviewDTO.getAccommodationId() != null)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try{
            reviewService.save(ReviewMapper.mapDtoToReview(reviewDTO, userService, accommodationService));
            return new ResponseEntity<>(reviewDTO, HttpStatus.CREATED);
        } catch (Exception exception){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReviewDTO> updateReview(@RequestBody ReviewDTO reviewDTO, @PathVariable Long id) throws Exception {
        Optional<Review> existingReview = reviewService.findById(id);
        if (existingReview.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Review updatedReview = reviewService.save(ReviewMapper.mapDtoToReview(reviewDTO, userService, accommodationService));
        return new ResponseEntity<>(ReviewMapper.mapToReviewDTO(updatedReview), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long id) {
        reviewService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
