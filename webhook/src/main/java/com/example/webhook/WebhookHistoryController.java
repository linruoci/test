package com.example.webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/webhook")
public class WebhookHistoryController {

//    @Autowired
//    private WebhookRequestHistoryRepository webhookRequestHistoryRepository;
//
//    // 获取某个 Webhook URL 的访问历史记录
//    @GetMapping("/{urlId}/history")
//    public ResponseEntity<List<WebhookRequestHistory>> getWebhookHistory(@PathVariable String urlId) {
//        List<WebhookRequestHistory> historyList = webhookRequestHistoryRepository.findByWebhookUrl(urlId);
//        return ResponseEntity.ok(historyList);
//    }
}
