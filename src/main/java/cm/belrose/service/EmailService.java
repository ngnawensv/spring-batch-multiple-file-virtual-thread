package cm.belrose.service;

import cm.belrose.service.dto.EmailContent;

public interface EmailService {
    void send(EmailContent content);
}
