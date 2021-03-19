package com.example.Experiment_To_The_Moon;

public class Post {

        private String UserID;
        private String Body;
        boolean isQuestion;
        private int Parent;



        public Post(String UserID, String body, Boolean isQuestion, int parent) {
            this.UserID = UserID;
            this.Body = body;
            this.isQuestion=isQuestion;
            this.Parent=parent;
        }

        public Post(String userID, String body, boolean isQuestion) {
            UserID = userID;
            Body = body;
            this.isQuestion = isQuestion;

        }

        public int getParent() {
            return Parent;
        }

        public void setParent(int parent) {
            Parent = parent;
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

