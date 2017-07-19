package com.linked_sys.hns.network;

public class ApiEndPoints {

    public static final String BASE_URL = "http://hns.edu.sa";

    //http://hns.edu.sa
    //http://hns.linked-sys.com

    public static final String GET_TOKEN = "/token"; //POST
    public static final String SEND_FB_TOKEN = "/api/Mail/SetDeviceToken"; //POST
    public static final String REMOVE_FB_TOKEN = "/api/Mail/RemoveToken"; //POST
    public static final String GET_USER_DATA = "/api/Parent/GetUserData/"; //GET
    public static final String ADD_SON = "/api/Parent/AddStudentToParent/"; //POST
    public static final String TIME_TABLE = "/api/TimeTable/GetTimeTable/"; //GET
    public static final String TEACHER_TIME_TABLE = "/api/TimeTable/GetTeacherTimeTable/"; //GET
    public static final String WEEK_PLAN = "/api/WeekPlan/GetWeekPlan/"; //GET
    public static final String SELECTED_WEEK = "/api/WeekPlan/GetWeekPlanByWeekID/"; //GET
    public static final String PLAN_BODY = "/api/WeekPlan/GetStudentWeekPlan/"; //GET
    public static final String ADD_COMMENT = "/api/WeekPlan/AddParentNote/"; //POST

    //EMAIL
    public static final String GET_INBOX = "/api/Mail/GetInbox/"; //GET
    public static final String GET_OUTBOX = "/api/Mail/GetOutbox/"; //GET
    public static final String GET_TRASH = "/api/Mail/GetTrash/"; //GET
    public static final String GET_MSG_BODY = "/api/Mail/GetMailBody/"; //GET
    public static final String DELETE_MSG = "/api/Mail/DeleteMail/"; //GET
    public static final String DELETE_MSG_FROM_TRASH = "/api/Mail/DeleteMailFromTrash/"; // POST
    public static final String MARK_MSG_UNREAD = "/api/Mail/IsRead/"; //POST
    public static final String SEND_MSG = "/api/Mail/ComposeNewMail"; //POST
    public static final String NEW_MSG_COUNT = "/api/Mail/CountUnReadMails/"; //GET
    public static final String GET_TEACHERS = "/api/Mail/GetClassTeachers/"; //GET
    public static final String GET_STAFF = "/api/Mail/GetMailCCData/"; //GET
    public static final String GET_ALL_TEACHERS = "/api/Teacher/GetTeachersBySchoolId/"; //GET

    public static final String GET_STAGES = "/api/Mail/GetSchoolStages/"; //GET
    public static final String GET_CLASSES = "/api/Mail/GetClassesByStage/"; //GET
    public static final String GET_STUDENTS = "/api/Mail/GetStudentsByClass/"; //GET

    //TASKS
    public static final String GET_TASKS = "/api/WeekPlan/GetStudentTasksNotDone/"; //GET

    public static final String GET_DASHBOARD = "/api/Student/DashBoard/"; //GET StudentID
    public static final String GET_LESSONS = "/api/Student/Lessons/"; //GET


}
