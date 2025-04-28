package com.example.webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebhookRequestHistoryService {

    @Autowired
    private WebhookRequestHistoryRepository webhookRequestHistoryRepository;
    // 保存请求
    public void saveRequest(WebhookRequestHistory webhookRequestHistory) {
        webhookRequestHistoryRepository.save(webhookRequestHistory);
    }
}
