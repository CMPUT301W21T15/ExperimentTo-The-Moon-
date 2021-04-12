package com.example.Experiment_To_The_Moon;

/**
 * This class represents a post in the Q&A
 */
public class Post {

        private String UserID;
        private String Body;
        boolean isQuestion;
        private String Parent;
        private Integer position;


    /**
     * Constructor for answer
     * @param UserID
     * UID of the user posting the answer
     * @param body
     * post text
     * @param isQuestion
     * whether the post is a question or not
     * @param parent
     * The identifier of the question the answer answers in the database
     * @param position
     * The identifier of the answer in the database
     */
    public Post(String UserID, String body, Boolean isQuestion,String parent, Integer position) {
            this.UserID = UserID;
            this.Body = body;
            this.isQuestion=isQuestion;
            this.Parent=parent;
            this.position=position;
        }

    /**
     * Constructor for question
     * @param userID
     * UID of the user posting the question
     * @param body
     * post text
     * @param isQuestion
     * whether the post is a question or not
     * @param position
     * The identifier of the question in the database
     */
        public Post(String userID, String body, boolean isQuestion,Integer position) {
            UserID = userID;
            Body = body;
            this.isQuestion = isQuestion;
            this.position=position;
            this.Parent="None";

        }


    /**
     *
     * @return
     * question that answer belongs to
     */
    public String getParent() {
            return Parent;
        }

    /**
     *
     * @param parent
     * post to set parent as
     */
    public void setParent(String parent) {
            Parent = parent;
        }

    /**
     * @deprecated
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @deprecated
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     *
     * @return
     * whether a post is a question or not
     */
    public boolean isQuestion() {
            return isQuestion;
        }

    /**
     * @deprecated
     */
    public void setQuestion(boolean question) {
            isQuestion = question;
        }

    /**
     *
     * @return
     * UID of post
     */
    public String getUserID() {
            return UserID;
        }

    /**
     * @deprecated
     */
        public void setUserID(String userID) {
            UserID = userID;
        }

    /**
     *
     * @return
     * the post's text
     */
    public String getPost() {
            return Body;
        }
    /**
     * @deprecated
     */
        public void setPost(String body) {
            Body = body;
        }
}

