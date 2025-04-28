package com.example.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_request_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookRequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "url_id")
    private WebhookUrl webhookUrl;  // 关联到 Webhook URL

    private String method;  // 请求方法（GET, POST, PUT, DELETE）
    private String path;    // 请求路径
    private String fullUrl; // 完整的 URL
    private String ip;      // 请求 IP
    private String host;    // 请求的 host
    private String size;    // 请求大小
    private String processingTime;  // 请求处理时间
    private String note;   // 备注（如果有）

    @Lob
    private String headers;  // 请求头信息

    @Lob
    private String body;     // 请求体内容

    private String query;    // 查询参数（如果有）

    private LocalDateTime createdAt;  // 请求的时间

    // getters and setters
}
