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

    @Value("${app.base-url:https://paulaccesso.paulmerchants.net}")
    private String baseUrl;

    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Your Visitor Management System OTP");

            String htmlContent = getOtpEmailHtml(otp);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}", to, e);
        }
    }

    public void sendVisitorNotificationToEmployee(User employee, Visitor visitor) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(employee.getEmail());
            helper.setSubject("📋 Meeting Request - " + visitor.getName());

            String acceptUrl = baseUrl + "/api/visitors/" + visitor.getId() + "/meeting-status?status=ACCEPTED";
            String rejectUrl = baseUrl + "/api/visitors/" + visitor.getId() + "/meeting-status?status=REJECTED";

            String htmlContent = getVisitorNotificationHtml(employee, visitor, acceptUrl, rejectUrl);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Meeting request sent to employee: {}", employee.getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send meeting request to employee: {}", employee.getEmail(), e);
        }
    }

    public void sendNotificationToReceptionist(User receptionist, Visitor visitor, User employee) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(receptionist.getEmail());
            helper.setSubject("🎫 Visitor Approved - " + visitor.getName());

            String htmlContent = getReceptionistNotificationHtml(receptionist, visitor, employee);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Notification sent to receptionist: {}", receptionist.getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send notification to receptionist: {}", receptionist.getEmail(), e);
        }
    }

    private String getOtpEmailHtml(String otp) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }\n" +
                "        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; overflow: hidden; }\n"
                +
                "        .header { background: linear-gradient(135deg, #2563eb, #06b6d4); padding: 30px; text-align: center; }\n"
                +
                "        .header h2 { color: white; margin: 0; font-size: 24px; }\n" +
                "        .content { padding: 30px; }\n" +
                "        .otp-code { font-size: 36px; font-weight: bold; text-align: center; padding: 20px; background: #f3f4f6; border-radius: 8px; margin: 20px 0; letter-spacing: 8px; color: #2563eb; font-family: monospace; }\n"
                +
                "        .footer { text-align: center; padding: 20px; background: #f9fafb; font-size: 12px; color: #6b7280; }\n"
                +
                "    </style>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 20px; background-color: #f4f4f4;\">\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h2>Visitor Management System</h2>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
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
    }

    private String getVisitorNotificationHtml(User employee, Visitor visitor, String acceptUrl, String rejectUrl) {
        String visitorPhotoHtml = "";
        if (visitor.getPhoto() != null && !visitor.getPhoto().isEmpty()) {
            visitorPhotoHtml = "<div style=\"text-align: center; margin: 20px 0;\">\n" +
                    "    <img src=\"" + visitor.getPhoto()
                    + "\" alt=\"Visitor Photo\" style=\"max-width: 250px; max-height: 250px; width: auto; height: auto; border-radius: 12px; border: 3px solid #e5e7eb;\" />\n"
                    +
                    "</div>";
        }

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; background-color: #f4f4f4; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }\n"
                +
                "        .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n"
                +
                "        .header { background: linear-gradient(135deg, #2563eb, #06b6d4); padding: 30px; text-align: center; }\n"
                +
                "        .header h1 { color: #ffffff; margin: 0; font-size: 24px; }\n" +
                "        .header p { color: #ffffff; margin: 10px 0 0; opacity: 0.9; }\n" +
                "        .content { padding: 30px; }\n" +
                "        .info-box { background: #f9fafb; padding: 20px; border-radius: 12px; margin: 20px 0; border-left: 4px solid #10b981; }\n"
                +
                "        .info-item { margin: 12px 0; }\n" +
                "        .info-label { font-weight: bold; color: #374151; }\n" +
                "        .info-value { color: #6b7280; margin-left: 10px; }\n" +
                "        .status-badge { display: inline-block; padding: 4px 12px; background: #fef3c7; color: #d97706; border-radius: 20px; font-size: 12px; font-weight: bold; }\n"
                +
                "        .btn-wrapper { text-align: center; margin: 30px 0; }\n" +
                "        .btn-table { display: inline-block; margin: 0 8px; }\n" +
                "        .footer { text-align: center; padding: 20px; background: #f9fafb; font-size: 12px; color: #6b7280; border-top: 1px solid #e5e7eb; }\n"
                +
                "        @media only screen and (max-width: 480px) {\n" +
                "            .btn-table { display: block; margin: 10px auto; width: 80%; }\n" +
                "            .info-item { display: block; }\n" +
                "            .info-value { display: block; margin-left: 0; margin-top: 4px; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 20px; background-color: #f4f4f4;\">\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>🚪 Visitor Request</h1>\n" +
                "            <p>Action Required</p>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear <strong>" + employee.getName() + "</strong>,</p>\n" +
                "            <p>A visitor has arrived to meet you at the reception. Please review the details below and confirm.</p>\n"
                +
                visitorPhotoHtml + "\n" +
                "            <div class=\"info-box\">\n" +
                "                <h3 style=\"margin-top: 0; color: #374151; font-size: 16px;\">📋 Visitor Details</h3>\n"
                +
                "                <div class=\"info-item\"><span class=\"info-label\">👤 Name:</span> <span class=\"info-value\"><strong>"
                + visitor.getName() + "</strong></span></div>\n" +
                "                <div class=\"info-item\"><span class=\"info-label\">📞 Mobile:</span> <span class=\"info-value\">"
                + visitor.getMobile() + "</span></div>\n" +
                "                <div class=\"info-item\"><span class=\"info-label\">📧 Email:</span> <span class=\"info-value\">"
                + (visitor.getEmail() != null ? visitor.getEmail() : "N/A") + "</span></div>\n" +
                "                <div class=\"info-item\"><span class=\"info-label\">🏢 Company:</span> <span class=\"info-value\">"
                + (visitor.getCompany() != null ? visitor.getCompany() : "N/A") + "</span></div>\n" +
                "                <div class=\"info-item\"><span class=\"info-label\">🎯 Purpose:</span> <span class=\"info-value\">"
                + visitor.getPurpose() + "</span></div>\n" +
                "                <div class=\"info-item\"><span class=\"info-label\">⏰ Check-in:</span> <span class=\"info-value\">"
                + visitor.getCheckInTime().toString() + "</span></div>\n" +
                "                <div class=\"info-item\"><span class=\"info-label\">📌 Status:</span> <span class=\"info-value\"><span class=\"status-badge\">Pending Approval</span></span></div>\n"
                +
                "            </div>\n" +
                "            \n" +
                "            <!-- BUTTONS - Outlook Compatible using tables -->\n" +
                "            <div class=\"btn-wrapper\">\n" +
                "                <table class=\"btn-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" style=\"background-color: #10b981; border-radius: 8px; margin: 0 8px;\">\n"
                +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"border-radius: 8px;\">\n" +
                "                            <a href=\"" + acceptUrl
                + "\" style=\"display: inline-block; padding: 12px 28px; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 14px;\">✓ ACCEPT MEETING</a>\n"
                +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "                <table class=\"btn-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\" style=\"background-color: #ef4444; border-radius: 8px; margin: 0 8px;\">\n"
                +
                "                    <tr>\n" +
                "                        <td align=\"center\" style=\"border-radius: 8px;\">\n" +
                "                            <a href=\"" + rejectUrl
                + "\" style=\"display: inline-block; padding: 12px 28px; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 14px;\">✗ REJECT MEETING</a>\n"
                +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </div>\n" +
                "            \n" +
                "            <!-- Fallback text links for very old email clients -->\n" +
                "            <div style=\"text-align: center; margin: 10px 0; font-size: 12px;\">\n" +
                "                <p>If buttons don't work, copy and paste these links:</p>\n" +
                "                <p><a href=\"" + acceptUrl
                + "\" style=\"color: #10b981;\">Accept Meeting</a> | <a href=\"" + rejectUrl
                + "\" style=\"color: #ef4444;\">Reject Meeting</a></p>\n" +
                "            </div>\n" +
                "            \n" +
                "            <hr style=\"border: none; border-top: 1px solid #e5e7eb; margin: 20px 0;\">\n" +
                "            <p style=\"font-size: 12px; color: #6b7280; text-align: center;\">\n" +
                "                ⚡ This request is pending your response. The visitor is waiting at the reception.\n" +
                "            </p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p><strong>Paul Accesso</strong> | Visitor Management System</p>\n" +
                "            <p>This is an automated message, please do not reply.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    private String getReceptionistNotificationHtml(User receptionist, Visitor visitor, User employee) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif; }\n" +
                "        .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n"
                +
                "        .header { background: linear-gradient(135deg, #10b981, #059669); padding: 30px; text-align: center; }\n"
                +
                "        .header h2 { color: #ffffff; margin: 0; font-size: 24px; }\n" +
                "        .content { padding: 30px; }\n" +
                "        .visitor-card { background: #f0fdf4; padding: 20px; border-radius: 12px; margin: 20px 0; border-left: 4px solid #10b981; }\n"
                +
                "        .info-item { margin: 12px 0; }\n" +
                "        .label { font-weight: bold; color: #374151; }\n" +
                "        .footer { text-align: center; padding: 20px; background: #f9fafb; font-size: 12px; color: #6b7280; border-top: 1px solid #e5e7eb; }\n"
                +
                "        @media only screen and (max-width: 480px) {\n" +
                "            .content { padding: 20px; }\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 20px; background-color: #f4f4f4;\">\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h2>✅ Visitor Approved</h2>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear <strong>" + receptionist.getName() + "</strong>,</p>\n" +
                "            <p>The meeting request has been <strong style='color: #10b981;'>ACCEPTED</strong> by <strong>"
                + employee.getName() + "</strong>.</p>\n" +
                "            <p>Please allow the visitor to enter and assist them:</p>\n" +
                "            <div class=\"visitor-card\">\n" +
                "                <div class=\"info-item\"><span class=\"label\">👤 Visitor Name:</span> "
                + visitor.getName() + "</div>\n" +
                "                <div class=\"info-item\"><span class=\"label\">📞 Mobile:</span> "
                + visitor.getMobile() + "</div>\n" +
                "                <div class=\"info-item\"><span class=\"label\">🎯 Purpose:</span> "
                + visitor.getPurpose() + "</div>\n" +
                "                <div class=\"info-item\"><span class=\"label\">👔 Meeting with:</span> "
                + employee.getName() + "</div>\n" +
                "                <div class=\"info-item\"><span class=\"label\">📧 Employee Email:</span> "
                + employee.getEmail() + "</div>\n" +
                "            </div>\n" +
                "            <p><strong>Action Required:</strong> Please escort the visitor to <strong>"
                + employee.getName() + "</strong>'s office.</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>Visitor Management System | Paul Accesso</p>\n" +
                "            <p>This is an automated message, please do not reply.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}