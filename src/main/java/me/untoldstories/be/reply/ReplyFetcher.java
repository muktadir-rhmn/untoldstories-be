package me.untoldstories.be.reply;

import me.untoldstories.be.reply.dtos.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static me.untoldstories.be.reply.MetaData.REPLY_SERVICE_API_ROOT_PATH;

class FetchRepliesOfStoryResponse {
    public List<Reply> replies;
}

@RestController
@RequestMapping(REPLY_SERVICE_API_ROOT_PATH)
public class ReplyFetcher {
    private final ReplyDetailsAggregator replyDetailsAggregator;

    @Autowired
    public ReplyFetcher(ReplyDetailsAggregator replyDetailsAggregator) {
        this.replyDetailsAggregator = replyDetailsAggregator;
    }

    @GetMapping("")
    public FetchRepliesOfStoryResponse fetchRepliesOfStory(
            @RequestParam long storyID
    ) {
        FetchRepliesOfStoryResponse response = new FetchRepliesOfStoryResponse();
        response.replies = replyDetailsAggregator.fetchCommentsOfStory(storyID);
        return response;
    }
}
