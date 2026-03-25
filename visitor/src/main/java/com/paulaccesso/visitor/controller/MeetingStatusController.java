package com.paulaccesso.visitor.controller;

import com.paulaccesso.visitor.dto.VisitorResponse;
import com.paulaccesso.visitor.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MeetingStatusController {
    
    private final VisitorService visitorService;
    
    @GetMapping("/visitors/{id}/meeting-status")
    @ResponseBody
    public String updateMeetingStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        try {
            VisitorResponse response = visitorService.updateMeetingStatus(id, status);
            
            // Simple HTML response
            return "<!DOCTYPE html>\n" +
                   "<html>\n" +
                   "<head>\n" +
                   "    <title>Meeting Status</title>\n" +
                   "    <style>\n" +
                   "        body {\n" +
                   "            font-family: Arial, sans-serif;\n" +
                   "            display: flex;\n" +
                   "            justify-content: center;\n" +
                   "            align-items: center;\n" +
                   "            height: 100vh;\n" +
                   "            margin: 0;\n" +
                   "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                   "        }\n" +
                   "        .container {\n" +
                   "            text-align: center;\n" +
                   "            background: white;\n" +
                   "            padding: 40px;\n" +
                   "            border-radius: 10px;\n" +
                   "            box-shadow: 0 10px 40px rgba(0,0,0,0.1);\n" +
                   "            max-width: 500px;\n" +
                   "        }\n" +
                   "        .success { color: #10b981; font-size: 64px; }\n" +
                   "        .error { color: #ef4444; font-size: 64px; }\n" +
                   "        .btn {\n" +
                   "            background: #2563eb;\n" +
                   "            color: white;\n" +
                   "            padding: 10px 20px;\n" +
                   "            border: none;\n" +
                   "            border-radius: 5px;\n" +
                   "            cursor: pointer;\n" +
                   "            font-size: 14px;\n" +
                   "            margin-top: 20px;\n" +
                   "        }\n" +
                   "        .btn:hover { background: #1d4ed8; }\n" +
                   "    </style>\n" +
                   "</head>\n" +
                   "<body>\n" +
                   "    <div class=\"container\">\n" +
                   "        <div class=\"success\">✓</div>\n" +
                   "        <h1>Meeting Request " + status + "</h1>\n" +
                   "        <p><strong>Visitor:</strong> " + response.getName() + "</p>\n" +
                   "        <p><strong>Mobile:</strong> " + response.getMobile() + "</p>\n" +
                   "        <p><strong>Purpose:</strong> " + response.getPurpose() + "</p>\n" +
                   "        <p><strong>Status:</strong> <strong style='color: #10b981'>" + status + "</strong></p>\n" +
                   "        <p>The visitor has been notified.</p>\n" +
                   "        <button class=\"btn\" onclick=\"window.close()\">Close Window</button>\n" +
                   "    </div>\n" +
                   "</body>\n" +
                   "</html>";
            
        } catch (Exception e) {
            return "<!DOCTYPE html>\n" +
                   "<html>\n" +
                   "<head>\n" +
                   "    <title>Error</title>\n" +
                   "    <style>\n" +
                   "        body {\n" +
                   "            font-family: Arial;\n" +
                   "            display: flex;\n" +
                   "            justify-content: center;\n" +
                   "            align-items: center;\n" +
                   "            height: 100vh;\n" +
                   "            margin: 0;\n" +
                   "            background: #fef2f2;\n" +
                   "        }\n" +
                   "        .container {\n" +
                   "            text-align: center;\n" +
                   "            background: white;\n" +
                   "            padding: 40px;\n" +
                   "            border-radius: 10px;\n" +
                   "            max-width: 500px;\n" +
                   "        }\n" +
                   "        .error { color: #dc2626; font-size: 64px; }\n" +
                   "    </style>\n" +
                   "</head>\n" +
                   "<body>\n" +
                   "    <div class=\"container\">\n" +
                   "        <div class=\"error\">✗</div>\n" +
                   "        <h1>Error</h1>\n" +
                   "        <p>" + e.getMessage() + "</p>\n" +
                   "        <p>Please contact the reception desk.</p>\n" +
                   "        <button class=\"btn\" onclick=\"window.close()\">Close</button>\n" +
                   "    </div>\n" +
                   "</body>\n" +
                   "</html>";
        }
    }
}