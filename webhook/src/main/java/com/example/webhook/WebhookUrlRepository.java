package com.example.webhook;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebhookUrlRepository extends JpaRepository<WebhookUrl, Long> {
    WebhookUrl findBySessionId(String sessionId);  // 通过 Session ID 查询

    WebhookUrl findByUniqueUrl(String uniqueUrl);

}
