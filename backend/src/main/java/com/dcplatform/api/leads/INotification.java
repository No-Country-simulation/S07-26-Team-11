package com.dcplatform.api.leads;

public interface INotification {
    void sendNotification(String email, LeadAccessTokens leadAccessTokens);
}
