package com.example.webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WebhookUrlService {

    @Autowired
    private WebhookUrlRepository webhookUrlRepository;

    // 生成唯一的 webhook URL
    public String createUniqueWebhookUrl(String sessionId) {
        String urlId = UUID.randomUUID().toString();
        WebhookUrl webhookUrl = new WebhookUrl();
        webhookUrl.setUniqueUrl(urlId);
        webhookUrl.setSessionId(sessionId);
        webhookUrl.setCreatedAt(LocalDateTime.now());
        webhookUrl.setExpireAt(LocalDateTime.now().plusDays(3)); // 3天后过期
        webhookUrlRepository.save(webhookUrl);
        return urlId;
    }

    public WebhookUrl findByUniqueUrl(String uniqueUrl) {
        return webhookUrlRepository.findByUniqueUrl(uniqueUrl);
    }


}
