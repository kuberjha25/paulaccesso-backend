package com.paulaccesso.visitor.service;

import com.paulaccesso.visitor.entity.User;
import com.paulaccesso.visitor.entity.Visitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Your Visitor Management System OTP");

            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <style>\n" +
                    "        body { font-family: Arial, sans-serif; }\n" +
                    "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }\n" +
                    "        .header { background: linear-gradient(135deg, #2563eb, #06b6d4); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }\n"
                    +
                    "        .otp-code { font-size: 32px; font-weight: bold; text-align: center; padding: 20px; background: #f3f4f6; border-radius: 8px; margin: 20px 0; letter-spacing: 5px; color: #2563eb; }\n"
                    +
                    "        .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h2>Visitor Management System</h2>\n" +
                    "        </div>\n" +
                    "        <div style=\"padding: 20px;\">\n" +
                    "            <p>Hello,</p>\n" +
                    "            <p>Your OTP for login is:</p>\n" +
                    "            <div class=\"otp-code\">" + otp + "</div>\n" +
                    "            <p>This OTP will expire in 5 minutes.</p>\n" +
                    "            <p>If you didn't request this OTP, please ignore this email.</p>\n" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            <p>This is an automated message, please do not reply.</p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}", to, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void sendVisitorNotification(User employee, Visitor visitor) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(employee.getEmail());
            helper.setSubject("New Visitor Request - " + visitor.getName());

            String acceptUrl = baseUrl + "/api/visitors/" + visitor.getId() + "/meeting-status?status=ACCEPTED";
            String rejectUrl = baseUrl + "/api/visitors/" + visitor.getId() + "/meeting-status?status=REJECTED";

            // Visitor photo HTML
            String visitorPhotoHtml = "";
            if (visitor.getPhoto() != null && !visitor.getPhoto().isEmpty()) {
                visitorPhotoHtml = "<div style=\"text-align: center; margin: 20px 0; padding: 15px; background: #f9fafb; border-radius: 10px;\">\n"
                        +
                        "    <p style=\"margin-bottom: 10px; font-weight: bold; color: #374151; font-size: 14px;\">📸 Visitor Photo:</p>\n"
                        +
                        "    <img src=\"" + visitor.getPhoto()
                        + "\" alt=\"Visitor Photo\" style=\"max-width: 250px; max-height: 250px; width: auto; height: auto; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.15); border: 3px solid #e5e7eb;\" />\n"
                        +
                        "</div>";
            } else {
                visitorPhotoHtml = "<div style=\"text-align: center; margin: 20px 0; padding: 15px; background: #f9fafb; border-radius: 10px;\">\n"
                        +
                        "    <p style=\"margin-bottom: 10px; font-weight: bold; color: #374151;\">📸 Visitor Photo:</p>\n"
                        +
                        "    <div style=\"width: 150px; height: 150px; background: #e5e7eb; border-radius: 12px; margin: 0 auto; display: flex; align-items: center; justify-content: center;\">\n"
                        +
                        "        <span style=\"font-size: 48px;\">👤</span>\n" +
                        "    </div>\n" +
                        "    <p style=\"font-size: 12px; color: #6b7280; margin-top: 8px;\">No photo available</p>\n" +
                        "</div>";
            }

            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <style>\n" +
                    "        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; line-height: 1.6; background-color: #f4f4f4; margin: 0; padding: 20px; }\n"
                    +
                    "        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n"
                    +
                    "        .header { background: linear-gradient(135deg, #2563eb, #06b6d4); color: white; padding: 30px; text-align: center; }\n"
                    +
                    "        .header h1 { margin: 0; font-size: 24px; }\n" +
                    "        .header p { margin: 10px 0 0; opacity: 0.9; }\n" +
                    "        .content { padding: 30px; }\n" +
                    "        .info-box { background: #f9fafb; padding: 20px; border-radius: 12px; margin: 20px 0; border-left: 4px solid #10b981; }\n"
                    +
                    "        .info-item { margin: 12px 0; display: flex; align-items: flex-start; gap: 10px; flex-wrap: wrap; }\n"
                    +
                    "        .info-label { font-weight: bold; color: #374151; min-width: 100px; }\n" +
                    "        .info-value { color: #6b7280; word-break: break-word; }\n" +
                    "        .button-container { text-align: center; margin: 30px 0; }\n" +
                    "        .btn { display: inline-block; padding: 12px 28px; margin: 0 8px; text-decoration: none; border-radius: 8px; font-weight: 600; transition: all 0.3s ease; cursor: pointer; }\n"
                    +
                    "        .btn-accept { background: #10b981; color: white; border: none; }\n" +
                    "        .btn-accept:hover { background: #059669; transform: translateY(-2px); }\n" +
                    "        .btn-reject { background: #ef4444; color: white; border: none; }\n" +
                    "        .btn-reject:hover { background: #dc2626; transform: translateY(-2px); }\n" +
                    "        .footer { text-align: center; padding: 20px; background: #f9fafb; color: #6b7280; font-size: 12px; border-top: 1px solid #e5e7eb; }\n"
                    +
                    "        .status-badge { display: inline-block; padding: 4px 12px; background: #fef3c7; color: #d97706; border-radius: 20px; font-size: 12px; font-weight: bold; }\n"
                    +
                    "        hr { border: none; border-top: 1px solid #e5e7eb; margin: 20px 0; }\n" +
                    "        @media only screen and (max-width: 480px) {\n" +
                    "            .container { width: 100%; }\n" +
                    "            .content { padding: 20px; }\n" +
                    "            .btn { display: block; margin: 10px auto; width: 80%; }\n" +
                    "            .info-item { flex-direction: column; gap: 4px; }\n" +
                    "            .info-label { min-width: auto; }\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>🚪 Visitor Request</h1>\n" +
                    "            <p>Action Required</p>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p>Dear <strong>" + employee.getName() + "</strong>,</p>\n" +
                    "            <p>A visitor has arrived to meet you at the reception. Please review the details below and confirm.</p>\n"
                    +
                    "            \n" + visitorPhotoHtml + "\n" +
                    "            \n" +
                    "            <div class=\"info-box\">\n" +
                    "                <h3 style=\"margin-top: 0; color: #374151; font-size: 16px;\">📋 Visitor Details</h3>\n"
                    +
                    "                <div class=\"info-item\">\n" +
                    "                    <span class=\"info-label\">👤 Name:</span>\n" +
                    "                    <span class=\"info-value\"><strong>" + visitor.getName() + "</strong></span>\n"
                    +
                    "                </div>\n" +
                    "                <div class=\"info-item\">\n" +
                    "                    <span class=\"info-label\">📞 Mobile:</span>\n" +
                    "                    <span class=\"info-value\">" + visitor.getMobile() + "</span>\n" +
                    "                </div>\n" +
                    "                <div class=\"info-item\">\n" +
                    "                    <span class=\"info-label\">📧 Email:</span>\n" +
                    "                    <span class=\"info-value\">" + visitor.getEmail() + "</span>\n" +
                    "                </div>\n" +
                    "                <div class=\"info-item\">\n" +
                    "                    <span class=\"info-label\">🏢 Company:</span>\n" +
                    "                    <span class=\"info-value\">"
                    + (visitor.getCompany() != null ? visitor.getCompany() : "N/A") + "</span>\n" +
                    "                </div>\n" +
                    "                <div class=\"info-item\">\n" +
                    "                    <span class=\"info-label\">🎯 Purpose:</span>\n" +
                    "                    <span class=\"info-value\">" + visitor.getPurpose() + "</span>\n" +
                    "                </div>\n" +
                    "                <div class=\"info-item\">\n" +
                    "                    <span class=\"info-label\">⏰ Check-in:</span>\n" +
                    "                    <span class=\"info-value\">" + visitor.getCheckInTime().toString()
                    + "</span>\n" +
                    "                </div>\n" +
                    "                <div class=\"info-item\">\n" +
                    "                    <span class=\"info-label\">📌 Status:</span>\n" +
                    "                    <span class=\"status-badge\">Pending Approval</span>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "            \n" +
                    "            <div class=\"button-container\">\n" +
                    "                <a href=\"" + acceptUrl
                    + "\" class=\"btn btn-accept\" style=\"color: white;\">✓ Accept Meeting</a>\n" +
                    "                <a href=\"" + rejectUrl
                    + "\" class=\"btn btn-reject\" style=\"color: white;\">✗ Reject Meeting</a>\n" +
                    "            </div>\n" +
                    "            \n" +
                    "            <hr />\n" +
                    "            <p style=\"font-size: 12px; color: #6b7280; text-align: center;\">\n" +
                    "                ⚡ This request is pending your response. The visitor is waiting at the reception.\n"
                    +
                    "            </p>\n" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            <p><strong>Paul Accesso</strong> | Visitor Management System</p>\n" +
                    "            <p>This is an automated message, please do not reply.</p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Visitor notification sent to: {}", employee.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send visitor notification to: {}", employee.getEmail(), e);
        }
    }

    public void sendMeetingStatusUpdate(User employee, Visitor visitor, String status) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(visitor.getEmail());
            helper.setSubject("Meeting Request Update - " + visitor.getName());

            String statusColor = status.equals("ACCEPTED") ? "#10b981" : "#ef4444";
            String statusText = status.equals("ACCEPTED") ? "ACCEPTED" : "REJECTED";
            String statusIcon = status.equals("ACCEPTED") ? "✅" : "❌";
            String statusMessage = status.equals("ACCEPTED")
                    ? "Your meeting request has been <strong style='color: #10b981;'>ACCEPTED</strong>. Please proceed to the reception area."
                    : "Your meeting request has been <strong style='color: #ef4444;'>REJECTED</strong>. Please contact the reception desk for assistance.";

            String employeeInfo = "";
            if (employee != null) {
                employeeInfo = "<div style=\"background: #f0f9ff; padding: 15px; border-radius: 12px; margin: 15px 0;\">\n"
                        +
                        "    <p style=\"margin: 0;\"><strong>👔 Meeting with:</strong> " + employee.getName() + "</p>\n"
                        +
                        "    <p style=\"margin: 5px 0 0; font-size: 12px; color: #6b7280;\">"
                        + (employee.getDesignation() != null ? employee.getDesignation() : "Employee") + "</p>\n" +
                        "    <p style=\"margin: 5px 0 0; font-size: 12px; color: #6b7280;\">" + employee.getEmail()
                        + "</p>\n" +
                        "</div>";
            }

            String htmlContent = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <style>\n" +
                    "        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }\n"
                    +
                    "        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n"
                    +
                    "        .header { background: linear-gradient(135deg, #2563eb, #06b6d4); color: white; padding: 30px; text-align: center; }\n"
                    +
                    "        .header h1 { margin: 0; font-size: 24px; }\n" +
                    "        .content { padding: 30px; }\n" +
                    "        .status-box { text-align: center; padding: 20px; margin: 20px 0; border-radius: 12px; background: "
                    + statusColor + "10; border: 1px solid " + statusColor + "30; }\n" +
                    "        .status-icon { font-size: 48px; margin-bottom: 10px; }\n" +
                    "        .status-text { font-size: 28px; font-weight: bold; color: " + statusColor
                    + "; margin: 10px 0; }\n" +
                    "        .info-box { background: #f9fafb; padding: 15px; border-radius: 12px; margin: 15px 0; }\n" +
                    "        .footer { text-align: center; padding: 20px; background: #f9fafb; color: #6b7280; font-size: 12px; border-top: 1px solid #e5e7eb; }\n"
                    +
                    "        @media only screen and (max-width: 480px) {\n" +
                    "            .content { padding: 20px; }\n" +
                    "            .status-text { font-size: 22px; }\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Meeting Request Update</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p>Dear <strong>" + visitor.getName() + "</strong>,</p>\n" +
                    "            \n" +
                    "            <div class=\"status-box\">\n" +
                    "                <div class=\"status-icon\">" + statusIcon + "</div>\n" +
                    "                <div class=\"status-text\">" + statusText + "</div>\n" +
                    "                <p>" + statusMessage + "</p>\n" +
                    "            </div>\n" +
                    "            \n" + employeeInfo + "\n" +
                    "            \n" +
                    "            <div class=\"info-box\">\n" +
                    "                <p><strong>📋 Visitor Details:</strong></p>\n" +
                    "                <p>👤 Name: " + visitor.getName() + "</p>\n" +
                    "                <p>📞 Mobile: " + visitor.getMobile() + "</p>\n" +
                    "                <p>🎯 Purpose: " + visitor.getPurpose() + "</p>\n" +
                    "            </div>\n" +
                    "            \n" +
                    "            <hr />\n" +
                    "            <p style=\"text-align: center; font-size: 12px; color: #6b7280;\">\n" +
                    "                Thank you for using <strong>Paul Accesso</strong> Visitor Management System.\n" +
                    "            </p>\n" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            <p>This is an automated message, please do not reply.</p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Meeting status update sent to: {}", visitor.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send meeting status update to: {}", visitor.getEmail(), e);
        }
    }
}