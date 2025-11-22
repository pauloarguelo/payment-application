package com.payment.app.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.rabbitmq.listener.simple")
public class RabbitMQProperties {

    private RetryConfig retry = new RetryConfig();
    private int concurrency = 3;
    private int maxConcurrency = 10;

    public RetryConfig getRetry() {
        return retry;
    }

    public void setRetry(RetryConfig retry) {
        this.retry = retry;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public int getMaxConcurrency() {
        return maxConcurrency;
    }

    public void setMaxConcurrency(int maxConcurrency) {
        this.maxConcurrency = maxConcurrency;
    }

    public static class RetryConfig {
        private boolean enabled = true;
        private int maxAttempts = 5;
        private long initialInterval = 1000;
        private double multiplier = 2.0;
        private long maxInterval = 30000;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public long getInitialInterval() {
            return initialInterval;
        }

        public void setInitialInterval(long initialInterval) {
            this.initialInterval = initialInterval;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }

        public long getMaxInterval() {
            return maxInterval;
        }

        public void setMaxInterval(long maxInterval) {
            this.maxInterval = maxInterval;
        }
    }
}

