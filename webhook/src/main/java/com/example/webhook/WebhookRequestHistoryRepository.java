package com.example.webhook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookRequestHistoryRepository extends JpaRepository<WebhookRequestHistory, Long> {

    List<WebhookRequestHistory> findByWebhookUrl(WebhookUrl webhookUrl);  // 根据 URL 查询访问历史
}
