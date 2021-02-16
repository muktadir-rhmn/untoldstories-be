package me.untoldstories.be.comment;

import me.untoldstories.be.comment.dtos.Comment;
import me.untoldstories.be.user.pojos.UserDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static me.untoldstories.be.comment.MetaData.COMMENT_SERVICE_API_ROOT_PATH;

class FetchCommentsOfStoryResponse {
    public List<Comment> comments;
}

@RestController
@RequestMapping(COMMENT_SERVICE_API_ROOT_PATH)
public class CommentFetcher {
    private final CommentDetailsAggregator commentDetailsAggregator;

    @Autowired
    public CommentFetcher(
            CommentDetailsAggregator commentDetailsAggregator
    ) {
        this.commentDetailsAggregator = commentDetailsAggregator;
    }

    @GetMapping("")
    public FetchCommentsOfStoryResponse fetchCommentsOfStory (
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @RequestParam long storyID
    ) {
        FetchCommentsOfStoryResponse response = new FetchCommentsOfStoryResponse();
        response.comments = commentDetailsAggregator.fetchCommentsOfStory(storyID, userDescriptor.getUserID());
        return response;
    }
}