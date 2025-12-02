package com.metrifuge.LogSimulator.service;

import com.metrifuge.LogSimulator.model.Post;
import com.metrifuge.LogSimulator.model.User;
import com.metrifuge.LogSimulator.trace.TraceContext;
import com.metrifuge.LogSimulator.trace.TraceLogger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ExternalApiService {

    private final WebClient webClient;
    private final Random random = new Random();

    public ExternalApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public User fetchUser(Long userId) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("external.api.fetchUser", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            String url = "https://jsonplaceholder.typicode.com/users/" + userId;

            long httpStartTime = System.currentTimeMillis();
            User user = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();
            long httpDuration = System.currentTimeMillis() - httpStartTime;

            TraceLogger.logHttpRequest("GET", url, 200, httpDuration);

            Map<String, String> tags = new HashMap<>();
            tags.put("userId", userId.toString());
            TraceLogger.logMetric("external.api.user.fetch", 1.0, tags);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("external.api.fetchUser", duration);

            return user;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Failed to fetch user: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("external.api.fetchUser", duration);
            throw e;
        }
    }

    public List<Post> fetchUserPosts(Long userId) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("external.api.fetchUserPosts", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            String url = "https://jsonplaceholder.typicode.com/users/" + userId + "/posts";

            long httpStartTime = System.currentTimeMillis();
            List<Post> posts = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToFlux(Post.class)
                    .collectList()
                    .block();
            long httpDuration = System.currentTimeMillis() - httpStartTime;

            TraceLogger.logHttpRequest("GET", url, 200, httpDuration);

            Map<String, String> tags = new HashMap<>();
            tags.put("userId", userId.toString());
            tags.put("postCount", String.valueOf(posts != null ? posts.size() : 0));
            TraceLogger.logMetric("external.api.posts.fetch", posts != null ? posts.size() : 0, tags);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("external.api.fetchUserPosts", duration);

            return posts;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Failed to fetch posts: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("external.api.fetchUserPosts", duration);
            throw e;
        }
    }

    public Post fetchPost(Long postId) {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("external.api.fetchPost", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            String url = "https://jsonplaceholder.typicode.com/posts/" + postId;

            long httpStartTime = System.currentTimeMillis();
            Post post = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Post.class)
                    .block();
            long httpDuration = System.currentTimeMillis() - httpStartTime;

            TraceLogger.logHttpRequest("GET", url, 200, httpDuration);

            Map<String, String> tags = new HashMap<>();
            tags.put("postId", postId.toString());
            TraceLogger.logMetric("external.api.post.fetch", 1.0, tags);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("external.api.fetchPost", duration);

            return post;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Failed to fetch post: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("external.api.fetchPost", duration);
            throw e;
        }
    }

    public String fetchRandomQuote() {
        String parentSpanId = TraceContext.getSpanId();
        TraceLogger.logSpanStart("external.api.fetchRandomQuote", parentSpanId);
        long startTime = System.currentTimeMillis();

        try {
            String url = "https://api.quotable.io/random";

            long httpStartTime = System.currentTimeMillis();
            String quote = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            long httpDuration = System.currentTimeMillis() - httpStartTime;

            TraceLogger.logHttpRequest("GET", url, 200, httpDuration);
            TraceLogger.logMetric("external.api.quote.fetch", 1.0, null);

            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logSpanEnd("external.api.fetchRandomQuote", duration);

            return quote;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            TraceLogger.logError("Failed to fetch quote: " + e.getMessage(), e.getClass().getSimpleName());
            TraceLogger.logSpanEnd("external.api.fetchRandomQuote", duration);
            throw e;
        }
    }
}
