package com.example.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookRequestDto {

    private String id;
    private String method;
    private String path;
    private String fullUrl;
    private String timestamp;
    private String ip;
    private String host;
    private String size;
    private String processingTime;
    private String note;
    private Map<String, String> headers;
    private String body;
    private Map<String, String> query;

    // getters and setters
}
