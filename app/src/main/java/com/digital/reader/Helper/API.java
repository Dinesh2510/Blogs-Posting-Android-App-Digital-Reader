package com.digital.reader.Helper;


public interface API {

    //    String BaseURL = "http://192.168.43.207/digital_reader/";
//    String ImageUrl = "http://192.168.43.207/digital_reader/images/";
    String BaseURL = "http://pixeldev.in/webservices/digital_reader";
    String ImageUrl = "http://pixeldev.in/webservices/digital_reader/admin/";


    String LOGIN_URL = BaseURL + "/login_new.php";
    String REGISTER_URL = BaseURL + "/register_new.php";
    String Update_URL = BaseURL + "/Update_user.php";
    String GET_ALL_POST_URL = BaseURL + "/GetAllPostList.php";
    String GET_ALL_TOPIC_URL = BaseURL + "/GetTopicList.php";
    String GET_ALL_User_TOPIC_URL = BaseURL + "/UserFollowTopicList.php";
    String UnFollowTopic_URL = BaseURL + "/UnFollowTopic.php";
    String FollowTopic_URL = BaseURL + "/FollowTopic.php";
    String GET_ALL_TRENDLIST_URL = BaseURL + "/GetAllTrendingList.php";
    String GET_COST = BaseURL + "/GetCost.php";
    String Add_Payment_Data = BaseURL + "/AddPaymentData.php";
    String Get_TopicWise_Post = BaseURL + "/TopicWisePost.php";
    String Get_NewsList = BaseURL + "/GetNewsList.php";
    String Send_Like = BaseURL + "/like.php";
    String BookMarkPost = BaseURL + "/AddToBookmark.php";
    String CheckBookMark = BaseURL + "/CheckBookMark.php";
    String SendActiveFlag = BaseURL + "/SendActiveFlag.php";
    String GetListOfBookmark = BaseURL + "/GetListOfBookmark.php";
    String DeleteBookmarkPost = BaseURL + "/DeleteBookmarkPost.php";
    String Email_Templete = BaseURL + "/email_temp.php";
    String Send_EMAIL = BaseURL + "/mail.php";
    String Get_Verification_Code = BaseURL + "/GetVerificationCode.php";
    String Get_Count_Update = BaseURL + "/count.php";
    String GetCommentList = BaseURL + "/GetCommentList.php";
    String GETSearchData = BaseURL + "/searchkey.php";
    String SendEmailCode = BaseURL + "/verifyEmailCode.php";


}