package com.baliset.webcrawl;

public enum Reason
{
    Ok,                      // ok
    ExceededDepthLimit,
    ExceededExecutionTimeLimit,
    ExceededConnectionTimeLimit,
    MalformedUrl,
    Unauthenticated,
    Unauthorized,
    UnsupportedRedirection,  // resource tried to send us elsewhere, but we are too stupid to follow
    OtherBadStatus,
    ConnectionError,
    OutsideDomain,
    ConsideredAnEndNode,    // we think it doesn't link to anything anyway
    UnsupportedProtocol,    // we don't support every protocol
    UnsupportedFormat       // don't know how to parse it for links
}
