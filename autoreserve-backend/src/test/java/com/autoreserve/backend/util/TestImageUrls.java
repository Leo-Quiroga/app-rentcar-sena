package com.autoreserve.backend.util;

/**
 * Utility class containing standardized image URLs for testing purposes.
 * These URLs point to real, stable images that match the production system behavior.
 */
public final class TestImageUrls {

    private TestImageUrls() {
        // Utility class - prevent instantiation
    }

    // Car model images - using stable, professional car images
    public static final String TOYOTA_RAV4 = "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80";
    public static final String HONDA_CIVIC = "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80";
    public static final String BMW_X5 = "https://images.unsplash.com/photo-1555215695-3004980ad54e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80";
    public static final String FORD_FOCUS = "https://images.unsplash.com/photo-1502877338535-766e1452684a?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80";
    
    // Default image for generic testing
    public static final String DEFAULT_CAR = TOYOTA_RAV4;
    
    /**
     * Get image URL by car brand and model for consistent testing
     */
    public static String getImageUrl(String brand, String model) {
        String key = (brand + "_" + model).toUpperCase().replace(" ", "_");
        
        switch (key) {
            case "TOYOTA_RAV4":
                return TOYOTA_RAV4;
            case "HONDA_CIVIC":
                return HONDA_CIVIC;
            case "BMW_X5":
                return BMW_X5;
            case "FORD_FOCUS":
                return FORD_FOCUS;
            default:
                return DEFAULT_CAR;
        }
    }
}