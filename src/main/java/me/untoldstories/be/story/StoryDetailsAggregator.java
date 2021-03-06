package me.untoldstories.be.story;

import me.untoldstories.be.comment.CommentInternalAPI;
import me.untoldstories.be.story.pojos.Story;
import me.untoldstories.be.story.repos.StoryReactionRepository;
import me.untoldstories.be.user.UserInternalAPI;
import me.untoldstories.be.user.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StoryDetailsAggregator {
    private final StoryReactionRepository storyReactionRepository;
    private final UserInternalAPI userInternalAPI;
    private final CommentInternalAPI commentInternalAPI;

    @Autowired
    public StoryDetailsAggregator(
            StoryReactionRepository storyReactionRepository,
            UserInternalAPI userInternalAPI,
            CommentInternalAPI commentInternalAPI
    ) {
        this.storyReactionRepository = storyReactionRepository;
        this.userInternalAPI = userInternalAPI;
        this.commentInternalAPI = commentInternalAPI;
    }

    public void fillUpAuthorLikeComment(Story story) {
        story.author.userName = userInternalAPI.fetchUserNameByID(story.author.id).userName;
        story.nLikes = storyReactionRepository.fetchNumOfLikes(story.id);
        story.nComments = commentInternalAPI.fetchNumOfCommentsOfStory(story.id);
    }

    public void fillUpUserReaction(Story story, long userID) {
        story.myReaction = storyReactionRepository.fetchReactionOfUser(story.id, userID);
    }

    public void fillUpAuthorLikeComment(List<Story> stories) {
        if (stories.size() == 0) return;

        StringBuilder sbStoryIDList = new StringBuilder();
        StringBuilder sbUserIDList = new StringBuilder();
        for (Story story : stories) {
            sbStoryIDList.append(story.id).append(',');
            sbUserIDList.append(story.author.id).append(',');
        }
        String storyIDList = sbStoryIDList.substring(0, sbStoryIDList.length() - 1);
        String userIDList = sbUserIDList.substring(0, sbUserIDList.length() - 1);

        Map<Long, User> users = userInternalAPI.fetchUsersByIDs(userIDList);
        Map<Long, Integer> nLikes = storyReactionRepository.fetchNumOfLikes(storyIDList);
        Map<Long, Integer> nComments = commentInternalAPI.fetchNumOfCommentsOfStories(storyIDList);

        for (Story story: stories) {
            story.author.userName = users.get(story.author.id).userName;
            story.nLikes = nLikes.getOrDefault(story.id, 0);
            story.nComments = nComments.getOrDefault(story.id, 0);
        }
    }

    public void fillUpUserReactions(List<Story> stories, long userID) {
        StringBuilder sbStoryIDList = new StringBuilder();
        for (Story story : stories) {
            sbStoryIDList.append(story.id).append(',');
        }
        String storyIDList = sbStoryIDList.substring(0, sbStoryIDList.length() - 1);

        Map<Long, Byte> myReactions = storyReactionRepository.fetchReactionsOfUser(storyIDList, userID);
        for (Story story: stories) {
            story.myReaction = myReactions.getOrDefault(story.id, (byte)0);
        }
    }
}
