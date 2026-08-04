package com.dcplatform.api.leads;

public interface INotification {
    void sendNotification(Lead lead , LeadAccessTokens leadAccessTokens);
}
