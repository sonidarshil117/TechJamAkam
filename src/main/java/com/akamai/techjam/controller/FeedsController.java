package com.akamai.techjam.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.akamai.techjam.service.FeedsProcessingService;

/**
 * Created by dsoni on 30/11/17.
 */

@RestController
@RequestMapping(value = "/v1/feeds")
public class FeedsController {

    @Autowired
    private FeedsProcessingService feedsProcessingService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public void processFeeds(HttpServletRequest request,
                                        @RequestBody final String[] feeds) {
        feedsProcessingService.processFeeds(feeds);
    }
}
