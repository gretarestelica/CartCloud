package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.Product;
import com.cartcloud.cartcloud.model.Review;
import com.cartcloud.cartcloud.model.User;
import com.cartcloud.cartcloud.repository.ProductRepository;
import com.cartcloud.cartcloud.repository.ReviewRepository;
import com.cartcloud.cartcloud.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

   
    public Review createReview(Long userId, Long productId, Review review) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        review.setUser(user);
        review.setProduct(product);

        return reviewRepository.save(review);
    }

    public Review updateReview(Long id, Review updated) {
        return reviewRepository.findById(id)
                .map(existing -> {
                   
                    existing.setRating(updated.getRating());
                    existing.setComment(updated.getComment());
                    return reviewRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
