package com.example.webhook;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "url_id")
    private WebhookUrl webhookUrl;
    private String headers;
    private String body;
    private LocalDateTime createdAt;



    // getters and setters
}
