package com.codewitheli.joblens.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Config for the simple per-client token-bucket rate limiter (joblens.ratelimit.*). */
@Component
@ConfigurationProperties(prefix = "joblens.ratelimit")
public class RateLimitProperties {

    private boolean enabled = true;
    private int capacity = 30;
    private int refillPerMinute = 30;

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getRefillPerMinute() { return refillPerMinute; }
    public void setRefillPerMinute(int refillPerMinute) { this.refillPerMinute = refillPerMinute; }
}
