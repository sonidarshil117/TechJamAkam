package com.akamai.techjam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.akamai.techjam.util.NLPUtil;

/**
 * Created by dsoni on 30/11/17.
 */
@Service
public class FeedsProcessingService {

    @Autowired
    private NLPUtil nlpUtil;

    public void processFeeds(final String[] feeds) {
        //STEP1 : Get the bad Domain/IPs
        nlpUtil.findDomainsOrIPs(nlpUtil.findSensitiveStrings(feeds));
        //STEP2 : Verify the domain with ETP-IOC details

    }
}
