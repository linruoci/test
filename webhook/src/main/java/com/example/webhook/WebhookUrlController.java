package com.example.webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.servlet.http.HttpUtils.parseQueryString;

@RestController
@RequestMapping("/webhook")
public class WebhookUrlController {

    @Autowired
    private WebhookUrlService webhookUrlService;


    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebhookRequestHistoryRepository webhookRequestHistoryRepository;

    // 生成唯一的 webhook URL
    @GetMapping("/generate")
    public ResponseEntity<String> generateWebhookUrl(HttpServletRequest request) {
        // 获取当前 session 对象
        HttpSession session = request.getSession();
        String sessionId = session.getId();  // 获取用户的 session ID

        // 检查 session 中是否已经存储了 URL
        String existingUrl = (String) session.getAttribute("webhookUrl");
        if (existingUrl != null) {
            // 如果已经有 URL，直接返回已有的 URL
            return ResponseEntity.ok(existingUrl);
        }

        // 获取当前请求的 host 和 port
        String host = request.getServerName();  // 获取主机名或 IP 地址
        int port = request.getServerPort();    // 获取端口号
        String contextPath = request.getContextPath(); // 获取上下文路径

        // 动态构造完整的 URL
        String baseUrl = "http://" + host + ":" + port + contextPath + "/webhook/";

        // 生成唯一的 URL
        String uniqueUrl = webhookUrlService.createUniqueWebhookUrl(sessionId);

        // 将生成的 URL 存储到 session 中
        session.setAttribute("webhookUrl", baseUrl + uniqueUrl);

        // 返回完整的 URL
        return ResponseEntity.ok(baseUrl + uniqueUrl);
    }

    // 获取某个 Webhook URL 的访问历史记录
    @GetMapping("/{urlId}/history")
    public  ResponseEntity<List<WebhookRequestDto>> getWebhookHistory(@PathVariable String urlId) {
        WebhookUrl webhookUrl = webhookUrlService.findByUniqueUrl(urlId);
        if (webhookUrl == null) {
            return ResponseEntity.notFound().build();
        }
        List<WebhookRequestHistory> historyList = webhookRequestHistoryRepository.findByWebhookUrl(webhookUrl);
        // 构造符合前端需要的格式
        List<WebhookRequestDto> response = new ArrayList<>();
        for (WebhookRequestHistory history : historyList) {
            WebhookRequestDto requestDetails = new WebhookRequestDto();
            requestDetails.setId(history.getId().toString());
            requestDetails.setMethod(history.getMethod());
            requestDetails.setPath(history.getPath());
            requestDetails.setFullUrl(history.getFullUrl());
            requestDetails.setTimestamp(history.getCreatedAt().toString());
            requestDetails.setIp(history.getIp());
            requestDetails.setHost(history.getHost());
            requestDetails.setSize(history.getSize());
            requestDetails.setProcessingTime(history.getProcessingTime());
            requestDetails.setNote(history.getNote());
            requestDetails.setHeaders(parseHeaders(history.getHeaders()));
            requestDetails.setBody(history.getBody());
            requestDetails.setQuery(parseQueryString(history.getQuery()));
            response.add(requestDetails);
        }

        return ResponseEntity.ok(response);
    }

    private Map<String, String> parseHeaders(String headersString) {
        Map<String, String> headers = new HashMap<>();
        String result = headersString.replaceAll("[\\[\\]]", "");
        // 去掉字符串中的引号
        String cleanedHeadersString = result.replace("\"", "");
        // 使用正则表达式匹配每个 header 的 key: value 键值对
        // 正则表达式：匹配 'key: value'，并忽略值中的逗号（即值可能包含逗号）
        String regex = "(\\S+):\\s*(\"[^\"]+\"|[^,]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cleanedHeadersString);
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            // 存储 header 到 Map 中
            headers.put(key, value);
        }
        return headers;
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryMap = new HashMap<>();
        // 假设 queryString 格式为 "key1=value1&key2=value2"
        if (StringUtils.hasText(queryString)) {
            String[] queries = queryString.split("&");
            for (String query : queries) {
                String[] keyValue = query.split("=");
                queryMap.put(keyValue[0], keyValue[1]);
            }
        }
        return queryMap;
    }

    @RequestMapping(value = "/{urlId}", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<String> handleWebhookRequest(
            @PathVariable String urlId,
            @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) String body,
            HttpServletRequest httpServletRequest) {
        String method = httpServletRequest.getMethod();
        // 打印请求方法和 Webhook URL
        System.out.println("Received " + method + " request for Webhook URL: " + urlId);
        WebhookUrl webhookUrl = webhookUrlService.findByUniqueUrl(urlId);
        if (webhookUrl == null) {
            return ResponseEntity.notFound().build();
        }

        // 获取请求相关的额外信息
        String path = httpServletRequest.getRequestURI();
        String ip = httpServletRequest.getRemoteAddr();
        String host = httpServletRequest.getServerName();
        long processingTime = System.currentTimeMillis() - httpServletRequest.getDateHeader("Date");  // 计算处理时间

        // 创建新的请求历史记录
        WebhookRequestHistory history = new WebhookRequestHistory();
        history.setWebhookUrl(webhookUrl);
        history.setMethod(method);
        history.setPath(path);
        history.setFullUrl(httpServletRequest.getRequestURL().toString());
        history.setIp(ip);
        history.setHost(host);
        history.setSize("0 bytes");  // 假设暂时没有计算上传/下载的字节数
        history.setProcessingTime(processingTime + " ms");
        history.setHeaders(headers.toString());
        history.setBody(body);
        history.setQuery(httpServletRequest.getQueryString() != null ? httpServletRequest.getQueryString() : "");
        history.setCreatedAt(LocalDateTime.now());

        webhookRequestHistoryRepository.save(history);

        WebhookRequestDto requestDetails = new WebhookRequestDto();
        requestDetails.setId(history.getId().toString());
        requestDetails.setMethod(history.getMethod());
        requestDetails.setPath(history.getPath());
        requestDetails.setFullUrl(history.getFullUrl());
        requestDetails.setTimestamp(history.getCreatedAt().toString());
        requestDetails.setIp(history.getIp());
        requestDetails.setHost(history.getHost());
        requestDetails.setSize(history.getSize());
        requestDetails.setProcessingTime(history.getProcessingTime());
        requestDetails.setNote(history.getNote());
        requestDetails.setHeaders(parseHeaders(history.getHeaders()));
        requestDetails.setBody(history.getBody());
        requestDetails.setQuery(parseQueryString(history.getQuery()));

        messagingTemplate.convertAndSend("/topic/webhook/" + urlId, requestDetails);  // 通过 WebSocket 推送消息

        // 根据请求方法返回不同的响应
        switch (method) {
            case "GET":
                return ResponseEntity.ok("GET request received for Webhook URL: " + urlId);
            case "POST":
                return ResponseEntity.status(201).body("POST request received for Webhook URL: " + urlId);
            case "PUT":
                return ResponseEntity.status(200).body("PUT request received for Webhook URL: " + urlId);
            case "DELETE":
                return ResponseEntity.status(200).body("DELETE request received for Webhook URL: " + urlId);
            default:
                return ResponseEntity.status(405).body("Method not allowed");
        }
    }
}
