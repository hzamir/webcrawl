package com.baliset.webcrawl;

public enum Reason
{
    Ok(false),                      // ok
    ExceededDepthLimit(true),
    ExceededExecutionTimeLimit(true),
    ExceededConnectionTimeLimit(true),
    MalformedUrl(false),
    Unauthenticated(true),
    Unauthorized(true),
    UnsupportedRedirection(true),  // resource tried to send us elsewhere, but we are too stupid to follow
    OtherBadStatus(true),
    ConnectionError(true),
    OutsideDomain(false),
    ConsideredAnEndNode(false),    // we think it doesn't link to anything anyway
    UnsupportedProtocol(false),    // we don't support every protocol
    UnsupportedFormat(true);       // don't know how to parse it for links


    private boolean isAnIssue;

    Reason(boolean isIssue)
    {
        isAnIssue = isIssue;
    }
    // does the reason that a link was not followed constitute an issue
    public boolean isIssue()
    {
        return isAnIssue;

    }


}
