package com.example.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "webhook_url")  // 显式指定表名
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uniqueUrl;
    private String sessionId;
    private LocalDateTime createdAt;
    private LocalDateTime expireAt;  // URL 过期时间



    // getters and setters
}
