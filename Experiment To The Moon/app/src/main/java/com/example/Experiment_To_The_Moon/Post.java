package com.example.Experiment_To_The_Moon;

public class Post {

        private String UserID;
        private String Body;
        boolean isQuestion;
        private String Parent;
        private Integer position;




    public Post(String UserID, String body, Boolean isQuestion,String parent, Integer position) {
            this.UserID = UserID;
            this.Body = body;
            this.isQuestion=isQuestion;
            this.Parent=parent;
            this.position=position;
        }

        public Post(String userID, String body, boolean isQuestion,Integer position) {
            UserID = userID;
            Body = body;
            this.isQuestion = isQuestion;
            this.position=position;
            this.Parent="None";

        }


        public String getParent() {
            return Parent;
        }

        public void setParent(String parent) {
            Parent = parent;
        }


    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public boolean isQuestion() {
            return isQuestion;
        }

        public void setQuestion(boolean question) {
            isQuestion = question;
        }

        public String getUserID() {
            return UserID;
        }

        public void setUserID(String userID) {
            UserID = userID;
        }

        public String getPost() {
            return Body;
        }

        public void setPost(String body) {
            Body = body;
        }
}

