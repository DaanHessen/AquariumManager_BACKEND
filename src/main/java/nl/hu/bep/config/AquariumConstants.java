package nl.hu.bep.config;

// IDK if this is a good practice, but it seems good to have 

public final class AquariumConstants {
    
    private AquariumConstants() {
        throw new AssertionError("bruh");
    }
    
    public static final double DEFAULT_AQUARIUM_TEMPERATURE = 24.0;
    public static final long DEFAULT_AQUARIUM_MANAGER_ID = 1L;
    public static final int DEFAULT_INHABITANT_COUNT = 1;
    public static final boolean DEFAULT_SCHOOLING = false;
    
    public static final int MIN_AQUARIUM_NAME_LENGTH = 1;
    public static final int MAX_AQUARIUM_NAME_LENGTH = 100;
    public static final double MIN_TEMPERATURE = 10.0;
    public static final double MAX_TEMPERATURE = 40.0;
    public static final double MIN_DIMENSION = 0.1;
    public static final double MAX_DIMENSION = 10.0;
    
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int BCRYPT_ROUNDS = 12;
    
    public static final int CONNECTION_TIMEOUT_SECONDS = 30;
    public static final int QUERY_TIMEOUT_SECONDS = 10;
    
    public static final String API_BASE_PATH = "/api";
    public static final String AUTH_BASE_PATH = "/auth";
    public static final String AQUARIUMS_PATH = "/aquariums";
    public static final String INHABITANTS_PATH = "/inhabitants";
    public static final String ACCESSORIES_PATH = "/accessories";
    public static final String ORNAMENTS_PATH = "/ornaments";
    public static final String STATUS_PATH = "/status";
    
    public static final String LOGIN_ENDPOINT = API_BASE_PATH + AUTH_BASE_PATH + "/login";
    public static final String REGISTER_ENDPOINT = API_BASE_PATH + AUTH_BASE_PATH + "/register";
    
    public static final String BEARER_SCHEME = "Bearer";
    public static final String[] PUBLIC_ENDPOINTS = {
        REGISTER_ENDPOINT,
        LOGIN_ENDPOINT
    };
    
    public static final double DEFAULT_TANK_SIZE = 100.0;
    public static final double DEFAULT_PH_LEVEL = 7.0;
    public static final double DEFAULT_WATER_TEMPERATURE = 25.0;
    public static final int DEFAULT_AGGRESSION_LEVEL = 1;
    public static final double DEFAULT_SALT_TOLERANCE = 0.0;
    public static final String DEFAULT_COLOR = "Unknown";
    public static final String DEFAULT_GENDER = "Unknown";
    public static final int DEFAULT_AGE = 0;
    public static final String DEFAULT_ACCESSORY_COLOR = "white";
    public static final int DEFAULT_FILTER_CAPACITY = 100;
    public static final double DEFAULT_MIN_TEMPERATURE = 20.0;
    public static final double DEFAULT_MAX_TEMPERATURE = 30.0;
    public static final double DEFAULT_CURRENT_TEMPERATURE = 25.0;
}
