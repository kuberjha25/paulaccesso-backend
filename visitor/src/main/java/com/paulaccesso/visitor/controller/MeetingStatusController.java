package com.paulaccesso.visitor.controller;

import com.paulaccesso.visitor.dto.VisitorResponse;
import com.paulaccesso.visitor.service.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MeetingStatusController {

    private final VisitorService visitorService;

    @GetMapping("/api/visitors/{id}/meeting-status")
    @ResponseBody
    public String updateMeetingStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        log.info("=== MEETING STATUS UPDATE ===");
        log.info("Visitor ID: {}", id);
        log.info("Status: {}", status);

        try {
            VisitorResponse response = visitorService.updateMeetingStatus(id, status);

            String name = response != null && response.getName() != null ? response.getName() : "Visitor";
            String mobile = response != null && response.getMobile() != null ? response.getMobile() : "N/A";
            String purpose = response != null && response.getPurpose() != null ? response.getPurpose() : "N/A";
            String meetingStatus = response != null && response.getMeetingStatus() != null ? response.getMeetingStatus()
                    : status;

            String successColor = "ACCEPTED".equals(status) ? "#10b981" : "#ef4444";
            String successMessage = "ACCEPTED".equals(status)
                    ? "Meeting request has been ACCEPTED. The visitor has been notified."
                    : "Meeting request has been REJECTED.";

            String html = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>Meeting Status - Paul Accesso</title>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <style>\n" +
                    "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                    "        body {\n" +
                    "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;\n"
                    +
                    "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                    "            min-height: 100vh;\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;\n" +
                    "            align-items: center;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "        .container {\n" +
                    "            background: white;\n" +
                    "            border-radius: 20px;\n" +
                    "            padding: 40px;\n" +
                    "            max-width: 500px;\n" +
                    "            width: 100%;\n" +
                    "            text-align: center;\n" +
                    "            box-shadow: 0 20px 40px rgba(0,0,0,0.1);\n" +
                    "            animation: fadeIn 0.5s ease;\n" +
                    "        }\n" +
                    "        @keyframes fadeIn {\n" +
                    "            from { opacity: 0; transform: translateY(-20px); }\n" +
                    "            to { opacity: 1; transform: translateY(0); }\n" +
                    "        }\n" +
                    "        .icon { font-size: 80px; margin-bottom: 20px; }\n" +
                    "        h1 { font-size: 28px; margin-bottom: 10px; color: #1f2937; }\n" +
                    "        .status-badge {\n" +
                    "            display: inline-block;\n" +
                    "            padding: 6px 16px;\n" +
                    "            border-radius: 50px;\n" +
                    "            font-size: 14px;\n" +
                    "            font-weight: 600;\n" +
                    "            margin: 15px 0;\n" +
                    "        }\n" +
                    "        .status-accepted { background: #d1fae5; color: #065f46; }\n" +
                    "        .status-rejected { background: #fee2e2; color: #991b1b; }\n" +
                    "        .info-card {\n" +
                    "            background: #f9fafb;\n" +
                    "            border-radius: 12px;\n" +
                    "            padding: 20px;\n" +
                    "            margin: 20px 0;\n" +
                    "            text-align: left;\n" +
                    "        }\n" +
                    "        .info-row { margin: 12px 0; display: flex; flex-wrap: wrap; }\n" +
                    "        .info-label { font-weight: 600; color: #4b5563; width: 100px; }\n" +
                    "        .info-value { color: #1f2937; flex: 1; }\n" +
                    "        .message { color: #6b7280; margin: 20px 0; line-height: 1.6; }\n" +
                    "        .btn {\n" +
                    "            background: linear-gradient(135deg, #2563eb, #06b6d4);\n" +
                    "            color: white;\n" +
                    "            border: none;\n" +
                    "            padding: 12px 30px;\n" +
                    "            border-radius: 10px;\n" +
                    "            font-size: 16px;\n" +
                    "            font-weight: 600;\n" +
                    "            cursor: pointer;\n" +
                    "            margin-top: 10px;\n" +
                    "        }\n" +
                    "        .footer { margin-top: 20px; font-size: 12px; color: #9ca3af; }\n" +
                    "        @media (max-width: 480px) {\n" +
                    "            .container { padding: 25px; }\n" +
                    "            .icon { font-size: 60px; }\n" +
                    "            h1 { font-size: 24px; }\n" +
                    "            .info-row { flex-direction: column; }\n" +
                    "            .info-label { width: 100%; margin-bottom: 4px; }\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"icon\">" + ("ACCEPTED".equals(status) ? "✅" : "❌") + "</div>\n" +
                    "        <h1>Meeting Request " + escapeHtml(status) + "</h1>\n" +
                    "        <div class=\"status-badge status-" + ("ACCEPTED".equals(status) ? "accepted" : "rejected")
                    + "\">\n" +
                    "            " + escapeHtml(status) + "\n" +
                    "        </div>\n" +
                    "        <div class=\"info-card\">\n" +
                    "            <div class=\"info-row\"><span class=\"info-label\">👤 Visitor:</span><span class=\"info-value\">"
                    + escapeHtml(name) + "</span></div>\n" +
                    "            <div class=\"info-row\"><span class=\"info-label\">📞 Mobile:</span><span class=\"info-value\">"
                    + escapeHtml(mobile) + "</span></div>\n" +
                    "            <div class=\"info-row\"><span class=\"info-label\">🎯 Purpose:</span><span class=\"info-value\">"
                    + escapeHtml(purpose) + "</span></div>\n" +
                    "            <div class=\"info-row\"><span class=\"info-label\">📌 Status:</span><span class=\"info-value\"><strong style='color: "
                    + successColor + ";'>" + escapeHtml(meetingStatus) + "</strong></span></div>\n" +
                    "        </div>\n" +
                    "        <p class=\"message\">" + successMessage + "</p>\n" +
                    "        <button class=\"btn\" onclick=\"window.close()\">Close Window</button>\n" +
                    "        <div class=\"footer\"><p>Paul Accesso Visitor Management System</p></div>\n" +
                    "    </div>\n" +
                    "    <script>setTimeout(function() { window.close(); }, 5000);</script>\n" +
                    "</body>\n" +
                    "</html>";

            log.info("Returning success page for status: {}", status);
            return html;

        } catch (Exception e) {
            log.error("Error updating meeting status: {}", e.getMessage(), e);

            return "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <title>Error - Paul Accesso</title>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <style>\n" +
                    "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                    "        body {\n" +
                    "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;\n"
                    +
                    "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\n" +
                    "            min-height: 100vh;\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;\n" +
                    "            align-items: center;\n" +
                    "            padding: 20px;\n" +
                    "        }\n" +
                    "        .container {\n" +
                    "            background: white;\n" +
                    "            border-radius: 20px;\n" +
                    "            padding: 40px;\n" +
                    "            max-width: 500px;\n" +
                    "            width: 100%;\n" +
                    "            text-align: center;\n" +
                    "            box-shadow: 0 20px 40px rgba(0,0,0,0.1);\n" +
                    "        }\n" +
                    "        .icon { font-size: 80px; margin-bottom: 20px; }\n" +
                    "        h1 { font-size: 28px; margin-bottom: 10px; color: #1f2937; }\n" +
                    "        .message { color: #6b7280; margin: 20px 0; line-height: 1.6; }\n" +
                    "        .btn {\n" +
                    "            background: linear-gradient(135deg, #2563eb, #06b6d4);\n" +
                    "            color: white;\n" +
                    "            border: none;\n" +
                    "            padding: 12px 30px;\n" +
                    "            border-radius: 10px;\n" +
                    "            font-size: 16px;\n" +
                    "            font-weight: 600;\n" +
                    "            cursor: pointer;\n" +
                    "            margin-top: 10px;\n" +
                    "        }\n" +
                    "        .footer { margin-top: 20px; font-size: 12px; color: #9ca3af; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"icon\">⚠️</div>\n" +
                    "        <h1>Something went wrong</h1>\n" +
                    "        <p class=\"message\">" + escapeHtml(e.getMessage()) + "</p>\n" +
                    "        <p class=\"message\">Please contact the reception desk for assistance.</p>\n" +
                    "        <button class=\"btn\" onclick=\"window.close()\">Close</button>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }

    private String escapeHtml(String text) {
        if (text == null)
            return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}