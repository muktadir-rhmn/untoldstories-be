package me.untoldstories.be.reply;

import me.untoldstories.be.constants.Reaction;
import me.untoldstories.be.error.exceptions.SingleErrorMessageException;
import me.untoldstories.be.reply.repos.ReplyReactionRepository;
import me.untoldstories.be.user.UserDescriptor;
import me.untoldstories.be.utils.dtos.SingleMessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static me.untoldstories.be.reply.MetaData.REPLY_SERVICE_API_ROOT_PATH;

class AddReplyReactionRequest {
    @NotNull
    public Long commentID;
}

/**
 * A user can have at most one reaction to a single entity
 */
@RestController
@RequestMapping(REPLY_SERVICE_API_ROOT_PATH)
public class ReplyReactionCUD {
    private final ReplyReactionRepository replyReactionRepository;

    @Autowired
    public ReplyReactionCUD(ReplyReactionRepository replyReactionRepository) {
        this.replyReactionRepository = replyReactionRepository;
    }

    @PostMapping("/{replyID}/like")
    public SingleMessageResponse like(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long replyID,
            @RequestBody @Valid AddReplyReactionRequest request
    ) {
        replyReactionRepository.addAndRemovePreviousOnes(userDescriptor.getUserID(), replyID, request.commentID, Reaction.LIKE);

        return SingleMessageResponse.OK;
    }

    @PostMapping("/{replyID}/dislike")
    public SingleMessageResponse dislike(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long replyID,
            @RequestBody @Valid AddReplyReactionRequest request
    ) {
        replyReactionRepository.addAndRemovePreviousOnes(userDescriptor.getUserID(), request.commentID, replyID, Reaction.DISLIKE);

        return SingleMessageResponse.OK;
    }

    @DeleteMapping("/{replyID}/reactions")
    public SingleMessageResponse removeReactions(
            @RequestAttribute("user") UserDescriptor userDescriptor,
            @PathVariable long replyID
    ) {
        boolean existed = replyReactionRepository.removeIfExists(userDescriptor.getUserID(), replyID);

        if (existed) return SingleMessageResponse.OK;
        else throw SingleErrorMessageException.DOES_NOT_EXIST;
    }

}