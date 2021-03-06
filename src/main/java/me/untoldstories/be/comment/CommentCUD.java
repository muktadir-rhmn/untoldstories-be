package me.untoldstories.be.comment;

import me.untoldstories.be.comment.repos.CommentRepository;
import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.user.auth.pojos.SignedInUser;
import me.untoldstories.be.utils.pojos.SingleIDResponse;
import me.untoldstories.be.utils.pojos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static me.untoldstories.be.comment.MetaData.COMMENT_SERVICE_API_BASE_PATH;


class AddCommentRequest {
    @NotBlank(message = "Comment must not be blank")
    @Size(max = 1000, message = "Too lengthy")
    public String body;

    @Min(value = 0, message = "Invalid storyID")
    @NotNull(message = "Invalid storyID")
    public Long storyID;
}

class UpdateCommentRequest {
    @NotBlank(message = "Comment must not be blank")
    @Size(max = 1000, message = "Too lengthy")
    public String body;
}

@RestController
@RequestMapping(COMMENT_SERVICE_API_BASE_PATH)
public final class CommentCUD {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentCUD(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @PostMapping("")
    public SingleIDResponse addComment(
            @RequestAttribute("user") SignedInUser signedInUser,
            @RequestBody @Valid AddCommentRequest request
    ) {
        Long commentID = commentRepository.add(signedInUser.getUserID(), request.body, request.storyID);
        return new SingleIDResponse(commentID);
    }

    @PutMapping("/{commentID}")
    public SingleMessageResponse updateComment(
            @RequestAttribute("user") SignedInUser signedInUser,
            @PathVariable long commentID,
            @RequestBody @Valid UpdateCommentRequest request
    ) {
        boolean exists = commentRepository.updateIfExists(signedInUser.getUserID(), commentID, request.body);

        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }

    @DeleteMapping("/{commentID}")
    public SingleMessageResponse deleteComment(
            @RequestAttribute("user") SignedInUser signedInUser,
            @PathVariable long commentID
    ) {
        boolean exists = commentRepository.deleteIfExists(signedInUser.getUserID(), commentID);

        if (exists) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }
}
